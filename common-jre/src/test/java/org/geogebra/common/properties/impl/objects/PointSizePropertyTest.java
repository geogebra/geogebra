package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.junit.Test;

public class PointSizePropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() {
		GeoElement point = addAvInput("(1,2)");
		try {
			new PointSizeProperty(getLocalization(), point);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testConstructorThrowsError() {
		GeoElement f = addAvInput("f: x");
		assertThrows(NotApplicablePropertyException.class,
				() -> new PointSizeProperty(getLocalization(), f));
	}
}