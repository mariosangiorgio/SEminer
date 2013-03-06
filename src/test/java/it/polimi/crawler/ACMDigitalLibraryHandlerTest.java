package it.polimi.crawler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it.polimi.crawler.digitalLibrariesHandlers.ACMDigitalLibraryHandler;
import it.polimi.masAPI.exceptions.MissingInformationException;
import it.polimi.webClient.DownloadException;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;

public class ACMDigitalLibraryHandlerTest {
	private ACMDigitalLibraryHandler handler = new ACMDigitalLibraryHandler();

	@Test
	public void testUrlMatching() {
		assertTrue(handler.canHandle("http://doi.acm.org/10.1145/504087.504088"));
		assertTrue(handler.canHandle("http://portal.acm.org/citation.cfm?id=227726.227839"));
		assertTrue(handler.canHandle("http://doi.acm.org/10.1145/1134368"));
	}

	@Test
	public void testAbstract() throws DownloadException, IOException, MissingInformationException {
		handler.canHandle("http://doi.acm.org/10.1145/504087.504088");
		assertEquals(
				"The Unified Modeling Language (UML) is a family of design notations that is rapidly becoming a de facto standard software design language. UML provides a variety of useful capabilities to the software designer, including multiple, interrelated design views, a semiformal semantics expressed as a UML meta model, and an associated language for expressing formal logic constraints on design elements. The primary goal of this work is an assessment of UML's expressive power for modeling software architectures in the manner in which a number of existing software architecture description languages (ADLs) model architectures. This paper presents two strategies for supporting architectural concerns within UML. One strategy involves using UML \"as is,\" while the other incorporates useful features of existing ADLs as UML extensions. We discuss the applicability, strengths, and weaknesses of the two strategies. The strategies are applied on three ADLs that, as a whole, represent a broad cross-section of present-day ADL capabilities. One conclusion of our work is that UML currently lacks support for capturing and exploiting certain architectural concerns whose importance has been demonstrated through the research and practice of software architectures. In particular, UML lacks direct support for modeling and exploiting architectural styles, explicit software connectors, and local and global architectural constraints.",
				handler.downloadAbstract());
	}

	@Test
	public void testFullText() throws DownloadException, IOException, MissingInformationException {
		handler.canHandle("http://doi.acm.org/10.1145/504087.504088");
		byte[] fullText = handler.downloadFullText();
		byte[] buffer = new byte[fullText.length];
		FileInputStream sampleFile = new FileInputStream("test-resources/p2-medvidovic.pdf");
		sampleFile.read(buffer);
		sampleFile.close();
		for (int i = 0; i < fullText.length; i++) {
			assertEquals(buffer[i], fullText[i]);
		}
	}
}
