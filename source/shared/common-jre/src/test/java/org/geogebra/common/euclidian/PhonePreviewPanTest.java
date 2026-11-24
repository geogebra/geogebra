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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.controller.PhoneTouchGestureTarget;
import org.geogebra.common.euclidian.controller.TouchGestureArbiter;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.EuclidianControllerNoGui;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.test.LocalizationCommonUTF;
import org.geogebra.test.TestEvent;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pins the mobile-only mode in which the first finger keeps holding the preview of a construction
 * in progress while a second finger pans the view.
 */
@Issue("APPS-8112")
class PhonePreviewPanTest {

	private static final double DELTA = 1E-6;

	private AppCommon app;
	private EuclidianController ec;
	private EuclidianView view;

	@AfterEach
	void disablePreviewFeatures() {
		PreviewFeature.setPreviewFeaturesEnabled(false);
	}

	@BeforeEach
	void setUp() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
		// like the Android and iOS controllers, this one can create touch events
		app =
				new AppCommon(
						new LocalizationCommonUTF(2), new AwtFactoryCommon(), new AppConfigDefault()) {
					@Override
					public EuclidianController newEuclidianController(Kernel kernel1) {
						return new EuclidianControllerNoGui(this, kernel1) {
							@Override
							public AbstractEvent createTouchEvent(int x, int y) {
								return touch(x, y);
							}
						};
					}
				};
		view = app.getActiveEuclidianView();
		ec = view.getEuclidianController();
		ec.setDraggingDelay(0);
		view.getSettings().setShowAxes(false, false);
		view.getSettings().setPointCapturing(EuclidianStyleConstants.POINT_CAPTURING_OFF);
		view.setCoordSystem(0, 0, 50, 50);
	}

	private static TestEvent touch(int x, int y) {
		return new TestEvent(x, y, PointerEventType.TOUCH, false);
	}

	private void tap(int x, int y) {
		ec.touchStartPhone(touch(x, y));
		ec.touchEndPhone(touch(x, y));
	}

	private List<String> content() {
		List<String> definitions = new ArrayList<>();
		for (String label : app.getGgbApi().getAllObjectNames()) {
			definitions.add(app.getKernel().lookupLabel(label).toString(StringTemplate.editTemplate));
		}
		return definitions;
	}

	@Test
	void secondFingerShouldPanViewWhileFirstFingerHoldsPreview() {
		app.setMode(EuclidianConstants.MODE_SEGMENT);
		tap(100, 100);
		ec.touchStartPhone(touch(200, 100));
		assertNotNull(view.getPreviewDrawable(), "construction in progress should have a preview");

		ec.twoTouchStartPhone(200, 100, 400, 300);
		assertNotNull(ec.getMovePosition(), "second finger should start panning");
		ec.twoTouchMovePhone(200, 100, 450, 320);

		assertEquals(50, view.getXZero(), DELTA);
		assertEquals(20, view.getYZero(), DELTA);
		assertEquals(50, view.getXscale(), DELTA);
		assertEquals(50, view.getYscale(), DELTA);
		assertEquals(EuclidianConstants.MODE_SEGMENT, ec.getMode());

		// the second finger lifts first, then the first finger finishes the segment
		ec.setMovePosition(null);
		ec.touchEndPhone(touch(200, 100));
		assertNull(ec.getMovePosition());
		assertEquals(List.of("A = (2, -2)", "B = (3, -1.6)", "f = 1.07703"), content());
	}

	@Test
	void lateSecondFingerShouldPanViewUnderPreview() {
		TouchGestureArbiter arbiter = new TouchGestureArbiter(new PhoneTouchGestureTarget(ec));
		app.setMode(EuclidianConstants.MODE_SEGMENT);
		tap(100, 100);
		arbiter.pointerDown(1, touch(200, 100), false);
		arbiter.onRun();
		arbiter.pointerDown(2, touch(400, 300), false);
		arbiter.pointerMove(2, touch(450, 320));

		assertEquals(50, view.getXZero(), DELTA);
		assertEquals(20, view.getYZero(), DELTA);
		assertEquals(50, view.getXscale(), DELTA);

		arbiter.pointerUp(2, touch(450, 320));
		arbiter.pointerUp(1, touch(200, 100));
		assertEquals(List.of("A = (2, -2)", "B = (3, -1.6)", "f = 1.07703"), content());
	}

	@Test
	void lateSecondFingerShouldNotPanViewWhenNavigationIsDisabled() {
		app.setShiftDragZoomEnabled(false);
		TouchGestureArbiter arbiter = new TouchGestureArbiter(new PhoneTouchGestureTarget(ec));
		app.setMode(EuclidianConstants.MODE_SEGMENT);
		tap(100, 100);
		arbiter.pointerDown(1, touch(200, 100), false);
		arbiter.onRun();
		arbiter.pointerDown(2, touch(400, 300), false);
		arbiter.pointerMove(2, touch(450, 320));

		assertEquals(0, view.getXZero(), DELTA);
		assertEquals(0, view.getYZero(), DELTA);

		arbiter.pointerUp(2, touch(450, 320));
		arbiter.pointerUp(1, touch(200, 100));
		assertEquals(List.of("A = (2, -2)", "B = (4, -2)", "f = 2"), content());
	}

	@Test
	void secondFingerInsideDelayShouldNavigateDespitePreview() {
		TouchGestureArbiter arbiter = new TouchGestureArbiter(new PhoneTouchGestureTarget(ec));
		app.setMode(EuclidianConstants.MODE_POLYGON);
		tap(100, 100);
		tap(200, 100);
		assertNotNull(view.getPreviewDrawable(), "construction in progress should have a preview");
		arbiter.pointerDown(1, touch(300, 300), false);
		arbiter.pointerDown(2, touch(500, 300), false);
		arbiter.pointerMove(1, touch(250, 300));
		arbiter.pointerMove(2, touch(550, 300));
		arbiter.pointerUp(1, touch(250, 300));
		arbiter.pointerUp(2, touch(550, 300));

		assertEquals(75, view.getXscale(), DELTA);
		assertEquals(List.of("A = (2, -2)", "B = (4, -2)"), content());
		assertEquals(EuclidianConstants.MODE_POLYGON, ec.getMode());
	}
}
