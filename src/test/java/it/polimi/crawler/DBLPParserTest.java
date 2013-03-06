package it.polimi.crawler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it.polimi.crawler.DBLPParser;
import it.polimi.crawler.TestHandler;
import it.polimi.data.hibernate.entities.Article;
import it.polimi.data.hibernate.entities.Author;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

public class DBLPParserTest {
	private DBLPParser parser;
	private TestHandler testHandler = new TestHandler();
	
	@After
	public void tearDown(){
		testHandler.clear();
	}
	
	@Test
	public void testTSE() {
		getPapersOfVenue("IEEE Trans. Software Eng.");
		List<Article> articles = testHandler.getArticles();
		assertEquals(2,articles.size());

		Article article = articles.get(0);
		assertEquals("Specification Techniques for Data Abstractions.",article.getTitle());
		
		article = articles.get(1);
		assertEquals("A Physical Database Design Evaluation System for CODASYL Databases.",article.getTitle());
		assertEquals("http://doi.ieeecomputersociety.org/10.1109/32.42741",article.getDigitalLibraryLink());
		assertEquals("10.1109/32.42741",article.getDOI());
	}
	
	@Test
	public void testICSE() {
		getPapersOfVenue("ICSE");
		List<Article> articles = testHandler.getArticles();
		assertEquals(1,articles.size());
		Article article = articles.get(0);
		assertEquals("Using Off-the-Shelf Middleware to Implement Connectors in Distributed Software Architectures.",article.getTitle());
	}
	
	@Test
	public void testTOSEM() {
		getPapersOfVenue("ACM Trans. Softw. Eng. Methodol.");
		List<Article> articles = testHandler.getArticles();
		assertEquals(1,articles.size());
		Article article = articles.get(0);
		assertEquals("A comprehensive approach for the development of modular software architecture description languages.",article.getTitle());
		assertEquals(3,article.getAuthors().size());
		Set<String> authorNames = new HashSet<String>();
		for(Author author : article.getAuthors()){
			authorNames.add(author.getName());
		}
		assertTrue(authorNames.contains("Eric M. Dashofy"));
		assertTrue(authorNames.contains("Andr\u00E9 van der Hoek"));
		assertTrue(authorNames.contains("Richard N. Taylor"));
	}
	
	
	private void getPapersOfVenue(String venue) {
		Set<String> venuesToKeep = new HashSet<String>();
		venuesToKeep.add(venue);
		parser = new DBLPParser(venuesToKeep,testHandler);
		parser.parse("test-resources/dblp-sample.xml");
	}
}
