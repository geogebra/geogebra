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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_FUNCTION_INSPECTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.jre.headless.DialogManagerNoGui;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

/** Tests function selection and the Function Inspector dialog handoff. */
class FunctionInspectorToolTest extends BaseDialogToolTest {
	@Test
	@Issue("APPS-7920")
	void previewWithExistingFunctionShouldNotOpenOrConsumeSelection() {
		GeoFunction function = add("f(x)=-x");
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_FUNCTION_INSPECTOR);
			getApp().getSelectionManager().getSelectedFunctionList().add(function);
			moveMouseRW(2, -2);
			Mockito.verify(dialog(dialogs), Mockito.never()).showFunctionInspector(Mockito.any());
			assertEquals(MODE_FUNCTION_INSPECTOR, getApp().getMode());
			assertEquals(List.of(function), getApp().getSelectionManager().getSelectedFunctionList());
		}
	}

	@Test
	void enteringHoveringAndEmptyClickShouldNotOpenInspector() {
		add("f(x)=x^2");
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_FUNCTION_INSPECTOR);
			moveMouseRW(0, 0);
			clickRW(4, -4);
			Mockito.verify(dialog(dialogs), Mockito.never()).showFunctionInspector(Mockito.any());
			assertEquals(MODE_FUNCTION_INSPECTOR, getApp().getMode());
			assertEquals(1, getConstructionSnapshot().size());
		}
	}

	@ParameterizedTest
	@ValueSource(strings = {"A=(2,-2)", "g:y=-2", "a=2", "c:Circle((2,-2),1)", "f(x,y)=x+y"})
	void unsupportedObjectsShouldNotOpenInspector(@NonNull String command) {
		GeoElement geo = add(command);
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_FUNCTION_INSPECTOR);
			Hits hits = new Hits();
			hits.add(geo);
			assertFalse(ec.processMode(hits, false, false));
			Mockito.verify(dialog(dialogs), Mockito.never()).showFunctionInspector(Mockito.any());
			assertEquals(MODE_FUNCTION_INSPECTOR, getApp().getMode());
		}
	}

	@ParameterizedTest
	@EnumSource(PointerEventType.class)
	void functionClickShouldSwitchToMoveBeforeOpeningInspector(@NonNull PointerEventType type) {
		GeoFunction function = add("f(x)=-x");
		enableUndo();
		Set<?> before = getConstructionSnapshot();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			Mockito.when(dialog(dialogs).showFunctionInspector(function)).thenAnswer(invocation -> {
				assertEquals(MODE_MOVE, getApp().getMode());
				return true;
			});
			setMode(MODE_FUNCTION_INSPECTOR);
			click(100, 100, type);
			Mockito.verify(dialog(dialogs)).showFunctionInspector(function);
			assertEquals(MODE_MOVE, getApp().getMode());
			assertEquals(before, getConstructionSnapshot());
			assertFalse(getKernel().undoPossible());
		}
	}

	@Test
	void overlappingPointShouldNotHideFunctionFromInspector() {
		GeoFunction function = add("f(x)=-x");
		GeoPoint point = add("A=(2,-2)");
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			setMode(MODE_FUNCTION_INSPECTOR);
			clickRW(2, -2);
			Mockito.verify(dialog(dialogs)).showFunctionInspector(function);
			assertEquals(2, point.getInhomX(), 1E-8);
			assertEquals(-2, point.getInhomY(), 1E-8);
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void dialogResultShouldNotCreateAnUndoStep(boolean result) {
		GeoFunction function = add("f(x)=-x");
		enableUndo();
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			Mockito.when(dialog(dialogs).showFunctionInspector(function)).thenReturn(result);
			setMode(MODE_FUNCTION_INSPECTOR);
			clickRW(2, -2);
			assertFalse(getKernel().undoPossible());
			assertEquals(Set.of(function), getConstructionSnapshot());
			assertEquals(MODE_MOVE, getApp().getMode());
		}
	}

	@Test
	void inspectorMayInstallSelectionListenerDuringDialogHandoff() {
		add("f(x)=-x");
		GeoPoint point = add("A=(4,-3)");
		GeoElementSelectionListener listener = Mockito.mock(GeoElementSelectionListener.class);
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			Mockito.when(dialog(dialogs).showFunctionInspector(Mockito.any())).thenAnswer(invocation -> {
				getApp().setSelectionListenerMode(listener);
				return true;
			});
			setMode(MODE_FUNCTION_INSPECTOR);
			clickRW(2, -2);
			assertNotNull(getApp().getCurrentSelectionListener());
			Mockito.verifyNoInteractions(listener);
			clickRW(4, -3);
			Mockito.verify(listener).geoElementSelected(point, false);
		}
	}

	@Test
	@Issue("APPS-7920")
	void missingDialogManagerShouldLeaveInspectorModeUsable() {
		add("f(x)=-x");
		GeoFunction nextFunction = add("g(x)=-x-1");
		setMode(MODE_FUNCTION_INSPECTOR);
		clickRW(2, -2);
		assertEquals(MODE_FUNCTION_INSPECTOR, getApp().getMode());
		assertEquals(2, getConstructionSnapshot().size());
		assertTrue(getApp().getSelectionManager().getSelectedFunctionList().isEmpty());
		try (MockedConstruction<DialogManagerNoGui> dialogs = mockDialogs()) {
			resetMouseLocation();
			clickRW(2, -3);
			Mockito.verify(dialog(dialogs)).showFunctionInspector(nextFunction);
			assertEquals(MODE_MOVE, getApp().getMode());
		}
	}
}
