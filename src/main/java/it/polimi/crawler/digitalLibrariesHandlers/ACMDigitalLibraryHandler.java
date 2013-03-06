package it.polimi.crawler.digitalLibrariesHandlers;

import it.polimi.masAPI.exceptions.MissingInformationException;
import it.polimi.webClient.DownloadException;

import java.io.IOException;
import java.util.regex.Matcher;

import org.apache.http.HttpHost;

public class ACMDigitalLibraryHandler extends DigitalLibraryHandler {
	private static final String ABSTRACT_TAG = "<div style=\"display:inline\">";

	public ACMDigitalLibraryHandler() {
		super(new HttpHost("dl.acm.org"), "http://(doi\\.acm\\.org/10\\.1145/|portal\\.acm\\.org/citation\\.cfm\\?id=)(\\d+\\.)?(\\d+)");
	}

	@Override
	public boolean canHandle(String digitalLibraryLink) {
		if (digitalLibraryLink == null) {
			return false;
		}
		Matcher URLMatcher = URLPattern.matcher(digitalLibraryLink);
		if (URLMatcher.matches()) {
			setArticleID(URLMatcher.group(3));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String downloadAbstract() throws DownloadException, IOException, MissingInformationException {
		if (getArticleID() == null) {
			throw new MissingInformationException("Identifier not found");
		}
		String dataPage = getPage("/tab_abstract.cfm?id=" + getArticleID());

		int startIndex = dataPage.indexOf(ABSTRACT_TAG) + ABSTRACT_TAG.length();
		int endIndex = dataPage.indexOf("</div>", startIndex);
		String abstractText = dataPage.substring(startIndex, endIndex);
		return abstractText.replace("\n", " ").replaceAll("<[^<]+>", "").trim();
	}

	@Override
	public byte[] downloadFullText() throws DownloadException, IOException, MissingInformationException {
		if (getArticleID() == null) {
			throw new MissingInformationException("Identifier not found");
		}
		return getBinaryData("/ft_gateway.cfm?id=" + getArticleID() + "&type=pdf");
	}
}
