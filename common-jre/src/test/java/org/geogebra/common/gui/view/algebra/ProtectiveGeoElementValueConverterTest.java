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
		assertConverts("c=Circle((0,0), 2)", "(Circle((0, 0), 2))");
		assertConverts("j=c", "(c)");
		assertConverts("k=j", "(j)");
	}

	@Test
	public void testShowCommandInput() {
		assertConverts("f=Line((1,2), (3,4))", "(-x + y = 1)");
		assertConverts("g=f", "(-x + y = 1)");
		assertConverts("h=g", "(-x + y = 1)");
	}

	private void assertConverts(String input, String expected) {
		GeoElement element = add(input);
		Assert.assertEquals(expected, converter.convert(element));
	}
}
