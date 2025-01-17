package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

/**
 * Test for default labeling done by {@link GeoElement#getDefaultLabel()} and
 * {@link LabelManager}}
 */
public class LabelManagerTest extends BaseUnitTest {

	@Test
	public void implicitEquationsShouldHaveEquationPrefix() {
		GeoElement lineX = addSilent("x=y");
		assertEquals("eq1", lineX.getDefaultLabel());
		GeoElement lineXY = addSilent("y=x+y");
		assertEquals("eq1", lineXY.getDefaultLabel());
	}

	@Test
	public void explicitEquationsShouldHaveFunctionLabels() {
		GeoElement lineY = addSilent("y=x");
		assertEquals("f", lineY.getDefaultLabel());
		GeoElement cubic = addSilent("y=x^3");
		assertEquals("f", cubic.getDefaultLabel());
		GeoElement trig = addSilent("y=sin(x)");
		assertEquals("f", trig.getDefaultLabel());
		GeoElement simpleFunction = addSilent("x");
		assertEquals("f", simpleFunction.getDefaultLabel());
	}

	@Test
	public void explicitConicsShouldHaveFunctionLabels() {
		GeoElement parabola = addSilent("y=x^2");
		assertEquals("f", parabola.getDefaultLabel());
	}

	@Test
	public void implicitConicsShouldHaveEquationPrefix() {
		GeoElement parabola = addSilent("x=y^2");
		assertEquals("eq1", parabola.getDefaultLabel());
	}

	@Test
	public void toolConicsShouldHaveConicLabel() {
		GeoElement parabola = addSilent("Circle(O, 1)");
		assertEquals("c", parabola.getDefaultLabel());
	}

	@Test
	public void equationLabelsShouldBeIndexed() {
		add("x=y");
		GeoElement lineX = addSilent("x=y");
		assertEquals("eq2", lineX.getDefaultLabel());
	}

	@Test
	public void functionLabelsShouldBeSequential() {
		add("y=x");
		add("y=x^3");
		add("x");
		add("y=sin(x)");
		assertArrayEquals(new String[] { "f", "g", "h", "p" },
				getApp().getGgbApi().getAllObjectNames());
	}

	private GeoElement addSilent(String string) {
		GeoElement geo = add(string);
		geo.remove();
		return geo;
	}
}
