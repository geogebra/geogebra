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

package org.geogebra.common.euclidian;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.euclidian.draw.DrawParametricCurve;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.jre.headless.FlushableTimer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PathTrackerTest extends BaseEuclidianControllerTest {

	@BeforeEach
	void setup() {
		setupApp(SuiteSubApp.GRAPHING);
		ec = getApp().getActiveEuclidianView().getEuclidianController();
		reset();
	}

	@Test
	void clickingAFunctionShouldCreateTrackingPoint() {
		evaluate("-x^2");
		dragStart(50, 50);
		DrawableList allDrawableList = ec.getView().getAllDrawableList();
		assertEquals(1, allDrawableList.size());
		FlushableTimer.flush();
		assertEquals(2, allDrawableList.size());
		assertInstanceOf(DrawParametricCurve.class, allDrawableList.get(0));
		assertInstanceOf(DrawPoint.class, allDrawableList.get(1));
		dragEnd(50, 50);
		assertEquals(1, allDrawableList.size());
	}
}
