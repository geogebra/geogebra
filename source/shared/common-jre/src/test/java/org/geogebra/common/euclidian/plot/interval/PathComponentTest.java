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

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.function.GeoFunctionConverter;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PathComponentTest extends BaseAppTestSetup {

	private final GeoFunctionConverter converter = new GeoFunctionConverter();
	private IntervalPathPlotterMock gp;
	private EuclidianViewBoundsMock bounds;

	@BeforeEach
	void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void testSqtrTanCotx() {
		withBounds(5.0, 8.0, 5, 5);
		withScreenSize(50, 50);
		withFunction("sqrt(tan(cot(x)))");
		assertEquals(6, componentCount());
	}

	private long componentCount() {
		return gp.getLog().stream()
				.filter(e -> e.operation() == IntervalPathMockEntry.PathOperation.MOVE_TO)
				.count();
	}

	@Test
	void cotsecx() {
		withBounds(5.0, 8.0, 5, 5);
		withScreenSize(50, 50);
		withFunction("cot(sec(x))");
		assertEquals(10, componentCount());
	}

	@ParameterizedTest
	@CsvSource({
			"lnx",
			"-1/sqrt(ln(x))",
			"(x^2)^2",
			"x^(2^2)"
	})
	void functionsThrowNoExceptions(String definition) {
		withBounds(5.0, 8.0, 5, 5);
		withScreenSize(500, 500);
		withFunction(definition);
	}

	@Test
	void testSqrtCotx() {
		withBounds(-3.2, 3.0, -2, 2);
		withScreenSize(1122, 802);
		withFunction("sqrt(cot(x))");
		assertEquals(2, componentCount());
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
