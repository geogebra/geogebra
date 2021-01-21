package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Test;

public class GeoFunctionNVarTest extends BaseUnitTest {

	@Test
	public void testNVarInequalityPreserved() {
		GeoFunctionNVar func = addAvInput("a: x < y");
		addAvInput("SetValue(a, ?)");
		getApp().setXML(getApp().getXML(), true);
		assertEquals("a: ?", func.toString(StringTemplate.defaultTemplate));
	}
}
