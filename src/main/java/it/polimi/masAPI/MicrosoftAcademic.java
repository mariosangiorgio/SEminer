package it.polimi.masAPI;

import it.polimi.data.hibernate.entities.Affiliation;
import it.polimi.data.hibernate.entities.Affiliation.Location;
import it.polimi.data.hibernate.entities.Article;
import it.polimi.data.hibernate.entities.CitationInformation;
import it.polimi.data.hibernate.entities.VenueTopic;
import it.polimi.masAPI.data.AuthorInformation;
import it.polimi.masAPI.data.PaperAuthorsInformation;
import it.polimi.masAPI.exceptions.CrawlingException;
import it.polimi.masAPI.exceptions.MissingInformationException;
import it.polimi.webClient.ContentDownloader;
import it.polimi.webClient.DownloadException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MicrosoftAcademic {
	private static final Logger logger = Logger.getLogger("it.polimi.masAPI.MicrosoftAcademic");

	private final String AppID;
	private static final String URL_BASE = "academic.research.microsoft.com";
	private HttpHost targetHost;
	private ContentDownloader downloader;
	private static final Pattern locationPattern = Pattern.compile("continentID=(.')>(.*?)</a>");

	public MicrosoftAcademic(String AppID) {
		this.AppID = AppID;
		this.targetHost = new HttpHost(URL_BASE);
		this.downloader = new ContentDownloader();
	}

	public Location getLocationByAffiliation(String affiliationName) throws CrawlingException, MissingInformationException {
		try {
			// http://academic.research.microsoft.com/json.svc/search?AppId=3CE&FullTextQuery=Politecnico+Milano&ResultObjects=Organization&StartIdx=1&EndIdx=1
			int affiliationID = this.getAffiliationID(affiliationName);
			String url = "/Organization/" + affiliationID + "/";
			String affiliationPage = downloader.getPage(targetHost, url);
			Matcher matcher = locationPattern.matcher(affiliationPage);
			if (!matcher.find()) {
				throw new MissingInformationException("Location not available for " + affiliationName);
			}
			String locationTitle = matcher.group(2);
			return Affiliation.LocationMap.get(locationTitle);
		} catch (MissingInformationException e) {
			throw e;
		} catch (Exception e) {
			throw new CrawlingException("Error getting location for " + affiliationName, e);
		}
	}

	/**
	 * This method looks for articles citing the paper with the provided title
	 * 
	 * @param paperTitle
	 *            Title of the paper
	 * @return A Set of the DOIs of citing articles
	 * @throws CrawlingException
	 */
	public Set<CitationInformation> getCitations(Article article) throws CrawlingException {
		try {
			// http://academic.research.microsoft.com/json.svc/search?AppId=Your_AppID&PublicationID=694978&ResultObjects=Publication&ReferenceType=Citation&StartIdx=1&EndIdx=10&OrderBy=Year
			Set<CitationInformation> results = new HashSet<CitationInformation>();
			int paperID = this.getPaperID(article.getTitle(), article.getVenue().getName(), article.getYear());
			int previousDim = 0;
			do {
				previousDim = results.size();
				results.addAll(this.getCitations(paperID, 1 + previousDim, 100 + previousDim));
			} while (!(previousDim == results.size()));
			return results;
		} catch (Exception e) {
			throw new CrawlingException("Error retrieving citations for " + article, e);
		}
	}

	private Set<CitationInformation> getCitations(int paperID, int start, int stop) throws DownloadException, IOException, JSONException {
		URLBuilder urlBuilder = new URLBuilder(AppID, ResultObjectType.Publication);
		urlBuilder.addParameter("PublicationID", paperID);
		urlBuilder.addParameter("ReferenceType", "citation");
		urlBuilder.setLimits(start, stop);

		Set<CitationInformation> results = new HashSet<CitationInformation>();
		String paramURL = urlBuilder.buildParamsUrl();
		String authorJSON = downloader.getPage(targetHost, paramURL);
		JSONObject jsonObject = new JSONObject(authorJSON);
		JSONArray citationsArray = jsonObject.getJSONObject("d").getJSONObject("Publication").getJSONArray("Result");
		for (int i = 0; i < citationsArray.length(); ++i) {
			JSONObject citation = citationsArray.getJSONObject(i);
			String citingArticleDOI = citation.getString("DOI");
			String venue;
			int citingArticleYear = citation.getInt("Year");
			if (!"".equals(citingArticleDOI) && (!citation.isNull("Conference") || !citation.isNull("Journal"))) {
				if (!citation.isNull("Conference")) {
					venue = citation.getJSONObject("Conference").getString("FullName");
				} else {
					venue = citation.getJSONObject("Journal").getString("FullName");
				}
				results.add(new CitationInformation(citingArticleDOI, new VenueTopic(venue), citingArticleYear));
			}
		}
		return results;
	}

	private int getAffiliationID(String affiliationName) throws DownloadException, IOException, JSONException, MissingInformationException {
		URLBuilder urlBuilder = new URLBuilder(AppID, ResultObjectType.Organization);
		urlBuilder.addFullTextQuery(affiliationName);
		urlBuilder.setLimits(1, 1);

		String authorJSON = downloader.getPage(targetHost, urlBuilder.buildParamsUrl());
		JSONObject jsonObject = new JSONObject(authorJSON);
		JSONArray results = jsonObject.getJSONObject("d").getJSONObject("Organization").getJSONArray("Result");
		if (results.length() == 0) {
			throw new MissingInformationException("No location information available for " + affiliationName);
		} else {
			int affiliationID = results.getJSONObject(0).getInt("ID");
			return affiliationID;
		}
	}

	private String buildPaperURL(String paperTitle, String venueName, String venueType, int yearOfPublication) {
		URLBuilder urlBuilder = new URLBuilder(AppID, ResultObjectType.Publication);
		urlBuilder.addParameter("TitleQuery", paperTitle);
		urlBuilder.addParameter("YearStart", yearOfPublication);
		urlBuilder.addParameter("YearEnd", yearOfPublication);
		if ("".equals(venueName)) {
			urlBuilder.addParameter(venueType + "Query", venueName);
		}
		urlBuilder.setLimits(1, 1);

		return urlBuilder.buildParamsUrl();
	}

	public int getPaperID(String paperTitle, String venueName, int yearOfPublication) throws MissingInformationException, CrawlingException {
		String[] venueTypes = { "Conference", "Journal" };
		Set<String> urls = new HashSet<String>();
		venueName = MSAcademicVenue.getMSAcademicName(venueName);
		if ("".equals(venueName)) {
			urls.add(buildPaperURL(paperTitle, venueName, "", yearOfPublication));
		} else {
			for (String venueType : venueTypes) {
				urls.add(buildPaperURL(paperTitle, venueName, venueType, yearOfPublication));
			}
		}

		for (String url : urls) {
			try {
				String paperJSON = downloader.getPage(targetHost, url);
				JSONArray results = (new JSONObject(paperJSON)).getJSONObject("d").getJSONObject("Publication").getJSONArray("Result");
				if (results.length() > 0) {
					return results.getJSONObject(0).getInt("ID");
				}
			} catch (Exception e) {
				logger.warn("Exception occurred retrieving paper identifier " + paperTitle, e);
			}
		}
		throw new MissingInformationException("Cannot find identifier for " + paperTitle);
	}

	public PaperAuthorsInformation getAuthorsInformation(String paperTitle) throws CrawlingException {
		try {
			PaperAuthorsInformation result = new PaperAuthorsInformation();

			// json.svc/search?AppId=...&TitleQuery=TITLE&AuthorQuery=NAME&ResultObjects=Author&StartIdx=1&EndIdx=20
			URLBuilder urlBuilder = new URLBuilder(AppID, ResultObjectType.Author);
			urlBuilder.addParameter("TitleQuery", paperTitle);
			urlBuilder.setLimits(1, 20);

			String authorsJSON = downloader.getPage(targetHost, urlBuilder.buildParamsUrl());
			JSONObject jsonObject = new JSONObject(authorsJSON);
			JSONArray authors = jsonObject.getJSONObject("d").getJSONObject("Author").getJSONArray("Result");

			for (int i = 0; i < authors.length(); i++) {
				JSONObject author = authors.getJSONObject(i);
				String firstName = author.getString("FirstName");
				String lastName = author.getString("LastName");
				if (!author.isNull("Affiliation")) {
					String affiliation = author.getJSONObject("Affiliation").getString("Name");
					result.addAuthorInformation(new AuthorInformation(firstName, lastName, affiliation));
				} else {
					result.addAuthorInformation(new AuthorInformation(firstName, lastName));
				}
			}
			// TODO: throw an exception when no value is available
			return result;
		} catch (Exception e) {
			throw new CrawlingException("Error getting authors for " + paperTitle, e);
		}
	}
}
