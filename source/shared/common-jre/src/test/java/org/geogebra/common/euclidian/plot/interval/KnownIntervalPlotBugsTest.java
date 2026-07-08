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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.function.GeoFunctionConverter;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class KnownIntervalPlotBugsTest extends BaseAppTestSetup {

	private final GeoFunctionConverter converter = new GeoFunctionConverter();
	private IntervalPathPlotterMock gp;
	private EuclidianViewBoundsMock bounds;

	@BeforeEach
	void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void lnCoshShouldNotProduceFalseInfinitySpikes() {
		withBounds(-750, 750, -50, 750);
		withScreenSize(200, 200);
		withFunction("ln(cosh(x))");

		assertEquals(0, gp.getLog().stream()
				.filter(e -> Double.isInfinite(e.y())).count());
		assertTrue(hasFinitePoint(-690, 650, false), gp.getLog().toString());
		assertTrue(hasFinitePoint(690, 650, true), gp.getLog().toString());
	}

	@Test
	void lnExpShouldNotProduceFalseNegativeInfinityRay() {
		withBounds(-750, 750, -5500, 5500);
		withScreenSize(1200, 900);
		withFunction("ln(e^x)");

		assertFalse(gp.getLog().stream().anyMatch(e -> e.y() < -1000),
				gp.getLog().toString());
		assertTrue(hasFinitePoint(-700, -800, false), gp.getLog().toString());
		assertTrue(hasFinitePoint(700, 600, true), gp.getLog().toString());
	}

	@ParameterizedTest
	@CsvSource({
			"ln(ln(exp(exp(x))))",
			"ln(ln(e^(e^x)))"
	})
	void nestedLnExpShouldNotProduceFalseNegativeInfinityRay(String definition) {
		withBounds(-1000, 1000, -1000, 1000);
		withScreenSize(625, 443);
		withFunction(definition);

		assertFalse(gp.getLog().stream().anyMatch(e -> e.y() < -2000),
				gp.getLog().toString());
		assertTrue(hasFinitePoint(-900, -950, false), gp.getLog().toString());
		assertTrue(hasFinitePoint(900, 850, true), gp.getLog().toString());
	}

	private boolean hasFinitePoint(double xThreshold, double yThreshold,
			boolean positiveSide) {
		return gp.getLog().stream().anyMatch(e -> Double.isFinite(e.y())
				&& (positiveSide ? e.x() > xThreshold : e.x() < xThreshold)
				&& e.y() > yThreshold);
	}

	private void withBounds(double xmin, double xmax, double ymin, double ymax) {
		bounds = new EuclidianViewBoundsMock(xmin, xmax, ymin, ymax);
	}

	private void withScreenSize(int width, int height) {
		bounds.setSize(width, height);
	}

	private void withFunction(String functionString) {
		gp = new IntervalPathPlotterMock(bounds);
		IntervalPlotter plotter = new IntervalPlotter(converter, bounds, gp);
		GeoFunction function = evaluateGeoElement(functionString);
		plotter.enableFor(function);
	}
}
