package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Assert;
import org.junit.Test;

public class ProtectiveGeoElementValueConverterTest extends BaseUnitTest {

	private ProtectiveGeoElementValueConverter converter =
			new ProtectiveGeoElementValueConverter();

	@Test
	public void testHidesCommandInput() {
		assertConverts("Line((1,2), (3,4))", "(Line((1, 2), (3, 4)))");
		assertConverts("Circle((0,0), 2)", "(Circle((0, 0), 2))");
	}

	private void assertConverts(String input, String expected) {
		GeoElement element = add(input);
		Assert.assertEquals(expected, converter.convert(element));
	}
}
