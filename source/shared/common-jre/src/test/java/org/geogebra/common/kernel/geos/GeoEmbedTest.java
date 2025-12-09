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

package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.io.XMLStringBuilder;
import org.junit.Test;

public class GeoEmbedTest extends BaseUnitTest {

	@Test
	public void testGetXmlEncodesUrl() {
		GeoEmbed embed = new GeoEmbed(getConstruction());
		embed.setUrl("https://www.example.com?param1=true&param2=false");

		XMLStringBuilder builder = new XMLStringBuilder();
		embed.getXMLTags(builder);

		assertTrue(builder.toString().matches("(?s).*true&amp;param2.*"));
	}
}
