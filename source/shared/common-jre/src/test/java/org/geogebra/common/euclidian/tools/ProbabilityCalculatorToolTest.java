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
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROBABILITY_CALCULATOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SELECT_MOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;

import org.geogebra.common.gui.GuiManager;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InOrder;
import org.mockito.Mockito;

/** Tests the real GUI-manager probability tool dispatch with platform views mocked. */
class ProbabilityCalculatorToolTest extends BaseToolTest {
	@ParameterizedTest
	@EnumSource(ModeSetter.class)
	void hiddenCalculatorShouldOpenWithNormalDistributionAndReturnToMove(@NonNull ModeSetter setter) {
		ProbabilityCalculatorView calculator = Mockito.mock(ProbabilityCalculatorView.class);
		ProbabilityGui gui = gui(calculator);
		Set<GeoElement> before = getConstructionSnapshot();
		getApp().setUndoActive(true);
		getApp().storeUndoInfo();

		getApp().setMode(MODE_PROBABILITY_CALCULATOR, setter);
		assertEquals(before, getConstructionSnapshot());

		InOrder order = Mockito.inOrder(gui, calculator);
		order.verify(gui).setShowView(true, App.VIEW_PROBABILITY_CALCULATOR);
		order.verify(calculator).setProbabilityCalculator(Dist.NORMAL, null, false);
		order.verify(gui).setMode(MODE_MOVE, ModeSetter.TOOLBAR);
		assertEquals(MODE_MOVE, getApp().getMode());
		assertFalse(getKernel().undoPossible());
	}

	@Test
	void visibleCalculatorShouldBeFocusedWithoutResettingDistribution() {
		ProbabilityCalculatorView calculator = Mockito.mock(ProbabilityCalculatorView.class);
		ProbabilityGui gui = gui(calculator);
		gui.setCalculator(calculator);
		Mockito.doReturn(true).when(gui).showView(App.VIEW_PROBABILITY_CALCULATOR);
		Layout layout = Mockito.mock(Layout.class, Mockito.RETURNS_DEEP_STUBS);
		Mockito.doReturn(layout).when(gui).getLayout();

		getApp().setMode(MODE_PROBABILITY_CALCULATOR, ModeSetter.TOOLBAR);

		Mockito.verify(layout.getDockManager()).setFocusedPanel(App.VIEW_PROBABILITY_CALCULATOR);
		Mockito.verifyNoInteractions(calculator);
		Mockito.verify(gui, Mockito.never()).setShowView(true, App.VIEW_PROBABILITY_CALCULATOR);
		assertEquals(MODE_MOVE, getApp().getMode());
	}

	@Test
	void repeatedActivationShouldFocusAlreadyVisibleCalculator() {
		ProbabilityCalculatorView calculator = Mockito.mock(ProbabilityCalculatorView.class);
		ProbabilityGui gui = gui(calculator);
		Layout layout = Mockito.mock(Layout.class, Mockito.RETURNS_DEEP_STUBS);
		Mockito.doReturn(layout).when(gui).getLayout();
		Mockito.doAnswer(invocation -> {
					gui.setCalculator(calculator);
					Mockito.doReturn(true).when(gui).showView(App.VIEW_PROBABILITY_CALCULATOR);
					return null;
				})
				.when(gui)
				.setShowView(true, App.VIEW_PROBABILITY_CALCULATOR);

		setMode(MODE_PROBABILITY_CALCULATOR);
		setMode(MODE_PROBABILITY_CALCULATOR);

		Mockito.verify(calculator).setProbabilityCalculator(Dist.NORMAL, null, false);
		Mockito.verify(layout.getDockManager()).setFocusedPanel(App.VIEW_PROBABILITY_CALCULATOR);
		assertEquals(MODE_MOVE, getApp().getMode());
	}

	@Test
	void activationShouldNotChangeExistingConstruction() {
		GeoPoint point = add("A=(2,-2)");
		gui(Mockito.mock(ProbabilityCalculatorView.class));
		setMode(MODE_PROBABILITY_CALCULATOR);
		assertEquals(Set.of(point), getConstructionSnapshot());
		assertEquals(MODE_MOVE, getApp().getMode());
	}

	@Test
	void activationWithoutGuiShouldNotCreateConstructionObjects() {
		setMode(MODE_PROBABILITY_CALCULATOR);
		clickRW(2, -2);
		assertEquals(MODE_PROBABILITY_CALCULATOR, getApp().getMode());
		assertFalse(ec.isUndoableMode());
		assertEquals(Set.of(), getConstructionSnapshot());
	}

	@Test
	void notesShouldReturnToSelectionAfterOpeningCalculator() {
		setupNotesApp();
		ec = getApp().getActiveEuclidianView().getEuclidianController();
		gui(Mockito.mock(ProbabilityCalculatorView.class));

		setMode(MODE_PROBABILITY_CALCULATOR);

		assertEquals(MODE_SELECT_MOW, getApp().getMode());
	}

	@Test
	void unavailableToolbarModeShouldNotOpenCalculator() {
		ProbabilityCalculatorView calculator = Mockito.mock(ProbabilityCalculatorView.class);
		ProbabilityGui gui = gui(calculator);
		Mockito.doReturn(MODE_MOVE)
				.when(gui)
				.setToolbarMode(MODE_PROBABILITY_CALCULATOR, ModeSetter.TOOLBAR);

		setMode(MODE_PROBABILITY_CALCULATOR);

		Mockito.verify(gui, Mockito.never()).setShowView(true, App.VIEW_PROBABILITY_CALCULATOR);
		Mockito.verifyNoInteractions(calculator);
		assertEquals(MODE_MOVE, getApp().getMode());
	}

	private @NonNull ProbabilityGui gui(@NonNull ProbabilityCalculatorView calculator) {
		ProbabilityGui gui = Mockito.mock(
				ProbabilityGui.class,
				Mockito.withSettings().useConstructor(getApp()).defaultAnswer(Mockito.CALLS_REAL_METHODS));
		Mockito.doAnswer(invocation -> {
					gui.setCalculator(calculator);
					return null;
				})
				.when(gui)
				.setShowView(true, App.VIEW_PROBABILITY_CALCULATOR);
		getApp().setGuiManager(gui);
		return gui;
	}

	/** Keeps real mode dispatch while supplying the platform toolbar contract. */
	abstract static class ProbabilityGui extends GuiManager {
		/** @param app headless application */
		ProbabilityGui(@NonNull App app) {
			super(app);
		}

		@Override
		protected int setToolbarMode(int mode, @NonNull ModeSetter setter) {
			return mode;
		}

		void setCalculator(@NonNull ProbabilityCalculatorView calculator) {
			setProbCalculator(calculator);
		}
	}
}
