package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.junit.Test;

public class SlopeSizePropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() {
		GeoNumeric slope = addAvInput("Slope( Line((1,1), (2,2)) )");
		try {
			new SlopeSizeProperty(getLocalization(), slope);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testConstructorThrowsError() {
		GeoNumeric number = addAvInput("1");
		assertThrows(NotApplicablePropertyException.class,
				() -> new SlopeSizeProperty(getLocalization(), number));
	}
}