package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.assertThrows;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class PointStylePropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() {
		GeoElement point = addAvInput("(1,2)");
		new PointStyleProperty(point);
	}

	@Test
	public void testConstructorThrowsError() {
		GeoElement f = addAvInput("f: x");
		assertThrows(NotApplicablePropertyException.class, () -> new PointStyleProperty(f));
	}
}