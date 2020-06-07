package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class ColorPropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() throws NotApplicablePropertyException {
		GeoElement point = addAvInput("(1,2)");
		new ColorProperty(point);
	}
}