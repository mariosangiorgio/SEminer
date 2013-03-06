package it.polimi.crawler;

import it.polimi.data.hibernate.entities.Article;
import it.polimi.data.hibernate.entities.Author;
import it.polimi.data.hibernate.entities.Venue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DBLPParser extends DefaultHandler {
	private static final Logger logger = Logger.getLogger("it.polimi.crawling.DBLPParser");

	private SAXParser parser;

	private Article currentArticle;
	private Set<String> venuesToKeep = new HashSet<String>();
	private boolean articleHasToBeKept = false;

	private final DBLPParserHandler handler;

	public DBLPParser(Set<String> venuesToKeep, DBLPParserHandler handler) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		this.venuesToKeep.addAll(venuesToKeep);
		try {
			parser = factory.newSAXParser();
		} catch (Exception e) {
			logger.error("Cannot initialize SAX parser", e);
		}
		this.handler = handler;
	}

	public void parse(String string) {
		try {
			parser.parse(string, this);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private StringBuffer buffer;

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		buffer.append(ch, start, length);
	}

	private String getReadContent() {
		return buffer.toString().replaceAll("\\s+", " ").trim();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		buffer = new StringBuffer();
		if ("article".equals(qName) || "inproceedings".equals(qName)) {
			currentArticle = new Article();
			articleHasToBeKept = false;
			return;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		String readContent = getReadContent();
		if (currentArticle != null) {
			if ("article".equals(qName) || "inproceedings".equals(qName)) {
				if (articleHasToBeKept) {
					handler.handle(currentArticle);
				}
				currentArticle = null;
				return;
			}
			if ("author".equals(qName)) {
				Author author = new Author(readContent);
				currentArticle.addAuthor(author);
				return;
			}
			if ("title".equals(qName)) {
				currentArticle.setTitle(readContent);
				return;
			}
			if ("year".equals(qName)) {
				currentArticle.setYear(Integer.parseInt(readContent));
				return;
			}
			if ("journal".equals(qName) || "booktitle".equals(qName)) {
				articleHasToBeKept = venuesToKeep.contains(readContent);
				currentArticle.setVenue(new Venue(readContent));
				return;
			}
			if ("ee".equals(qName)) {
				currentArticle.setDigitalLibraryLink(readContent);
				return;
			}
		}
	}
}
