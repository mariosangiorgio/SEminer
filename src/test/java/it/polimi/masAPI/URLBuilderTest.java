package it.polimi.masAPI;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class URLBuilderTest {

	@Test
	public void testURLBuilder() {
		URLBuilder urlBuilder = new URLBuilder("ID", ResultObjectType.Author);
		urlBuilder.addParameter("TitleQuery", "TITLE");
		urlBuilder.addParameter("AuthorQuery", "NAME");
		urlBuilder.setLimits(1, 20);
		assertEquals("/json.svc/search?AppId=ID&ResultObjects=Author&TitleQuery=TITLE&AuthorQuery=NAME&StartIdx=1&EndIdx=20", urlBuilder.buildParamsUrl());
	}
}
