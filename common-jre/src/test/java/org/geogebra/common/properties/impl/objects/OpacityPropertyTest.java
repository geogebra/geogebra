package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.junit.Test;

public class OpacityPropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorForPolygon() {
		addAvInput("A = (0, 0)");
		addAvInput("B = (1, 1)");
		addAvInput("C = (0, 1)");
		GeoElement polygon = addAvInput("Polygon(A,B,C)");
		try {
			new OpacityProperty(getLocalization(), polygon);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testConstructorForSlider() {
		GeoElement slider = addAvInput("1");
		slider.setEuclidianVisible(true);
		assertThrows(NotApplicablePropertyException.class,
				() -> new OpacityProperty(getLocalization(), slider));
	}

	@Test
	public void testConstructorForPoint() {
		GeoElement point = addAvInput("(1,2)");
		assertThrows(NotApplicablePropertyException.class,
				() -> new OpacityProperty(getLocalization(), point));
	}
}