package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.*;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class LineOpacityPropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() {
		GeoElement slider = addAvInput("1");
		slider.setEuclidianVisible(true);
		new LineOpacityProperty(slider);
	}

	@Test
	public void testConstructorThrowsError() {
		GeoElement point = addAvInput("(1,2)");
		assertThrows(NotApplicablePropertyException.class, () -> new LineOpacityProperty(point));
	}
}