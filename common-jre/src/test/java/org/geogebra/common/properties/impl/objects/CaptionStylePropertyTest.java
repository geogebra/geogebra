package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.assertThrows;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.junit.Test;

public class CaptionStylePropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() throws NotApplicablePropertyException {
		GeoElement f = addAvInput("f: x");
		new CaptionStyleProperty(f);
	}

	@Test
	public void testConstructorThrowsError() {
		getApp().setGraphingConfig();
		GeoText text = addAvInput("\"\"");
		assertThrows(NotApplicablePropertyException.class, () -> new CaptionStyleProperty(text));
	}
}