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

package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.states.DerivedState;
import org.geogebra.common.states.MutableState;
import org.geogebra.common.states.State;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Coordinates the table values content and button state for the probability calculator view. */
public final class ProbabilityCalculatorTableValuesViewModel
		implements ProbabilityCalculatorView.Listener {
	private final @NonNull ProbabilityCalculatorView probabilityCalculatorView;
	private final @NonNull MutableState<@Nullable ProbabilityCalculatorTableValues> tableValues;
	private final @NonNull MutableState<@NonNull Boolean> buttonVisible;
	private final @NonNull State<@NonNull ButtonState> buttonState;

	/** Describes the state of the table values button. */
	public enum ButtonState {
		/** Visible and active table values button (table shown) */
		ACTIVE,
		/** Visible and inactive table values button (table not shown) */
		INACTIVE,
		/** Unavailable/invisible table values button. */
		HIDDEN
	}

	/**
	 * Constructs the table values view.
	 * @param probabilityCalculatorView internal probability calculator view
	 */
	public ProbabilityCalculatorTableValuesViewModel(
			@NonNull ProbabilityCalculatorView probabilityCalculatorView) {
		this.probabilityCalculatorView = probabilityCalculatorView;
		this.tableValues = new MutableState<>(null);
		this.buttonVisible = new MutableState<>(probabilityCalculatorView.isDiscreteProbability());
		this.buttonState = DerivedState.of(tableValues, buttonVisible,
				(tableValues, buttonVisible) -> buttonVisible ? tableValues != null
						? ButtonState.ACTIVE : ButtonState.INACTIVE : ButtonState.HIDDEN);
		probabilityCalculatorView.addListener(this);
	}

	/**
	 * @return table values content, or {@code null} when closed
	 */
	public @NonNull State<@Nullable ProbabilityCalculatorTableValues> getContent() {
		return tableValues;
	}

	/**
	 * @return current button state
	 */
	public @NonNull State<ButtonState> getButtonState() {
		return buttonState;
	}

	/** Handles tapping on the table values button. */
	public void onButtonTapped() {
		tableValues.set(tableValues.get() == null
				? ProbabilityCalculatorTableValues.from(probabilityCalculatorView) : null);
	}

	/** Handles closing the view, called when the view is collapsed or closed via the x button. */
	public void onClosed() {
		tableValues.set(null);
	}

	/** Detaches the view from listening to probability view changes and sending notifications. */
	public void detach() {
		probabilityCalculatorView.removeListener(this);
	}

	@Override
	public void probabilityCalculatorViewChanged() {
		if (tableValues.get() != null) {
			tableValues.set(ProbabilityCalculatorTableValues.from(probabilityCalculatorView));
		}
		buttonVisible.set(probabilityCalculatorView.isDiscreteProbability());
	}
}
