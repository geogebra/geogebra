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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.geogebra.test.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TemporaryTranslateViewTest extends BaseEuclidianControllerTest {

	private EuclidianView view;

	@BeforeEach
	void setUp() {
		setUpController();
		setMode(MODE_MOVE);
		view = getApp().getActiveEuclidianView();
		view.setShowAxes(true, true);
	}

	@Test
	void shiftDragXAxisShouldScaleTemporarilyAndRestoreMoveMode() {
		double originalXScale = view.getXscale();
		double originalYScale = view.getYscale();
		shiftDragStart(50, 0);
		shiftDragEnd(70, 0);
		assertAll(
				() -> assertEquals(originalXScale + 20, view.getXscale()),
				() -> assertEquals(originalYScale, view.getYscale()),
				() -> assertEquals(MODE_MOVE, getApp().getMode()),
				() -> assertFalse(ec.isTemporaryMode()));
	}

	@Test
	void shiftDragYAxisShouldScaleTemporarilyAndRestoreMoveMode() {
		double originalXScale = view.getXscale();
		double originalYScale = view.getYscale();
		shiftDragStart(0, 50);
		shiftDragEnd(0, 70);
		assertAll(
				() -> assertEquals(originalXScale, view.getXscale()),
				() -> assertEquals(originalYScale + 20, view.getYscale()),
				() -> assertEquals(MODE_MOVE, getApp().getMode()),
				() -> assertFalse(ec.isTemporaryMode()));
	}

	private void shiftDragStart(int x, int y) {
		ec.setDraggingDelay(0);
		ec.wrapMousePressed(new ShiftTestEvent(x, y));
	}

	private void shiftDragEnd(int x, int y) {
		TestEvent event = new ShiftTestEvent(x, y);
		ec.wrapMouseDragged(event, true);
		ec.wrapMouseDragged(event, true);
		ec.wrapMouseReleased(event);
	}

	private static final class ShiftTestEvent extends TestEvent {

		private ShiftTestEvent(int x, int y) {
			super(x, y);
		}

		@Override
		public boolean isShiftDown() {
			return true;
		}
	}
}
