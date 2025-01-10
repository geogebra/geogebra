package org.geogebra.common.properties.impl.objects;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.junit.Test;

public class ThicknessPropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() {
		GeoElement line = addAvInput("Line((1,1),(2,2))");
		try {
			new ThicknessProperty(getLocalization(), line);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testConstructorThrowsError() {
		GeoElement point = addAvInput("(1,1)");
		assertThrows(NotApplicablePropertyException.class,
				() -> new ThicknessProperty(getLocalization(), point));
	}
}