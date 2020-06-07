package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.assertThrows;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class PointSizePropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() throws NotApplicablePropertyException {
		GeoElement point = addAvInput("(1,2)");
		new PointSizeProperty(point);
	}

	@Test
	public void testConstructorThrowsError() {
		GeoElement f = addAvInput("f: x");
		assertThrows(NotApplicablePropertyException.class, () -> new PointSizeProperty(f));
	}
}