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
