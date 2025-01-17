package org.geogebra.common.gui.view.algebra;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class GeoElementCasValueConverterTest extends BaseSymbolicTest {

	private final GeoElementValueConverter converter = new GeoElementValueConverter();

	@Test
	public void testConvertsSimpleNumber() {
		GeoElement element = add("a*b");
		assertEquals("a b", converter.convert(element));
	}
}
