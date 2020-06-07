package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.*;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class OpacityPropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorForPolygon() throws NotApplicablePropertyException {
		addAvInput("A = (0, 0)");
		addAvInput("B = (1, 1)");
		addAvInput("C = (0, 1)");
		GeoElement polygon = addAvInput("Polygon(A,B,C)");
		new OpacityProperty(polygon);
	}

	@Test
	public void testConstructorForSlider() throws NotApplicablePropertyException {
		GeoElement slider = addAvInput("1");
		slider.setEuclidianVisible(true);
		new OpacityProperty(slider);
	}

	@Test
	public void testConstructorForPoint() {
		GeoElement point = addAvInput("(1,2)");
		assertThrows(NotApplicablePropertyException.class, () -> new OpacityProperty(point));
	}
}