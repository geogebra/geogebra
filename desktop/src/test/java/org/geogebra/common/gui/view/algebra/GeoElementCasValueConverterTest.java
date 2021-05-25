package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolicTest;
import org.junit.Assert;
import org.junit.Test;

public class GeoElementCasValueConverterTest extends GeoSymbolicTest {

	private final GeoElementValueConverter converter = new GeoElementValueConverter();

	@Test
	public void testConvertsSimpleNumber() {
		GeoElement element = add("a*b");
		Assert.assertEquals("a b", converter.convert(element));
	}
}
