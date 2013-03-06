package it.polimi.classification;

import static org.junit.Assert.assertTrue;
import it.polimi.analysis.analyzers.NoCitationsException;
import it.polimi.analysis.analyzers.PaperClassifier;
import it.polimi.data.hibernate.entities.Article;

import org.junit.Test;

public class PaperClassifierTest {

	@Test
	public void testLabeling() throws NoCitationsException {
		PaperClassifier classifier = new PaperClassifier();
		Article article = classifier.getArticle(160L);
		assertTrue(classifier.getTopicProfile(article).getDistribution().containsKey("REVERSE ENGINEERING"));
	}
}
