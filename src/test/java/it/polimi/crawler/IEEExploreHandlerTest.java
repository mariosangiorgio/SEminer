package it.polimi.crawler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it.polimi.crawler.digitalLibrariesHandlers.DigitalLibraryHandler;
import it.polimi.crawler.digitalLibrariesHandlers.IEEExploreHandler;
import it.polimi.masAPI.exceptions.MissingInformationException;
import it.polimi.webClient.DownloadException;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;

public class IEEExploreHandlerTest {
	private DigitalLibraryHandler handler = new IEEExploreHandler();

	@Test
	public void testUrlMatching() {
		assertTrue(handler.canHandle("http://doi.ieeecomputersociety.org/10.1109/32.21720"));
		assertTrue(handler.canHandle("http://doi.ieeecomputersociety.org/10.1109/TSE.2003.1265525"));
		assertTrue(handler.canHandle("http://doi.ieeecomputersociety.org/10.1109/TSE.2004.100"));
		assertTrue(handler.canHandle("http://dx.doi.org/10.1109/TSE.2007.256941"));
	}

	@Test
	public void testAbstract() throws MissingInformationException, DownloadException, IOException {
		handler.canHandle("http://doi.ieeecomputersociety.org/10.1109/32.21720");
		assertEquals(
				"Significant modifications of the first-order rules have been developed so that they can be applied directly to algebraic expressions. The importance and implication of normalization of formulas in any theorem prover are discussed. It is shown how the properties of the domain of discourse have been taken care of either by the normalizer or by the inference rules proposed. Using a nontrivial example, the following capabilities of the verifier that would use these inference rules are highlighted: (1) closeness of the proof construction process to the human thought process; and (2) efficient handling of user provided axioms. Such capabilities make interfacing with humans easy",
				handler.downloadAbstract());

		handler.canHandle("http://doi.ieeecomputersociety.org/10.1109/32.153383");
		assertEquals(
				"The authors investigate the dynamic scheduling of tasks with well-defined timing constraints. They present a dynamic uniprocessor scheduling algorithm with an O(n log n) worst-case complexity. The preemptive scheduling performed by the algorithm is shown to be of higher efficiency than that of other known algorithms. Furthermore, tasks may be related by precedence constraints, and they may have arbitrary deadlines and start times (which need not equal their arrival times). An experimental evaluation of the algorithm compares its average case behavior to the worst case. An analytic model used for explanation of the experimental results is validated with actual system measurements. The dynamic scheduling algorithm is the basis of a real-time multiprocessor operating system kernel developed in conjunction with this research. Specifically, this algorithm is used at the lowest, threads-based layer of the kernel whenever threads are created",
				handler.downloadAbstract());

		handler.canHandle("http://doi.ieeecomputersociety.org/10.1109/TSE.2003.1265525");
		assertEquals(
				"We present a framework for testing applications for mobile computing devices. When a device is moved into and attached to a new network, the proper functioning of applications running on the device often depends on the resources and services provided locally in the current network. This framework provides an application-level emulator for mobile computing devices to solve this problem. Since the emulator is constructed as a mobile agent, it can carry applications across networks on behalf of its target device and allow the applications to connect to local servers in its current network in the same way as if they had been moved with and executed on the device itself. This paper also demonstrates the utility of this framework by describing the development of typical network-dependent applications in mobile and ubiquitous computing settings.",
				handler.downloadAbstract());

		handler.canHandle("http://doi.ieeecomputersociety.org/10.1109/32.56097");
		assertEquals(
				"Remote evaluation (REV) is a construct for building distributed systems that involves sending executable code from one computer to another computer via a communication network. How REV can reduce communication and improve performance for certain classes of distributed applications is explained. Implementation issues are discussed. REV is incorporated into a high-level programming language by defining its syntax and its semantics. The compile-time and run-time support for REV is discussed in both heterogeneous and homogeneous systems and compared to that needed by a remote procedure call implementation. Sample performance measurements are included. Experience with a prototype REV implementation is summarized",
				handler.downloadAbstract());
	}

	@Test(expected = MissingInformationException.class)
	public void testMissingAbstract() throws DownloadException, IOException, MissingInformationException {
		handler.canHandle("http://doi.ieeecomputersociety.org/10.1109/TSE.1982.235729");
		handler.downloadAbstract();
	}

	@Test
	public void testFullText() throws DownloadException, IOException, MissingInformationException {
		handler.canHandle("http://doi.ieeecomputersociety.org/10.1109/32.21720");
		byte[] fullText = handler.downloadFullText();
		byte[] buffer = new byte[fullText.length];
		FileInputStream sampleFile = new FileInputStream("test-resources/00021720.pdf");
		sampleFile.read(buffer);
		sampleFile.close();
		for (int i = 0; i < fullText.length; i++) {
			assertEquals(buffer[i], fullText[i]);
		}
	}

	@Test
	public void testMissingFullTextLink() throws DownloadException, IOException, MissingInformationException {
		handler.canHandle("http://doi.ieeecomputersociety.org/10.1109/TSE.2004.100");
		byte[] fullText = handler.downloadFullText();
		byte[] buffer = new byte[fullText.length];
		FileInputStream sampleFile = new FileInputStream("test-resources/01377190.pdf");
		sampleFile.read(buffer);
		sampleFile.close();
		for (int i = 0; i < fullText.length; i++) {
			assertEquals(buffer[i], fullText[i]);
		}
	}
}
