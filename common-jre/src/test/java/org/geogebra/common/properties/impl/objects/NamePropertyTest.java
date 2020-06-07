package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class NamePropertyTest extends BaseUnitTest {

	@Test
	public void testConstructorSucceeds() throws NotApplicablePropertyException {
		GeoElement slider = addAvInput("1");
		new NameProperty(slider);
	}
}