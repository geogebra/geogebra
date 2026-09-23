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

package org.geogebra.common.euclidian.tools;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRANSLATE_VIEW;
import static org.geogebra.common.kernel.Kernel.MAX_PRECISION;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.euclidian.EuclidianView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TranslateViewToolTest extends BaseToolTest {

	private EuclidianView view;

	@BeforeEach
	void setMode() {
		setMode(MODE_TRANSLATE_VIEW);
		view = getApp().getActiveEuclidianView();
	}

	@Test
	void moveViewShouldMoveOrigin() {
		double xZeroOriginal = view.getXZero();
		double yZeroOriginal = view.getYZero();
		double originalXScale = view.getXscale();
		double originalYScale = view.getYscale();
		dragStart(100, 100);
		dragEnd(200, 200);
		assertAll(
				() -> assertEquals(xZeroOriginal + 100, view.getXZero()),
				() -> assertEquals(yZeroOriginal + 100, view.getYZero()),
				() -> assertEquals(originalXScale, view.getXscale()),
				() -> assertEquals(originalYScale, view.getYscale()));
	}

	@Test
	void dragXAxisShouldIncreaseXScaleAndPreserveYScale() {
		double xZeroOriginal = view.getXZero();
		double yZeroOriginal = view.getYZero();
		view.setShowAxes(true, true);
		double originalXScale = view.getXscale();
		double originalYScale = view.getYscale();
		dragStart(50, 0);
		dragEnd(70, 0);
		assertAll(
				() -> assertEquals(originalXScale + 20, view.getXscale()),
				() -> assertEquals(originalYScale, view.getYscale()),
				() -> assertEquals(xZeroOriginal, view.getXZero()),
				() -> assertEquals(yZeroOriginal, view.getYZero()));
	}

	@Test
	void dragYAxisShouldIncreaseYScaleAndPreserveXScale() {
		double xZeroOriginal = view.getXZero();
		double yZeroOriginal = view.getYZero();
		view.setShowAxes(true, true);
		double originalXScale = view.getXscale();
		double originalYScale = view.getYscale();
		dragStart(0, 50);
		dragEnd(0, 70);
		assertAll(
				() -> assertEquals(originalXScale, view.getXscale()),
				() -> assertEquals(originalYScale + 20, view.getYscale()),
				() -> assertEquals(xZeroOriginal, view.getXZero()),
				() -> assertEquals(yZeroOriginal, view.getYZero()));
	}

	@Test
	void dragXAxisNearOriginShouldUseMinimumScaleDistance() {
		view.setShowAxes(true, true);
		double originalYScale = view.getYscale();
		dragStart(50, 0);
		dragEnd(1, 0);
		assertAll(
				() -> assertEquals(2, view.getXscale()),
				() -> assertEquals(originalYScale, view.getYscale()));
	}

	@Test
	void dragYAxisNearOriginShouldUseMinimumScaleDistance() {
		view.setShowAxes(true, true);
		double originalXScale = view.getXscale();
		dragStart(0, 50);
		dragEnd(0, 1);
		assertAll(
				() -> assertEquals(originalXScale, view.getXscale()),
				() -> assertEquals(2, view.getYscale()));
	}

	@Test
	void dragXAxisWithOriginLeftOfViewShouldScaleFromViewEdge() {
		view.setShowAxes(true, true);
		double originalXScale = view.getXscale();
		double originalYScale = view.getYscale();
		view.setCoordSystem(-2 * originalXScale, view.getYZero(), originalXScale, originalYScale);
		dragStart(50, 0);
		dragEnd(70, 0);
		assertAll(
				() -> assertEquals(originalXScale + 20, view.getXscale()),
				() -> assertEquals(-2 * (originalXScale + 20), view.getXZero()),
				() -> assertEquals(originalYScale, view.getYscale()));
	}

	@Test
	void dragXAxisWithOriginRightOfViewShouldScaleFromViewEdge() {
		view.setShowAxes(true, true);
		double originalXScale = view.getXscale();
		double originalYScale = view.getYscale();
		int viewWidth = view.getWidth();
		view.setCoordSystem(
				viewWidth + 2 * originalXScale, view.getYZero(), originalXScale, originalYScale);
		dragStart(viewWidth - 50, 0);
		dragEnd(viewWidth - 70, 0);
		assertAll(
				() -> assertEquals(originalXScale + 20, view.getXscale()),
				() -> assertEquals(viewWidth + 2 * (originalXScale + 20), view.getXZero()),
				() -> assertEquals(originalYScale, view.getYscale()));
	}

	@Test
	void dragXAxisWithLockedRatioShouldPanInsteadOfScale() {
		double xZeroOriginal = view.getXZero();
		double yZeroOriginal = view.getYZero();
		view.setShowAxes(true, true);
		view.setLockedAxesRatio(1);
		double originalXScale = view.getXscale();
		double originalYScale = view.getYscale();
		dragStart(50, 0);
		dragEnd(70, 0);
		assertAll(
				() -> assertEquals(originalXScale, view.getXscale()),
				() -> assertEquals(originalYScale, view.getYscale()),
				() -> assertEquals(xZeroOriginal + 20, view.getXZero()),
				() -> assertEquals(yZeroOriginal, view.getYZero()));
	}

	@Test
	void dragYAxisWithLockedRatioShouldPanInsteadOfScale() {
		double xZeroOriginal = view.getXZero();
		double yZeroOriginal = view.getYZero();
		view.setShowAxes(true, true);
		view.setLockedAxesRatio(1);
		double originalXScale = view.getXscale();
		double originalYScale = view.getYscale();
		dragStart(0, 50);
		dragEnd(0, 70);
		assertAll(
				() -> assertEquals(originalXScale, view.getXscale()),
				() -> assertEquals(originalYScale, view.getYscale()),
				() -> assertEquals(xZeroOriginal, view.getXZero()),
				() -> assertEquals(yZeroOriginal + 20, view.getYZero(), MAX_PRECISION));
	}
}
