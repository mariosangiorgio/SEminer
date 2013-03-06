package it.polimi.analysis.analyzers;

import it.polimi.data.hibernate.HibernateSessionManager;
import it.polimi.data.hibernate.entities.Article;
import it.polimi.data.hibernate.entities.TopicProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

public class PaperClassifier {
	private final Session session;

	public PaperClassifier() {
		session = HibernateSessionManager.getNewSession();
	}

	private Map<String, Long> getMostCitingTopics(Article article, String queryName) {
		Query query = session.getNamedQuery(queryName);
		query.setParameter("article", article);
		Map<String, Long> topics = new HashMap<String, Long>();

		@SuppressWarnings("unchecked")
		Iterator<Object[]> resultIterator = (Iterator<Object[]>) query.iterate();

		while (resultIterator.hasNext()) {
			Object[] result = resultIterator.next();
			if (result != null) {
				String venue = (String) result[0];
				Long count = (Long) result[1];
				topics.put(venue, count);
			}
		}

		return topics;
	}

	public Article getArticle(long articleIdentifier) {
		return (Article) session.getNamedQuery("findArticleByID").setParameter("identifier", articleIdentifier).uniqueResult();
	}

	private TopicProfile getArticleTopicProfile(Article article) throws NoCitationsException {
		// External citations
		Map<String, Long> topicsFromExternalCitations = getMostCitingTopics(article, "getTopCitingExternalTopics");
		if (topicsFromExternalCitations.containsKey(null)) {
			topicsFromExternalCitations.remove(null);
		}
		TopicProfile externalCitationsTopicProfile = new TopicProfile(topicsFromExternalCitations);

		// Internal citations
		Collection<TopicProfile> internalCitationTopicProfiles = getTopicProfilesForInternalCitations(article);

		// Merging profiles
		TopicProfile result = externalCitationsTopicProfile;
		for (TopicProfile internalCitationTopicProfile : internalCitationTopicProfiles) {
			result = result.mergeWith(internalCitationTopicProfile);
		}
		if (result.getTotalCitations() == 0) {
			throw new NoCitationsException("No citations for " + article.getTitle());
		} else {
			return result;
		}
	}

	private Collection<TopicProfile> getTopicProfilesForInternalCitations(Article article) {
		Collection<TopicProfile> topicProfiles = new ArrayList<TopicProfile>();
		for (Article citingArticle : article.getCitingArticles()) {
			if (citingArticle.getTopicProfile() != null) {
				topicProfiles.add(citingArticle.getTopicProfile());
			}
		}
		return topicProfiles;
	}

	public TopicProfile getTopicProfile(Article article) throws NoCitationsException {
		int maximumRecursionDepth = 10;
		return getTopicProfile(article, maximumRecursionDepth);
	}

	public TopicProfile getTopicProfile(Article article, int maximumRecursionDepth) throws NoCitationsException {
		if (article.getTopicProfile() != null) {
			return article.getTopicProfile();
		} else {
			computeTopicsForInternalCitations(article, maximumRecursionDepth);
			TopicProfile topicProfile = getArticleTopicProfile(article);
			article.setTopicProfile(topicProfile);
			
			session.beginTransaction();
			session.saveOrUpdate(topicProfile);
			session.saveOrUpdate(article);
			session.getTransaction().commit();
			return topicProfile;
		}
	}

	private void computeTopicsForInternalCitations(Article article, int maximumRecursionDepth) {
		if (maximumRecursionDepth > 0) {
			for (Article citingArticle : article.getCitingArticles()) {
				try {
					getTopicProfile(citingArticle, maximumRecursionDepth - 1);
				} catch (NoCitationsException e) {
					// System.err.println("Internal citation resolution: " +
					// e.getMessage());
				}
			}
		}
	}

	public void getTopicProfilesForAllArticles() {
		Query query = session.getNamedQuery("getAllArticles");
		@SuppressWarnings("unchecked")
		Iterator<Article> articles = query.iterate();
		int failureCounter = 0;
		while (articles.hasNext()) {
			Article article = articles.next();
			try {
				System.out.println(article.getTitle() + "\n" + getTopicProfile(article));
			} catch (NoCitationsException e) {
				System.err.println(e.getMessage());
				failureCounter++;
			}
		}
		System.err.println("Missing citations for " + failureCounter);
	}

	public static void main(String args[]) throws NoCitationsException {
		PaperClassifier classifier = new PaperClassifier();
		classifier.getTopicProfilesForAllArticles();
	}
}