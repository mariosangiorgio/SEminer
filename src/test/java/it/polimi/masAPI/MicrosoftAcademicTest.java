package it.polimi.masAPI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it.polimi.data.hibernate.entities.Affiliation.Location;
import it.polimi.data.hibernate.entities.Article;
import it.polimi.data.hibernate.entities.CitationInformation;
import it.polimi.data.hibernate.entities.Venue;
import it.polimi.masAPI.data.AuthorInformation;
import it.polimi.masAPI.data.PaperAuthorsInformation;
import it.polimi.masAPI.exceptions.CrawlingException;
import it.polimi.masAPI.exceptions.MissingInformationException;

import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MicrosoftAcademicTest {
	private String AppID = "645D91ABB81441946F33514A53DC61F574D7B3CE";
	private MicrosoftAcademic msAcademic = new MicrosoftAcademic(AppID);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getAuthorsOfAPaper() throws CrawlingException, MissingInformationException {
		PaperAuthorsInformation authors;
		AuthorInformation authorInformation;

		authors = msAcademic.getAuthorsInformation("Run-time efficient probabilistic model checking");
		authorInformation = authors.findCompatibleAuthors("Carlo Ghezzi");
		assertEquals("Carlo", authorInformation.getFirstName());
		assertEquals("Ghezzi", authorInformation.getLastName());
		assertEquals("Politecnico di Milano", authorInformation.getAffiliation());

		authors = msAcademic.getAuthorsInformation("Dynamic QoS Management and Optimization in Service-Based Systems");
		authorInformation = authors.findCompatibleAuthors("Giordano Tamburrelli");
		assertEquals("Giordano", authorInformation.getFirstName());
		assertEquals("Tamburrelli", authorInformation.getLastName());
		assertEquals("Politecnico di Milano", authorInformation.getAffiliation());

		authors = msAcademic.getAuthorsInformation("Workshop on flexible modeling tools: (FlexiTools 2011)");
		authorInformation = authors.findCompatibleAuthors("Andr\u00E9 van der Hoek");
		assertEquals("Andr\u00E9", authorInformation.getFirstName());
		assertEquals("Hoek", authorInformation.getLastName());
		assertEquals("University of California Irvine", authorInformation.getAffiliation());
	}

	@Test
	public void getLocationTest() throws CrawlingException, MissingInformationException {
		Location location = msAcademic.getLocationByAffiliation("Politecnico di Milano");
		assertEquals("Europe", location.toString());
	}

	@Test(expected = MissingInformationException.class)
	public void testMissingLocationInformation() throws MissingInformationException, CrawlingException {
		msAcademic.getLocationByAffiliation("Cistel Technology Inc");
	}

	@Test(expected = MissingInformationException.class)
	public void testMissingLocationInformation2() throws CrawlingException, MissingInformationException {
		msAcademic.getLocationByAffiliation("National Institute of Informatics");
	}

	@Test
	public void getCitationsTest() throws CrawlingException {
		Article article = new Article();
		article.setTitle("The Software Development System");
		article.setVenue(new Venue("IEEE Trans. Software Eng."));
		article.setYear(1977);
		
		Set<CitationInformation> citations = msAcademic.getCitations(article);
		assertTrue(citations.size() > 50);
	}

	@Test
	public void getPaperIDTest() throws MissingInformationException, CrawlingException{
		int paperID = msAcademic.getPaperID("The Software Development System", "IEEE Trans. Software Eng.", 1977);
		assertEquals(767715, paperID);
	}

}
