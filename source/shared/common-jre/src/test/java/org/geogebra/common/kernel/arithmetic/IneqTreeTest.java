package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
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

	@Test
	public void inequalityShouldOnlyUseStrictBorder() {
		GeoFunction fn = add("x < 1 && x <= 1");
		List<Inequality> borders = fn.getIneqs().getPreferredBorders();
		assertEquals(1, borders.size());
		assertTrue(borders.get(0).isStrict());
	}

	@Test
	public void inequalityShouldNotOverlayBorders1() {
		GeoFunction fn = add("(-4 < x <= 3) && (-1 < x < 3)");
		List<Inequality> borders = fn.getIneqs().getPreferredBorders();
		assertEquals(3, borders.size());
		assertTrue(borders.get(0).isStrict());
		assertTrue(borders.get(1).isStrict());
	}

	@Test
	public void inequalityShouldNotOverlayBorders2() {
		GeoFunction fn = add("x >= 3 && x > 2 && x > 3");
		List<Inequality> borders = fn.getIneqs().getPreferredBorders();
		assertEquals(2, borders.size());
		assertTrue(borders.get(0).isStrict());
	}

	@Test
	public void inequalityShouldOnlyUseNonStrictBorder() {
		GeoFunction fn = add("x > 2 || x >= 2");
		List<Inequality> borders = fn.getIneqs().getPreferredBorders();
		assertEquals(1, borders.size());
		assertFalse(borders.get(0).isStrict());
	}
}
