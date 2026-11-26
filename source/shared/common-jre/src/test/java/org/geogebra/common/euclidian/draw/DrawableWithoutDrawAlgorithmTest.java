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

package org.geogebra.common.euclidian.draw;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Drawables of numbers drawn by their parent algorithm must survive losing that algorithm.
 * On iOS the parent algorithm is referenced weakly, so it becomes null once it is removed.
 */
class DrawableWithoutDrawAlgorithmTest extends BaseAppTestSetup {

	@BeforeEach
	void setup() {
		setupApp(SuiteSubApp.GRAPHING);
		evaluate("f(x)=x^2");
		evaluate("g(x)=x");
		evaluate("h: y=2x+1");
	}

	@Issue("APPS-8200")
	@ParameterizedTest
	@ValueSource(
			strings = {
				"Integral(f,0,1)",
				"IntegralBetween(f,g,0,1)",
				"UpperSum(f,0,1,4)",
				"Slope(h)",
				"BarChart({1,2,3},{4,5,6})"
			})
	void updateShouldNotCrashWithoutDrawAlgorithm(String command) {
		GeoElement number = evaluateGeoElement(command);
		Drawable drawable =
				assertInstanceOf(Drawable.class, getApp().getActiveEuclidianView().getDrawableFor(number));
		number.setParentAlgorithm(null);
		assertDoesNotThrow(drawable::update);
		assertDoesNotThrow(() -> drawable.draw(new SvgGraphics(800, 600)));
	}
}
