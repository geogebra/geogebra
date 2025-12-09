/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian.plot.interval;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.function.GeoFunctionConverter;
import org.geogebra.common.util.DoubleUtil;
import org.junit.Test;

public class GlitchesTest extends BaseUnitTest {

	private final GeoFunctionConverter converter = new GeoFunctionConverter();

	@Test
	public void testZeroDividedBySinXShouldNotContainInfinity() {
		withDefaultScreen();
		withFunction("0/sin(x)");
		assertEquals(0, gp.getLog().stream().filter(e -> Double.isInfinite(e.y)).count());
	}

	@Test
	public void oneDividedByZeroTimeXShouldBeEmpty() {
		withHiResFunction("1/(0x)");
		assertEquals(1, gp.getLog().size());
	}

	@Test
	public void divByZeroThreshold() {
		withHiResFunction("((1*10^(-13) x)/(1*10^(-13)))");
		assertEquals(1 + 1920 * 2, gp.getLog().size());
	}

	@Test
	public void divTwoConstantsBelowZeroThreshold() {
		withHiResFunction("((1*10^(-13))/(1*10^(-13)))x");
		assertEquals(1 + 1920 * 2, gp.getLog().size());
	}

	@Test
	public void testXInverseMultipliedByZero() {
		withBounds(-1, 1, -8, -8);
		withScreenSize(50, 50);
		withFunction("0(1/x)");
		assertEquals(101, gp.getLog().stream().filter(e -> DoubleUtil.isEqual(e.y, 0))
				.count());
	}

	@Test
	public void testZeroDividedByZeroDividedByTanXShouldBeEmpty() {
		withBounds(-1, 1, -8, -8);
		withScreenSize(50, 50);
		withFunction("0/(0/tan(x))");
		assertEquals(1, gp.getLog().size());
	}

	@Test
	public void testLnInverseTimesZeroShouldBeZero() {
		withBounds(0, 10, -8, -8);
		withDefaultScreen();
		withFunction("(1/ln(x)) * 0");
		assertEquals(0,
				gp.getLog().stream().filter(t -> t.y != 0).count());
	}

	@Test
	public void testSin0OnPowerZero() {
		withBounds(0, 10, -8, -8);
		withDefaultScreen();
		withFunction("sin(0)^x");
		assertEquals(1919,
				gp.getLog().stream().filter(t -> t.y == 0).count());
	}

	@Test
	public void testTanXAtHighZoomIsWhole() {
		withBounds(-1E15, 1E15, -1E15, -1E15);
		withScreenSize(50, 50);
		withFunction("tan(x)");
		assertEquals(101, gp.getLog().size());
	}

	@Test
	public void oneDividedByXDoesNotProduceNans() {
		withDefaultScreen();
		withFunction("1/x");
		assertEquals(0, gp.getLog().stream().filter(e -> Double.isInfinite(e.y)).count());
	}

	IntervalPathPlotterMock gp;
	EuclidianViewBoundsMock bounds;
	IntervalPlotter plotter;

	void withDefaultScreen() {
		withBounds(-15, 15, -8, -8);
		withScreenSize(1920, 1250);
	}

	void withBounds(double xmin, double xmax, double ymin, double ymax) {
		bounds = new EuclidianViewBoundsMock(xmin, xmax, ymin, ymax);
	}

	void withScreenSize(int width, int height) {
		bounds.setSize(width, height);
	}

	void withFunction(String functionString) {
		gp = new IntervalPathPlotterMock(bounds);
		plotter = new IntervalPlotter(converter, bounds, gp);
		GeoFunction function = add(functionString);
		plotter.enableFor(function);
	}

	protected void withHiResFunction(String description) {
		withBounds(-5000, 5000, 6000, -4000);
		withScreenSize(1920, 1280);
		withFunction(description);
	}

	@Test
	public void testExpShouldNotHaveGaps() {
		withBounds(5.0, 8.0, 5, 5);
		withScreenSize(50, 50);
		withFunction("1-exp(-5x)");
		assertEquals(1, gp.getLog().stream().filter(
				e -> e.operation == IntervalPathMockEntry.PathOperation.MOVE_TO).count());
	}
}
