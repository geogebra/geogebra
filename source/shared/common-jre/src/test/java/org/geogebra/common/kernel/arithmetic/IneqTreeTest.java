package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.util.ExtendedBoolean;
import org.junit.Test;

public class IneqTreeTest extends BaseUnitTest {

	@Test
	public void ignoreExtraVerticesOr() {
		GeoFunctionNVar fn = add("(x > 1 || x > 2 + y || x + y > 6) && y > 1");
		IneqTree ineqs = fn.getIneqs();
		assertEquals("Expect UNKNOWN for true vertex",
				ExtendedBoolean.UNKNOWN, ineqs.valueAround(1, 1));
		assertEquals("Expect FALSE for intersection outside",
				ExtendedBoolean.FALSE, ineqs.valueAround(1, -1));
		assertEquals("Expect TRUE for intersection inside",
				ExtendedBoolean.TRUE, ineqs.valueAround(4, 2));
	}

}
