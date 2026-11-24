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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.euclidian.controller.MouseTouchGestureController;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.TestEvent;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pins what a pinch does to pen strokes in Notes, which must not change.
 */
@Issue({"APPS-8112", "MOW-714", "MOW-1896"})
class NotesPenPinchTest extends BaseEuclidianControllerTest {

	private MouseTouchGestureController gestureController;

	@BeforeEach
	void setUp() {
		setUpController();
		getApp().setNotesConfig();
		ec.setDraggingDelay(0);
		gestureController = new MouseTouchGestureController(getApp(), ec);
		setMode(EuclidianConstants.MODE_PEN);
	}

	private static TestEvent touch(int x, int y) {
		return new TestEvent(x, y, PointerEventType.TOUCH, false);
	}

	private void drawStroke(int x, int y) {
		ec.wrapMousePressed(touch(x, y));
		ec.wrapMouseDragged(touch(x + 20, y + 20), true);
		ec.wrapMouseDragged(touch(x + 40, y + 20), true);
		ec.wrapMouseReleased(touch(x + 40, y + 20));
	}

	private String strokeDefinition(String label) {
		return lookup(label).getDefinition(StringTemplate.defaultTemplate);
	}

	@Test
	void pinchShouldDropStrokeInProgressAndKeepEarlierStrokes() {
		drawStroke(100, 100);
		checkContentLabels("stroke1");
		String firstStroke = strokeDefinition("stroke1");

		ec.wrapMousePressed(touch(300, 300));
		ec.wrapMouseDragged(touch(320, 320), true);
		ec.wrapMouseDragged(touch(340, 340), true);
		double scaleBeforePinch = getApp().getActiveEuclidianView().getXscale();
		gestureController.twoTouchStart(340, 340, 500, 340);
		gestureController.twoTouchMove(340, 340, 600, 340);
		ec.wrapMouseReleased(touch(340, 340));
		ec.wrapMouseReleased(touch(600, 340));

		checkContentLabels("stroke1");
		assertEquals(firstStroke, strokeDefinition("stroke1"));
		assertTrue(
				getApp().getActiveEuclidianView().getXscale() > scaleBeforePinch, "pinch should zoom in");
	}

	@Test
	void strokeAfterPinchShouldBeNewStroke() {
		drawStroke(100, 100);
		ec.wrapMousePressed(touch(300, 300));
		gestureController.twoTouchStart(300, 300, 500, 300);
		gestureController.twoTouchMove(300, 300, 600, 300);
		ec.wrapMouseReleased(touch(300, 300));
		ec.wrapMouseReleased(touch(600, 300));

		drawStroke(100, 400);
		checkContentLabels("stroke1", "stroke2");
	}
}
