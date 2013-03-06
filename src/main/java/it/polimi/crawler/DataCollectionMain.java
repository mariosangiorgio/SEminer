package it.polimi.crawler;

import it.polimi.crawler.digitalLibrariesHandlers.ACMDigitalLibraryHandler;
import it.polimi.crawler.digitalLibrariesHandlers.DBLPDataPersister;
import it.polimi.crawler.digitalLibrariesHandlers.DefaultHandler;
import it.polimi.crawler.digitalLibrariesHandlers.IEEExploreHandler;

import java.util.HashSet;
import java.util.Set;

public class DataCollectionMain {
	public static void main(String[] args) {
		// Collecting papers
		Set<String> venuesToKeep = new HashSet<String>();
		venuesToKeep.add("IEEE Trans. Software Eng.");
		venuesToKeep.add("ACM Trans. Softw. Eng. Methodol.");
		venuesToKeep.add("ICSE");
		venuesToKeep.add("ASE");
		venuesToKeep.add("SIGSOFT FSE");

		DBLPDataPersister handler = new DBLPDataPersister();
		handler.addContentDownloader(new IEEExploreHandler());
		handler.addContentDownloader(new ACMDigitalLibraryHandler());
		handler.addContentDownloader(new DefaultHandler());

		DBLPParser parser = new DBLPParser(venuesToKeep, handler);
		parser.parse("dblp.xml");

		// Retrieving citations for papers in the database
		handler.getAllCitationInformation(false);
	}
}
