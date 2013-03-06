package it.polimi.crawler.digitalLibrariesHandlers;

import it.polimi.masAPI.exceptions.MissingInformationException;
import it.polimi.webClient.DownloadException;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class DefaultHandler extends DigitalLibraryHandler {
	private static final Logger logger = Logger.getLogger("it.polimi.crawling.DBLPDataPersister");

	public DefaultHandler() {
		super(null, (Pattern) null);

	}

	@Override
	public boolean canHandle(String digitalLibraryLink) {
		logger.warn("Using default handler. Digital libraries information for " + digitalLibraryLink + " will be skipped");
		return true;
	}

	@Override
	public String downloadAbstract() throws DownloadException, IOException, MissingInformationException {
		return "";
	}

	@Override
	public byte[] downloadFullText() throws DownloadException, IOException, MissingInformationException {
		return new byte[0];
	}

}
