package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class GeoEmbedTest extends BaseUnitTest {

	@Test
	public void testGetXmlEncodesUrl() {
		GeoEmbed embed = new GeoEmbed(getConstruction());
		embed.setUrl("https://www.example.com?param1=true&param2=false");

		StringBuilder builder = new StringBuilder();
		embed.getXMLtags(builder);

		assertTrue(builder.toString().matches("(?s).*true&amp;param2.*"));
	}
}
