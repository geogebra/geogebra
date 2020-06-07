package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.assertThrows;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.junit.Test;

public class SlopeSizePropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() throws NotApplicablePropertyException {
		GeoNumeric slope = addAvInput("Slope( Line((1,1), (2,2)) )");
		new SlopeSizeProperty(slope);
	}

	@Test
	public void testConstructorThrowsError() {
		GeoNumeric number = addAvInput("1");
		assertThrows(NotApplicablePropertyException.class, () -> new SlopeSizeProperty(number));
	}
}