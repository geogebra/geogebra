package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.*;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class FixObjectPropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() throws NotApplicablePropertyException {
		GeoElement point = addAvInput("(1,2)");
		new FixObjectProperty(point);
	}

	@Test
	public void testConstructorThrowsError() {
		getApp().setGraphingConfig();
		GeoElement f = addAvInput("f: x");
		assertThrows(NotApplicablePropertyException.class, () -> new FixObjectProperty(f));
	}
}