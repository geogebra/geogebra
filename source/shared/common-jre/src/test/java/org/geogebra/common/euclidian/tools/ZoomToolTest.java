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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ZOOM_IN;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ZOOM_OUT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ZoomToolTest extends BaseToolTest {

	private static final double PRECISION = 1E-8;

	private EuclidianView view;

	@BeforeEach
	void getView() {
		view = getApp().getActiveEuclidianView();
	}

	@Test
	void zoomInClickShouldIncreaseScaleAroundClickedPoint() {
		setMode(MODE_ZOOM_IN);

		assertClickZoom(2, -2, EuclidianView.MODE_ZOOM_FACTOR);
	}

	@Test
	void zoomOutClickShouldDecreaseScaleAroundClickedPoint() {
		setMode(MODE_ZOOM_OUT);

		assertClickZoom(2, -2, 1 / EuclidianView.MODE_ZOOM_FACTOR);
	}

	@Test
	void repeatedZoomInClicksShouldApplyFactorEachTime() {
		setMode(MODE_ZOOM_IN);
		double originalScale = view.getXscale();

		clickRW(2, -2);
		resetMouseLocation();
		clickRW(2, -2);

		assertEquals(
				originalScale * EuclidianView.MODE_ZOOM_FACTOR * EuclidianView.MODE_ZOOM_FACTOR,
				view.getXscale(),
				PRECISION);
	}

	@Test
	void zoomInSelectionRectangleShouldSetViewBoundsAndBeUndoable() {
		getApp().setUndoActive(true);
		getApp().storeUndoInfo();
		setMode(MODE_ZOOM_IN);
		ViewState originalView = currentViewState();

		dragRW(1, -1, 5, -4);

		assertAll(
				() -> assertEquals(1, view.getXmin(), PRECISION),
				() -> assertEquals(5, view.getXmax(), PRECISION),
				() -> assertEquals(-4, view.getYmin(), PRECISION),
				() -> assertEquals(-1, view.getYmax(), PRECISION));

		getKernel().undo();

		assertViewState(originalView);
	}

	@Test
	void narrowZoomInSelectionRectangleShouldNotChangeView() {
		setMode(MODE_ZOOM_IN);
		ViewState originalView = currentViewState();

		dragRW(1, -1, 1.4, -4);

		assertViewState(originalView);
	}

	@Test
	void shortZoomInSelectionRectangleShouldNotChangeView() {
		setMode(MODE_ZOOM_IN);
		ViewState originalView = currentViewState();

		dragRW(1, -1, 5, -1.4);

		assertViewState(originalView);
	}

	@Test
	void disabledSelectionRectangleZoomShouldNotChangeView() {
		setMode(MODE_ZOOM_IN);
		getApp().setShiftDragZoomEnabled(false);
		ViewState originalView = currentViewState();

		dragRW(1, -1, 5, -4);

		assertViewState(originalView);
	}

	@Test
	void zoomInDragStartingOnObjectShouldNotChangeView() {
		setMode(MODE_ZOOM_IN);
		add("A = (1, -1)");
		ViewState originalView = currentViewState();

		dragRW(1, -1, 5, -4);

		assertViewState(originalView);
	}

	@Test
	void zoomOutDragShouldNotChangeView() {
		setMode(MODE_ZOOM_OUT);
		ViewState originalView = currentViewState();

		dragRW(1, -1, 5, -4);

		assertViewState(originalView);
	}

	@Test
	void labeledViewBoundsShouldPreventClickZoom() {
		setMode(MODE_ZOOM_IN);
		GeoNumeric xmin = add("xmin = " + view.getXmin());
		view.setXminObject(xmin);
		ViewState originalView = currentViewState();
		assertFalse(view.isZoomable());

		clickRW(2, -2);

		assertViewState(originalView);
	}

	@Test
	void leavingZoomToolShouldMakeClicksUndoableAsOneAction() {
		getApp().setUndoActive(true);
		getApp().storeUndoInfo();
		setMode(MODE_ZOOM_IN);
		ViewState originalView = currentViewState();

		clickRW(2, -2);
		resetMouseLocation();
		clickRW(2, -2);
		setMode(MODE_MOVE);
		getKernel().undo();

		assertViewState(originalView);
	}

	private void assertClickZoom(double xRW, double yRW, double factor) {
		double originalXScale = view.getXscale();
		double originalYScale = view.getYscale();
		int screenX = view.toScreenCoordX(xRW);
		int screenY = view.toScreenCoordY(yRW);

		clickRW(xRW, yRW);

		assertAll(
				() -> assertEquals(originalXScale * factor, view.getXscale(), PRECISION),
				() -> assertEquals(originalYScale * factor, view.getYscale(), PRECISION),
				() -> assertEquals(screenX, view.toScreenCoordX(xRW)),
				() -> assertEquals(screenY, view.toScreenCoordY(yRW)));
	}

	private ViewState currentViewState() {
		return new ViewState(view.getXmin(), view.getXmax(), view.getYmin(), view.getYmax());
	}

	private void assertViewState(ViewState expected) {
		assertAll(
				() -> assertEquals(expected.xmin(), view.getXmin(), PRECISION),
				() -> assertEquals(expected.xmax(), view.getXmax(), PRECISION),
				() -> assertEquals(expected.ymin(), view.getYmin(), PRECISION),
				() -> assertEquals(expected.ymax(), view.getYmax(), PRECISION));
	}

	private record ViewState(double xmin, double xmax, double ymin, double ymax) {}
}
