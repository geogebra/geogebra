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

package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.implicit.EuclidianViewBoundsRWSCMock;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;

/**
 * Test base for implicit contour plotting.
 * <p>
 * Sets up a GRAPHING app and provides helpers to create sample curves and
 * view bounds for tests.
 * </p>
 */
public class BaseContourTestSetup extends BaseAppTestSetup {
	public ContourBuilder builder = new ContourBuilder();

	/**
	 * Initializes the test app in GRAPHING mode before each test.
	 */
	@BeforeEach
	void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	/**
	 * Adds a Cassini oval to the kernel and returns the created element.
	 *
	 * @param a parameter kept for API compatibility (unused in this test helper)
	 * @param c parameter kept for API compatibility (unused in this test helper)
	 * @return the created implicit curve as a {@link GeoElement}
	 */
	protected GeoElement addCassini(double a, double c) {
		String cassiniDef = "(x^2 + y^2)^2 - " + 2 * Math.pow(c, 2) + " * (x^2 - y^2)"
				+ "- " + (Math.pow(a, 4) - Math.pow(c, 4)) + " = 0";
		Log.debug(cassiniDef);
		return (GeoElement) evaluate(cassiniDef)[0];
	}

	/**
	 * Creates view bounds for tests from a world rectangle and screen size.
	 *
	 * @param xmin   minimum x in world units
	 * @param xmax   maximum x in world units
	 * @param ymin   minimum y in world units
	 * @param ymax   maximum y in world units
	 * @param width  screen width in pixels
	 * @param height screen height in pixels
	 * @return a bounds mock that computes scales and origin from the inputs
	 */
	public static EuclidianViewBounds newBounds(double xmin, double xmax, double ymin, double ymax,
			int width,
			int height) {
		return new EuclidianViewBoundsRWSCMock(xmin, xmax, ymin, ymax, width, height);
	}

	/**
	 * Constructs {@link EuclidianViewBounds} from a GeoGebra view JSON string.
	 * <p>
	 * Accepts the JSON output from {@code ggbApplet.getViewProperties(viewNo)},
	 * typically copied from the JavaScript console, and uses it to mock view bounds.
	 * Note that top and left are not used, so can be deleted from JSON output.
	 *
	 * @param json JSON string containing x/y bounds, pixel dimensions, and scale info
	 * @return a computed {@link EuclidianViewBounds} representing the view setup
	 * @throws RuntimeException if the input is not a valid or expected JSON string
	 */
	public static EuclidianViewBounds boundsFromJSON(String json) {
		try {
			JSONObject dir = new JSONObject(json);
			double xMin = dir.getDouble("xMin");
			double yMin = dir.getDouble("yMin");
			int width = dir.getInt("width");
			int heigh = dir.getInt("height");
			double xMax = xMin + (width * dir.getDouble("invXscale"));
			double yMax = yMin + (heigh * dir.getDouble("invYscale"));
			return newBounds(xMin, xMax, yMin, yMax, width, heigh);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
}
