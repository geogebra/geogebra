package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.junit.Test;

public class CaptionStylePropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() {
		GeoElement f = addAvInput("f: x");
		try {
			new CaptionStyleProperty(getLocalization(), f);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testConstructorThrowsError() {
		getApp().setGraphingConfig();
		GeoText text = addAvInput("\"\"");
		assertThrows(NotApplicablePropertyException.class,
				() -> new CaptionStyleProperty(getLocalization(), text));
	}
}