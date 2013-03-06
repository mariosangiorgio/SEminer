package it.polimi.crawler.digitalLibrariesHandlers;

import it.polimi.masAPI.exceptions.MissingInformationException;
import it.polimi.webClient.ContentDownloader;
import it.polimi.webClient.DownloadException;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;

public abstract class DigitalLibraryHandler {
	protected final ContentDownloader downloader = new ContentDownloader();
	private final HttpHost targetHost;

	protected final Pattern URLPattern;
	private String articleID;

	protected DigitalLibraryHandler(HttpHost targetHost, Pattern URLPattern) {
		this.URLPattern = URLPattern;
		this.targetHost = targetHost;
	}

	protected DigitalLibraryHandler(HttpHost targetHost, String digitalLibraryURL) {
		URLPattern = Pattern.compile(digitalLibraryURL);
		this.targetHost = targetHost;
	}

	protected String getArticleID() {
		return articleID;
	}

	protected void setArticleID(String articleID) {
		this.articleID = articleID;
	}

	public abstract boolean canHandle(String digitalLibraryLink);

	public abstract String downloadAbstract() throws DownloadException, IOException, MissingInformationException;

	public abstract byte[] downloadFullText() throws DownloadException, IOException, MissingInformationException;

	protected String getPage(String address) throws DownloadException, IOException {
		return downloader.getPage(targetHost, address);
	}

	protected byte[] getBinaryData(String address) throws DownloadException, IOException {
		return downloader.getBinaryData(targetHost, address);
	}
}
