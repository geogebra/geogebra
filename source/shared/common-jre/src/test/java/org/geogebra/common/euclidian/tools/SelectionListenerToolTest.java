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
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SELECTION_LISTENER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.test.TestEvent;
import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.Mockito;

/** Tests object delivery, preview isolation and selection-listener lifecycle. */
class SelectionListenerToolTest extends BaseToolTest {
	@Test
	void controlClickShouldRequestAddToSelectionThroughPointerEvents() {
		GeoElement point = add("A=(2,-2)");
		GeoElementSelectionListener listener = Mockito.mock(GeoElementSelectionListener.class);
		getApp().setSelectionListenerMode(listener);
		TestEvent event = new TestEvent(100, 100) {
			@Override
			public boolean isControlDown() {
				return true;
			}
		};

		ec.wrapMousePressed(event);
		ec.wrapMouseReleased(event);

		Mockito.verify(listener).geoElementSelected(point, true);
		Mockito.verifyNoMoreInteractions(listener);
	}

	@Test
	void enteringHoveringAndEmptyClickShouldNotNotifyListener() {
		GeoElement point = add("A=(2,-2)");
		GeoElementSelectionListener listener = Mockito.mock(GeoElementSelectionListener.class);
		getApp().setSelectionListenerMode(listener);
		moveMouseRW(2, -2);
		clickRW(4, -4);
		Mockito.verifyNoInteractions(listener);
		assertSame(listener, getApp().getCurrentSelectionListener());
		assertEquals(MODE_SELECTION_LISTENER, getApp().getMode());
		assertEquals(Set.of(point), getConstructionSnapshot());
		assertFalse(ec.isUndoableMode());
	}

	@ParameterizedTest
	@EnumSource(PointerEventType.class)
	void clickShouldDeliverSelectedObjectExactlyOnce(@NonNull PointerEventType type) {
		GeoElement point = add("A=(2,-2)");
		getApp().setUndoActive(true);
		getApp().storeUndoInfo();
		GeoElementSelectionListener listener = Mockito.mock(GeoElementSelectionListener.class);
		getApp().setSelectionListenerMode(listener);
		click(100, 100, type);
		Mockito.verify(listener).geoElementSelected(point, false);
		Mockito.verifyNoMoreInteractions(listener);
		assertFalse(getKernel().undoPossible());
		assertEquals(Set.of(point), getConstructionSnapshot());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void controllerShouldForwardAddToSelectionFlag(boolean addToSelection) {
		GeoElement point = add("A=(2,-2)");
		GeoElementSelectionListener listener = Mockito.mock(GeoElementSelectionListener.class);
		getApp().setSelectionListenerMode(listener);
		Hits hits = new Hits();
		hits.add(point);
		assertFalse(ec.processMode(hits, addToSelection, false));
		Mockito.verify(listener).geoElementSelected(point, addToSelection);
	}

	@ParameterizedTest
	@ValueSource(strings = {"A=(2,-2)", "g:y=-2", "f(x)=-x", "n=2", "text=\"hello\""})
	void controllerShouldDeliverDifferentGeoTypes(@NonNull String definition) {
		GeoElement geo = add(definition);
		GeoElementSelectionListener listener = Mockito.mock(GeoElementSelectionListener.class);
		getApp().setSelectionListenerMode(listener);
		Hits hits = new Hits();
		hits.add(geo);
		ec.processMode(hits, false, false);
		Mockito.verify(listener).geoElementSelected(geo, false);
		assertTrue(getApp().getSelectionManager().getSelectedGeoList().isEmpty());
	}

	@Test
	void repeatedSelectionShouldNotRetainPreviousObject() {
		GeoElement a = add("A=(2,-2)");
		GeoElement b = add("B=(4,-4)");
		GeoElementSelectionListener listener = Mockito.mock(GeoElementSelectionListener.class);
		getApp().setSelectionListenerMode(listener);
		clickRW(2, -2);
		clickRW(4, -4);
		InOrder order = Mockito.inOrder(listener);
		order.verify(listener).geoElementSelected(a, false);
		order.verify(listener).geoElementSelected(b, false);
		order.verifyNoMoreInteractions();
	}

	@Test
	void removingListenerShouldReturnToMoveAndStopCallbacks() {
		add("A=(2,-2)");
		GeoElementSelectionListener listener = Mockito.mock(GeoElementSelectionListener.class);
		getApp().setSelectionListenerMode(listener);
		getApp().setSelectionListenerMode(null);
		clickRW(2, -2);
		assertEquals(MODE_MOVE, getApp().getMode());
		assertNull(getApp().getCurrentSelectionListener());
		Mockito.verifyNoInteractions(listener);
	}

	@Test
	void replacementListenerShouldReceiveNextClick() {
		GeoElement point = add("A=(2,-2)");
		GeoElementSelectionListener first = Mockito.mock(GeoElementSelectionListener.class);
		GeoElementSelectionListener second = Mockito.mock(GeoElementSelectionListener.class);
		getApp().setSelectionListenerMode(first);
		getApp().setSelectionListenerMode(second);
		clickRW(2, -2);
		Mockito.verifyNoInteractions(first);
		Mockito.verify(second).geoElementSelected(point, false);
	}

	@Test
	void listenerMayRemoveItselfDuringCallback() {
		add("A=(2,-2)");
		getApp().setSelectionListenerMode((geo, add) -> getApp().setSelectionListenerMode(null));
		clickRW(2, -2);
		assertNull(getApp().getCurrentSelectionListener());
		assertEquals(MODE_MOVE, getApp().getMode());
	}

	@Test
	void rightClickShouldNotNotifyListener() {
		add("A=(2,-2)");
		GeoElementSelectionListener listener = Mockito.mock(GeoElementSelectionListener.class);
		getApp().setSelectionListenerMode(listener);
		TestEvent event = new TestEvent(100, 100, PointerEventType.MOUSE, true);
		ec.wrapMousePressed(event);
		ec.wrapMouseReleased(event);
		Mockito.verifyNoInteractions(listener);
	}

	@Test
	void selectingWithoutListenerShouldBeHarmless() {
		GeoElement point = add("A=(2,-2)");
		setMode(MODE_SELECTION_LISTENER);
		clickRW(2, -2);
		assertEquals(Set.of(point), getConstructionSnapshot());
		assertNull(getApp().getCurrentSelectionListener());
	}

	@Test
	@Issue("APPS-7920")
	void previewWithExistingSelectionShouldNotConsumeOrDeliverIt() {
		GeoElement point = add("A=(2,-2)");
		GeoElementSelectionListener listener = Mockito.mock(GeoElementSelectionListener.class);
		getApp().setSelectionListenerMode(listener);
		getApp().getSelectionManager().getSelectedGeoList().add(point);
		moveMouseRW(2, -2);
		Mockito.verifyNoInteractions(listener);
		assertEquals(List.of(point), getApp().getSelectionManager().getSelectedGeoList());
	}

	@Test
	void clickingPendingSelectionShouldDeselectWithoutCallback() {
		GeoElement point = add("A=(2,-2)");
		GeoElementSelectionListener listener = Mockito.mock(GeoElementSelectionListener.class);
		getApp().setSelectionListenerMode(listener);
		getApp().getSelectionManager().getSelectedGeoList().add(point);
		Hits hits = new Hits();
		hits.add(point);

		ec.processMode(hits, false, false);

		Mockito.verifyNoInteractions(listener);
		assertTrue(getApp().getSelectionManager().getSelectedGeoList().isEmpty());
	}
}
