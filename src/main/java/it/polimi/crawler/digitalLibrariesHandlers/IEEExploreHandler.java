package it.polimi.crawler.digitalLibrariesHandlers;

import it.polimi.masAPI.exceptions.MissingInformationException;
import it.polimi.webClient.DownloadException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;

public class IEEExploreHandler extends DigitalLibraryHandler {
	private static final Pattern fullTextLink = Pattern.compile("<frame src=\"http://ieeexplore.ieee.org(/[^\"]*)\" frameborder=0 />");

	public IEEExploreHandler() {
		super(new HttpHost("ieeexplore.ieee.org"), "http://(dx\\.doi\\.org|doi\\.ieeecomputersociety\\.org)(/\\d+\\.\\d+/([A-Z]+\\.)?\\d+\\.(\\d+))");
	}

	private static final String ABSTRACT_TAG = "<h2>Abstract</h2></a>";

	@Override
	public String downloadAbstract() throws DownloadException, IOException, MissingInformationException {
		if (getArticleID() == null) {
			throw new MissingInformationException("Identifier not found");
		}
		String pageURL = "/xpls/abs_all.jsp?arnumber=" + getArticleID();
		String dataPage = getPage(pageURL);
		if (dataPage.contains(ABSTRACT_TAG)) {
			int startIndex = dataPage.indexOf(ABSTRACT_TAG);
			startIndex = dataPage.indexOf("<p>", startIndex) + 3;
			int endIndex = dataPage.indexOf("</p>", startIndex);
			String abstractText = dataPage.substring(startIndex, endIndex);
			return abstractText.replace("\n", " ").replaceAll("<[^<]+>", "").trim();
		} else {
			throw new MissingInformationException("Abstract not found for article http://ieeexplore.ieee.org" + pageURL);
		}
	}

	@Override
	public byte[] downloadFullText() throws DownloadException, IOException, MissingInformationException {
		if (getArticleID() == null) {
			throw new MissingInformationException("Identifier not found");
		}
		String intermediatePage = getPage("/stamp/stamp.jsp?tp=&arnumber=" + getArticleID());
		Matcher fullTextLinkMatcher = fullTextLink.matcher(intermediatePage);
		if (fullTextLinkMatcher.find()) {
			return getBinaryData(fullTextLinkMatcher.group(1));
		} else {
			throw new MissingInformationException("FullText link not found");
		}
	}

	private static final HttpHost doiHost = new HttpHost("dx.doi.org");
	private static final Pattern articleIDPattern = Pattern.compile("<meta name=\"citation_abstract_html_url\" content=\"http://ieeexplore.ieee.org/xpls/abs_all.jsp\\?arnumber=(\\d+)\">");

	@Override
	public boolean canHandle(String digitalLibraryLink) {
		if (digitalLibraryLink == null) {
			return false;
		}
		Matcher URLMatcher = URLPattern.matcher(digitalLibraryLink);
		if (URLMatcher.matches()) {
			try {
				String articlePage = downloader.getPage(doiHost, URLMatcher.group(2));
				Matcher articleIDMatcher = articleIDPattern.matcher(articlePage);
				if (articleIDMatcher.find()) {
					setArticleID(articleIDMatcher.group(1));
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}
}
