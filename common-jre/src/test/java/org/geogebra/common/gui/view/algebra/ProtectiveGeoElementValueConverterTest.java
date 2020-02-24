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
		assertConverts("f=Line((1,2), (3,4))", "(Line((1, 2), (3, 4)))");
		assertConverts("c=Circle((0,0), 2)", "(Circle((0, 0), 2))");
		assertConverts("g=f", "(f)");
		assertConverts("h=g", "(g)");
		assertConverts("j=c", "(c)");
		assertConverts("k=j", "(j)");
	}

	private void assertConverts(String input, String expected) {
		GeoElement element = add(input);
		Assert.assertEquals(expected, converter.convert(element));
	}
}
