package org.geogebra.common.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class XMLStringBuilderTest {

	XMLStringBuilder builder = new XMLStringBuilder();

	@Test
	public void testEmptyTag() {
		builder.startTag("a", 0).attr("b", "c").endTag();
		assertEquals("<a b=\"c\"/>\n", builder.toString());
	}

	@Test
	public void testOpeningTag() {
		builder.startOpeningTag("p", 0).endTag();
		builder.startTag("a").attr("b", "c").endTag();
		builder.closeTag("p");
		assertEquals("<p>\n\t<a b=\"c\"/>\n</p>\n", builder.toString());
	}
}
