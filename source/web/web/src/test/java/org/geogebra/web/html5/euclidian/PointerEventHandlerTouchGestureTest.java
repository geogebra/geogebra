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

package org.geogebra.web.html5.euclidian;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.test.annotation.Issue;
import org.geogebra.web.html5.event.HasOffsets;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.util.GlobalHandlerRegistry;
import org.geogebra.web.test.ElementalMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.gwtproject.dom.client.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import elemental2.dom.EventListener;
import elemental2.dom.EventTarget;
import elemental2.dom.HTMLCanvasElement;

/**
 * Finger touches on a view whose controller uses the touch gesture arbiter (2D views outside
 * Notes).
 */
@Issue("APPS-8112")
@RunWith(GgbMockitoTestRunner.class)
public class PointerEventHandlerTouchGestureTest {

	private IsEuclidianController controller;
	private HTMLCanvasElement canvas;
	private GTimerListener startDelay;
	private final Map<String, EventListener> elementListeners = new HashMap<>();

	/**
	 * Attach a handler to a mocked view element and collect its listeners.
	 */
	@Before
	public void setUp() {
		ElementalMocker.setupElemental();
		UtilFactory utilFactory = mock(UtilFactory.class);
		when(utilFactory.newTimer(any(GTimerListener.class), anyInt())).thenAnswer(invocation -> {
			startDelay = (GTimerListener) invocation.getArguments()[0];
			return mock(GTimer.class);
		});
		UtilFactory.setPrototype(utilFactory);

		controller = mock(IsEuclidianController.class);
		MouseTouchGestureControllerW offsets = mock(MouseTouchGestureControllerW.class);
		when(controller.getOffsets()).thenReturn(offsets);
		when(controller.getLongTouchManager()).thenReturn(mock(LongTouchManager.class));
		when(controller.usesTouchGestureArbiter()).thenReturn(true);
		when(controller.getTouchStartDelay()).thenReturn(150);
		HasOffsets zoomLevel = mock(HasOffsets.class);
		when(zoomLevel.getZoomLevel()).thenReturn(1.0);

		canvas = new HTMLCanvasElement();
		canvas.tagName = "CANVAS";
		GlobalHandlerRegistry registry = new GlobalHandlerRegistry() {
			@Override
			public void addEventListener(Object target, String type, EventListener listener) {
				elementListeners.put(type, listener);
			}

			@Override
			public void addEventListener(EventTarget target, String type, EventListener listener) {
				// listener on the window: not needed, pointers are released inside the view
			}
		};
		new PointerEventHandler(controller, zoomLevel).attachTo(mock(Element.class), registry);
	}

	private void fire(String eventType, int pointerId, String pointerType, double x, double y) {
		elemental2.dom.PointerEvent event = new elemental2.dom.PointerEvent(eventType);
		event.pointerId = pointerId;
		event.pointerType = pointerType;
		event.offsetX = x;
		event.offsetY = y;
		event.target = canvas;
		elementListeners.get(eventType).handleEvent(event);
	}

	private void down(int pointerId, double x, double y) {
		fire("pointerdown", pointerId, "touch", x, y);
	}

	private void move(int pointerId, double x, double y) {
		fire("pointermove", pointerId, "touch", x, y);
	}

	private void up(int pointerId, double x, double y) {
		fire("pointerup", pointerId, "touch", x, y);
		fire("pointerout", pointerId, "touch", x, y);
	}

	private void startDelayPasses() {
		startDelay.onRun();
	}

	private void verifyNoToolInput() {
		verify(controller, never()).onPointerEventStart(any(AbstractEvent.class));
		verify(controller, never()).onPointerEventMove(any(PointerEvent.class));
		verify(controller, never()).onPointerEventEnd(any(PointerEvent.class));
	}

	private void verifyNoTwoTouchGesture() {
		verify(controller, never()).twoTouchStart(anyDouble(), anyDouble(), anyDouble(), anyDouble());
		verify(controller, never())
				.touchNavigationStart(anyDouble(), anyDouble(), anyDouble(), anyDouble());
		verify(controller, never()).twoTouchMove(anyDouble(), anyDouble(), anyDouble(), anyDouble());
	}

	@Test
	public void tapShouldReachToolWhenFingerLifts() {
		down(3, 100, 110);
		verifyNoToolInput();
		up(3, 100, 110);
		InOrder order = inOrder(controller);
		order.verify(controller).onPointerEventStart(any(AbstractEvent.class));
		order.verify(controller).onPointerEventEnd(any(PointerEvent.class));
		verifyNoTwoTouchGesture();
	}

	@Test
	public void heldFingerShouldReachToolWithItsMovesWhenDelayPasses() {
		down(3, 100, 110);
		move(3, 120, 110);
		verifyNoToolInput();
		startDelayPasses();
		InOrder order = inOrder(controller);
		order.verify(controller).onPointerEventStart(any(AbstractEvent.class));
		order.verify(controller).onPointerEventMove(any(PointerEvent.class));
		move(3, 140, 110);
		up(3, 140, 110);
		order.verify(controller).onPointerEventMove(any(PointerEvent.class));
		order.verify(controller).onPointerEventEnd(any(PointerEvent.class));
	}

	@Test
	public void secondFingerInsideDelayShouldNavigateWithoutToolInput() {
		down(3, 100, 300);
		down(4, 300, 300);
		verify(controller).touchNavigationStart(100, 300, 300, 300);
		move(3, 50, 300);
		verify(controller).twoTouchMove(50, 300, 300, 300);
		move(4, 350, 300);
		verify(controller).twoTouchMove(50, 300, 350, 300);
		up(4, 350, 300);
		verify(controller, never()).touchNavigationEnd();
		move(3, 60, 310);
		up(3, 60, 310);
		verify(controller).touchNavigationEnd();
		verifyNoToolInput();
		verify(controller, never()).twoTouchStart(anyDouble(), anyDouble(), anyDouble(), anyDouble());
	}

	@Test
	public void freshTouchAfterNavigationShouldReachTool() {
		down(3, 100, 300);
		down(4, 300, 300);
		up(3, 100, 300);
		up(4, 300, 300);
		down(5, 200, 200);
		up(5, 200, 200);
		verify(controller).onPointerEventStart(any(AbstractEvent.class));
		verify(controller).onPointerEventEnd(any(PointerEvent.class));
	}

	@Test
	public void lateSecondFingerShouldBeIgnoredWithConstructionTool() {
		down(3, 100, 300);
		startDelayPasses();
		down(4, 300, 300);
		move(4, 350, 300);
		up(4, 350, 300);
		verifyNoTwoTouchGesture();
		verify(controller, never()).onPointerEventEnd(any(PointerEvent.class));
		verify(controller, never()).onPointerEventMove(any(PointerEvent.class));

		move(3, 120, 300);
		up(3, 120, 300);
		verify(controller).onPointerEventMove(any(PointerEvent.class));
		verify(controller).onPointerEventEnd(any(PointerEvent.class));
	}

	@Test
	public void lateSecondFingerShouldStartTwoTouchGestureWithMoveTool() {
		when(controller.keepsTwoTouchBehavior()).thenReturn(true);
		down(3, 100, 300);
		startDelayPasses();
		down(4, 300, 300);
		verify(controller).twoTouchStart(100, 300, 300, 300);
		move(4, 350, 300);
		verify(controller).twoTouchMove(100, 300, 350, 300);
		up(4, 350, 300);
		up(3, 100, 300);
		verify(controller, times(2)).onPointerEventEnd(any(PointerEvent.class));
		verify(controller, never())
				.touchNavigationStart(anyDouble(), anyDouble(), anyDouble(), anyDouble());
	}

	@Test
	public void fingerShouldBeIgnoredWhilePenIsDown() {
		fire("pointerdown", 7, "pen", 100, 100);
		verify(controller).onPointerEventStart(any(AbstractEvent.class));
		down(3, 300, 300);
		move(3, 310, 310);
		up(3, 310, 310);
		verify(controller).onPointerEventStart(any(AbstractEvent.class));
		verify(controller, never()).onPointerEventEnd(any(PointerEvent.class));
		verifyNoTwoTouchGesture();

		fire("pointermove", 7, "pen", 120, 120);
		verify(controller).onPointerEventMove(any(PointerEvent.class));
		fire("pointerup", 7, "pen", 120, 120);
		verify(controller).onPointerEventEnd(any(PointerEvent.class));
	}

	@Test
	public void pointerCancelShouldEndNavigation() {
		down(3, 100, 300);
		down(4, 300, 300);
		fire("pointercancel", 3, "touch", 100, 300);
		fire("pointercancel", 4, "touch", 300, 300);
		verify(controller).touchNavigationEnd();
		verify(controller, never()).onPointerCancel();
		verifyNoToolInput();

		down(5, 200, 200);
		up(5, 200, 200);
		verify(controller).onPointerEventStart(any(AbstractEvent.class));
	}

	@Test
	public void penShouldEndFingerThatActsOnTool() {
		down(3, 100, 300);
		startDelayPasses();
		fire("pointerdown", 7, "pen", 120, 120);
		verify(controller).onPointerEventEnd(any(PointerEvent.class));
	}

	@Test
	public void pointerCancelShouldDropHeldFinger() {
		down(3, 100, 300);
		fire("pointercancel", 3, "touch", 100, 300);
		startDelayPasses();
		verifyNoToolInput();
		verify(controller, never()).onPointerCancel();
	}

	@Test
	public void newPrimaryTouchShouldRecoverFromLostFingers() {
		down(3, 100, 300);
		down(4, 300, 300);
		// the fingers are lifted while the page does not get events, e.g. after an app switch
		elemental2.dom.PointerEvent event = new elemental2.dom.PointerEvent("pointerdown");
		event.pointerId = 9;
		event.pointerType = "touch";
		event.isPrimary = true;
		event.offsetX = 200;
		event.offsetY = 200;
		event.target = canvas;
		elementListeners.get("pointerdown").handleEvent(event);
		up(9, 200, 200);
		verify(controller).touchNavigationEnd();
		verify(controller).onPointerEventStart(any(AbstractEvent.class));
		verify(controller).onPointerEventEnd(any(PointerEvent.class));
	}

	@Test
	public void mouseShouldNotBeHeldBack() {
		fire("pointerdown", 1, "mouse", 100, 110);
		verify(controller).onPointerEventStart(any(AbstractEvent.class));
	}
}
