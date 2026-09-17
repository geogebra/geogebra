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

package org.geogebra.common.kernel.implicit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.EuclidianViewBoundsImp;
import org.geogebra.common.euclidian.plot.implicit.BernsteinImplicitAlgo;
import org.geogebra.common.euclidian.plot.implicit.BernsteinPlotCell;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImplicitCurveConnectivityTest extends BaseAppTestSetup {

	@BeforeEach
	void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void testCassiniOneClosedCurve() {
		ContourAssembler contourLinker = newContourLinker();
		BernsteinImplicitAlgo algo = new BernsteinImplicitAlgo(
				newBounds(), addCassini(2.98, 0.03), newCells(), contourLinker, 4);
		algo.compute();
		assertEquals(1, contourLinker.contourCount());
	}

	private EuclidianViewBoundsImp newBounds() {
		return new EuclidianViewBoundsImp(getApp().getEuclidianView1());
	}

	private static ContourAssembler newContourLinker() {
		return new ContourAssembler(new CompleteContourLinker());
	}

	private GeoElement addCassini(double a, double c) {
		evaluate("a = " + a);
		evaluate("c = " + c);
		return (GeoElement) evaluate("eq1: (x^2 + y^2)^2 - 2 * c^2 * (x^2 - y^2) - (a^4 - c^4) = 0")[0];
	}

	@Test
	void testCassiniTwoClosedCurve() {
		GeoElement cassiniGeo = addCassini(3.8, 3.9);
		ContourAssembler linker = newContourLinker();
		EuclidianViewBounds bounds = newBounds();
		BernsteinImplicitAlgo algo =
				new BernsteinImplicitAlgo(bounds, cassiniGeo, newCells(), linker, 4);
		algo.compute();
		assertEquals(2, linker.contourCount());
	}

	private static ArrayList<BernsteinPlotCell> newCells() {
		return new ArrayList<>();
	}
}
