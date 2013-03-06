package it.polimi.crawler.digitalLibrariesHandlers;

import it.polimi.crawler.DBLPParserHandler;
import it.polimi.crawler.exceptions.PersistenceException;
import it.polimi.data.hibernate.HibernateSessionManager;
import it.polimi.data.hibernate.entities.Affiliation;
import it.polimi.data.hibernate.entities.Affiliation.Location;
import it.polimi.data.hibernate.entities.Article;
import it.polimi.data.hibernate.entities.Author;
import it.polimi.data.hibernate.entities.CitationInformation;
import it.polimi.data.hibernate.entities.FullText;
import it.polimi.data.hibernate.entities.Venue;
import it.polimi.data.hibernate.entities.VenueTopic;
import it.polimi.masAPI.MicrosoftAcademic;
import it.polimi.masAPI.data.AuthorInformation;
import it.polimi.masAPI.data.PaperAuthorsInformation;
import it.polimi.masAPI.exceptions.CrawlingException;
import it.polimi.masAPI.exceptions.MissingInformationException;
import it.polimi.webClient.DownloadException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;

public class DBLPDataPersister implements DBLPParserHandler {
	private static final Logger logger = Logger.getLogger("it.polimi.crawling.DBLPDataPersister");
	private Session session;

	private final Set<DigitalLibraryHandler> contentDownloaders = new LinkedHashSet<DigitalLibraryHandler>();
	private static final String AppID = "645D91ABB81441946F33514A53DC61F574D7B3CE";
	// TODO: read AppID from a configuration file
	private static final MicrosoftAcademic msAcademicAPI = new MicrosoftAcademic(AppID);

	public DBLPDataPersister() {
	}

	public void addContentDownloader(DigitalLibraryHandler contentDownloader) {
		contentDownloaders.add(contentDownloader);
	}

	@Override
	public void handle(Article article) {
		logger.info("Processing article\n" + article);
		if (session == null || !session.isOpen()) {
			session = HibernateSessionManager.getNewSession();
		}
		session.beginTransaction();
		try {
			// Checking whether the article is already in the database
			// TODO: add index on DOI column
			String DOI = article.getDOI();
			if (session.getNamedQuery("findArticleByDOI").setParameter("DOI", DOI).uniqueResult() == null) {
				getAdditionalInformation(article);
				persistData(article);
			} else {
				logger.info("Article already in the database\n" + article);
			}
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			logger.warn(e.getMessage());
		} finally {
			session.flush();
			session.clear();
			session.close();
		}
	}

	private void getAdditionalInformation(Article article) throws DownloadException, IOException, PersistenceException, MissingInformationException {
		String digitalLibraryLink = article.getDigitalLibraryLink();
		for (DigitalLibraryHandler contentDownloader : contentDownloaders) {
			if (contentDownloader.canHandle(digitalLibraryLink)) {
				String articleAbstract = contentDownloader.downloadAbstract();
				if (articleAbstract == null) {
					throw new PersistenceException("No abstract found for " + article);
				}
				byte[] fullTextPDF = contentDownloader.downloadFullText();
				if (fullTextPDF == null) {
					throw new PersistenceException("No full text found for " + article);
				}

				article.setArticleAbstract(articleAbstract);
				article.setFullTextPDF(new FullText(fullTextPDF));
				return;
			}
		}
		throw new PersistenceException("No handler found for \n" + article);
	}

	private void persistData(Article article) throws PersistenceException {
		// Replacing venue with the value stored in the database (If any)
		Venue venue = (Venue) session.getNamedQuery("findVenueByName").setParameter("name", article.getVenue().getName()).uniqueResult();
		if (venue != null) {
			article.setVenue(venue);
		}
		// Replacing authors with the values stored in the database (If any)
		getAuthorsAdditionalInformation(article);

		session.saveOrUpdate(article);
	}

	private void getAdditionalInformation(Author author, PaperAuthorsInformation authorsInformation) throws PersistenceException {
		String name = author.getName();
		AuthorInformation authorInformation;
		try {
			authorInformation = authorsInformation.findCompatibleAuthors(name);
			String affiliationName = authorInformation.getAffiliation();
			Affiliation affiliation = (Affiliation) session.getNamedQuery("getAffiliationByTitle").setParameter("title", affiliationName).uniqueResult();
			if (affiliation == null) {
				try {
					Location location = msAcademicAPI.getLocationByAffiliation(affiliationName);
					affiliation = new Affiliation(affiliationName, location);
					session.saveOrUpdate(affiliation);
				} catch (MissingInformationException e) {
					logger.warn("Location not found for " + affiliationName);
					affiliation = new Affiliation(affiliationName, null);
					session.saveOrUpdate(affiliation);
				}
			}
			author.setAffiliation(affiliation);
		} catch (CrawlingException e) {
			throw new PersistenceException("Error downloading author data for " + name, e);
		} catch (MissingInformationException e) {
			logger.warn("Affiliation not found for " + author);
		}
	}

	private void getAuthorsAdditionalInformation(Article article) throws PersistenceException {
		try {
			PaperAuthorsInformation authorsInformation = msAcademicAPI.getAuthorsInformation(article.getTitle());
			Set<Author> persistedAuthors = new HashSet<Author>();
			for (Author author : article.getAuthors()) {
				Author dbAuthor = (Author) session.getNamedQuery("findAuthorByName").setParameter("name", author.getName()).uniqueResult();
				if (dbAuthor != null) {
					persistedAuthors.add(dbAuthor);
				} else {
					getAdditionalInformation(author, authorsInformation);
					persistedAuthors.add(author);
				}
			}
			article.setAuthors(persistedAuthors);
		} catch (Exception e) {
			throw new PersistenceException("Error retrieving data for " + article.getTitle(), e);
		}
	}

	public void getAllCitationInformation(boolean updateIfAlreadyInTheDatabase) {
		logger.info("Retrieving citations");
		if (session == null || !session.isOpen()) {
			session = HibernateSessionManager.getNewSession();
		}
		@SuppressWarnings("unchecked")
		Iterator<Article> articlesIterator = session.getNamedQuery("getAllArticles").iterate();
		while (articlesIterator.hasNext()) {
			Article article = articlesIterator.next();
			logger.info("Retrieving citations for " + article);
			session.beginTransaction();
			try {
				getCitationInformation(article, updateIfAlreadyInTheDatabase);
				session.saveOrUpdate(article);
				session.getTransaction().commit();
				session.flush();
			} catch (RuntimeException e) {
				session.getTransaction().rollback();
				logger.error("Unexpected error retrieving citations for " + article, e);
			}
			session.clear();
		}
		session.close();
	}

	private void lookupVenueTopic(CitationInformation citationInformation) {
		String venueName = citationInformation.getVenue().getVenueName();
		VenueTopic venueTopic = (VenueTopic) session.getNamedQuery("findVenueTopicByVenue").setParameter("venueName", venueName).uniqueResult();
		if (venueTopic != null) {
			citationInformation.setVenue(venueTopic);
		}
	}

	private void getCitationInformation(Article article, boolean updateIfAlreadyInTheDatabase) {
		if (updateIfAlreadyInTheDatabase || (article.getCitingArticles().size() == 0 && article.getAllCitations().size() == 0)) {
			if (updateIfAlreadyInTheDatabase) {
				article.clearCitations();
			}
			try {
				Set<CitationInformation> citations = msAcademicAPI.getCitations(article);
				for (CitationInformation citationInformation : citations) {
					lookupVenueTopic(citationInformation);
					article.addCitationInformation(citationInformation);
					Article citingArticle = (Article) session.getNamedQuery("findArticleByDOI").setParameter("DOI", citationInformation.getDOI()).uniqueResult();
					if (citingArticle != null) {
						article.addCitingArticle(citingArticle);
						logger.info("Match found for article with identifier " + citationInformation.getDOI());
					} else {
						logger.warn("Article with identifier " + citationInformation.getDOI() + " from " + citationInformation.getVenue() + " is not in the database");
					}
				}
			} catch (Exception e) {
				logger.warn("Cannot retrieve citations for\n" + article, e);
			}
		}
	}
}