/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */
 
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
