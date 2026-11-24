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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
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
import org.mockito.ArgumentCaptor;

import elemental2.dom.EventListener;
import elemental2.dom.EventTarget;
import elemental2.dom.HTMLCanvasElement;

/**
 * Pins what {@link PointerEventHandler} tells a controller for mouse, pen and touch sequences.
 * The controller is a plain mock, so this is the contract that the 3D view and Notes rely on.
 */
@Issue("APPS-8112")
@RunWith(GgbMockitoTestRunner.class)
public class PointerEventHandlerTest {

	private IsEuclidianController controller;
	private LongTouchManager longTouchManager;
	private Element element;
	private HTMLCanvasElement canvas;
	private final Map<String, EventListener> elementListeners = new HashMap<>();

	/**
	 * Attach a handler to a mocked view element and collect its listeners.
	 */
	@Before
	public void setUp() {
		ElementalMocker.setupElemental();
		controller = mock(IsEuclidianController.class);
		longTouchManager = mock(LongTouchManager.class);
		MouseTouchGestureControllerW offsets = mock(MouseTouchGestureControllerW.class);
		when(controller.getOffsets()).thenReturn(offsets);
		when(controller.getLongTouchManager()).thenReturn(longTouchManager);
		HasOffsets zoomLevel = mock(HasOffsets.class);
		when(zoomLevel.getZoomLevel()).thenReturn(1.0);

		element = mock(Element.class);
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
		new PointerEventHandler(controller, zoomLevel).attachTo(element, registry);
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

	private void down(int pointerId, String pointerType, double x, double y) {
		fire("pointerdown", pointerId, pointerType, x, y);
	}

	private void move(int pointerId, String pointerType, double x, double y) {
		fire("pointermove", pointerId, pointerType, x, y);
	}

	private void up(int pointerId, String pointerType, double x, double y) {
		fire("pointerup", pointerId, pointerType, x, y);
	}

	private AbstractEvent startedEvent() {
		ArgumentCaptor<AbstractEvent> captor = ArgumentCaptor.forClass(AbstractEvent.class);
		verify(controller).onPointerEventStart(captor.capture());
		return captor.getValue();
	}

	private PointerEvent endedEvent() {
		ArgumentCaptor<PointerEvent> captor = ArgumentCaptor.forClass(PointerEvent.class);
		verify(controller).onPointerEventEnd(captor.capture());
		return captor.getValue();
	}

	private void assertEvent(AbstractEvent event, PointerEventType type, int x, int y) {
		assertEquals(type, event.getType());
		assertEquals(x, event.getX());
		assertEquals(y, event.getY());
	}

	@Test
	public void mouseShouldStartMoveAndEnd() {
		down(1, "mouse", 100, 110);
		assertEvent(startedEvent(), PointerEventType.MOUSE, 100, 110);
		verify(controller).setDefaultEventType(PointerEventType.MOUSE, true);

		move(1, "mouse", 120, 130);
		ArgumentCaptor<PointerEvent> moved = ArgumentCaptor.forClass(PointerEvent.class);
		verify(controller).onPointerEventMove(moved.capture());
		assertEvent(moved.getValue(), PointerEventType.MOUSE, 120, 130);

		up(1, "mouse", 120, 130);
		assertEvent(endedEvent(), PointerEventType.MOUSE, 120, 130);
		verify(controller, never()).twoTouchStart(anyDouble(), anyDouble(), anyDouble(), anyDouble());
	}

	@Test
	public void mouseHoverShouldMoveWithoutPointerDown() {
		move(1, "mouse", 120, 130);
		verify(controller).onPointerEventMove(any(PointerEvent.class));
		verify(controller, never()).onPointerEventStart(any(AbstractEvent.class));
	}

	@Test
	public void penShouldStartMoveAndEnd() {
		down(7, "pen", 100, 110);
		assertEvent(startedEvent(), PointerEventType.PEN, 100, 110);
		verify(controller).setDefaultEventType(PointerEventType.PEN, true);

		move(7, "pen", 120, 130);
		verify(controller).onPointerEventMove(any(PointerEvent.class));

		up(7, "pen", 120, 130);
		assertEvent(endedEvent(), PointerEventType.PEN, 120, 130);
	}

	@Test
	public void singleTouchShouldStartMoveAndEnd() {
		down(3, "touch", 100, 110);
		assertEvent(startedEvent(), PointerEventType.TOUCH, 100, 110);
		verify(controller).setDefaultEventType(PointerEventType.TOUCH, true);

		move(3, "touch", 120, 130);
		verify(controller).onPointerEventMove(any(PointerEvent.class));

		up(3, "touch", 120, 130);
		assertEvent(endedEvent(), PointerEventType.TOUCH, 120, 130);
		verify(controller, never()).twoTouchStart(anyDouble(), anyDouble(), anyDouble(), anyDouble());
	}

	@Test
	public void touchMoveOfUnknownPointerShouldBeIgnored() {
		move(3, "touch", 120, 130);
		verify(controller, never()).onPointerEventMove(any(PointerEvent.class));
	}

	@Test
	public void twoTouchesShouldStartAndMoveTwoTouchGesture() {
		down(3, "touch", 100, 300);
		down(4, "touch", 300, 300);
		verify(controller).onPointerEventStart(any(AbstractEvent.class));
		verify(controller).twoTouchStart(100, 300, 300, 300);

		// only movement of the second pointer is reported; it carries both positions
		move(3, "touch", 50, 300);
		verify(controller, never()).twoTouchMove(anyDouble(), anyDouble(), anyDouble(), anyDouble());
		move(4, "touch", 350, 300);
		verify(controller).twoTouchMove(50, 300, 350, 300);
		verify(controller, never()).onPointerEventMove(any(PointerEvent.class));
	}

	@Test
	public void touchAndPenShouldNotStartTwoTouchGesture() {
		down(3, "touch", 100, 300);
		down(7, "pen", 300, 300);
		move(7, "pen", 350, 300);
		verify(controller, never()).twoTouchStart(anyDouble(), anyDouble(), anyDouble(), anyDouble());
		verify(controller, never()).twoTouchMove(anyDouble(), anyDouble(), anyDouble(), anyDouble());
	}

	@Test
	public void bothTouchesShouldEndWhenLifted() {
		down(3, "touch", 100, 300);
		down(4, "touch", 300, 300);
		up(4, "touch", 300, 300);
		up(3, "touch", 100, 300);
		verify(controller, times(2)).onPointerEventEnd(any(PointerEvent.class));
	}

	@Test
	public void thirdTouchShouldNotStartOrMoveAnything() {
		down(3, "touch", 100, 300);
		down(4, "touch", 300, 300);
		down(5, "touch", 200, 400);
		verify(controller).onPointerEventStart(any(AbstractEvent.class));
		verify(controller).twoTouchStart(anyDouble(), anyDouble(), anyDouble(), anyDouble());

		move(5, "touch", 220, 420);
		verify(controller, never()).twoTouchMove(anyDouble(), anyDouble(), anyDouble(), anyDouble());
		verify(controller, never()).onPointerEventMove(any(PointerEvent.class));
	}

	@Test
	public void pointerCancelShouldNotifyController() {
		down(3, "touch", 100, 300);
		fire("pointercancel", 3, "touch", 100, 300);
		verify(controller).onPointerCancel();
		verify(controller, never()).onPointerEventEnd(any(PointerEvent.class));
	}

	@Test
	public void touchShouldScheduleLongTouchAndCancelItOnSecondTouch() {
		down(3, "touch", 100, 300);
		verify(longTouchManager).scheduleTimer(controller, 100, 300);
		down(4, "touch", 300, 300);
		verify(longTouchManager).cancelTimer();
	}
}
