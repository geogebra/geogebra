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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Coordinates the table values content and button state for the probability calculator view. */
public final class ProbabilityCalculatorTableValuesViewModel
		implements ProbabilityCalculatorView.Listener {
	private final @NonNull ProbabilityCalculatorView probabilityCalculatorView;
	private @Nullable ProbabilityCalculatorTableValues tableValues;
	private boolean buttonVisible;
	private @Nullable Delegate delegate;

	/** Receives notifications when the content or button state changes. */
	public interface Delegate {
		/** Updates the UI using the current content and button state. */
		void update();
	}

	/** Describes the state of the table values button. */
	public enum ButtonState {
		/** Visible and active table values button (table shown) */
		ACTIVE,
		/** Visible and inactive table values button (table not shown) */
		INACTIVE,
		/** Unavailable/invisible table values button. */
		HIDDEN;
	}

	/**
	 * Constructs the table values view.
	 * @param probabilityCalculatorView internal probability calculator view
	 */
	public ProbabilityCalculatorTableValuesViewModel(
			@NonNull ProbabilityCalculatorView probabilityCalculatorView) {
		this.probabilityCalculatorView = probabilityCalculatorView;
		this.buttonVisible = probabilityCalculatorView.isDiscreteProbability();
		probabilityCalculatorView.addListener(this);
	}

	/**
	 * Sets the delegate notified when this view's content or button state changes.
	 * @param delegate delegate to notify, or {@code null} to stop notifications
	 */
	public void setDelegate(@Nullable Delegate delegate) {
		this.delegate = delegate;
	}

	/**
	 * @return table values content, or {@code null} when closed
	 */
	public @Nullable ProbabilityCalculatorTableValues getContent() {
		return tableValues;
	}

	/**
	 * @return current button state
	 */
	public @NonNull ButtonState getButtonState() {
		return buttonVisible ? tableValues != null ? ButtonState.ACTIVE : ButtonState.INACTIVE
				: ButtonState.HIDDEN;
	}

	/** Handles tapping on the table values button. */
	public void onButtonTapped() {
		tableValues = tableValues == null
				? ProbabilityCalculatorTableValues.from(probabilityCalculatorView) : null;
		if (delegate != null) {
			delegate.update();
		}
	}

	/** Handles closing the view, called when the view is collapsed or closed via the x button. */
	public void onClosed() {
		tableValues = null;
		if (delegate != null) {
			delegate.update();
		}
	}

	/** Detaches the view from listening to probability view changes and sending notifications. */
	public void detach() {
		delegate = null;
		probabilityCalculatorView.removeListener(this);
	}

	@Override
	public void probabilityCalculatorViewChanged() {
		if (tableValues != null) {
			tableValues = ProbabilityCalculatorTableValues.from(probabilityCalculatorView);
		}
		buttonVisible = probabilityCalculatorView.isDiscreteProbability();
		if (delegate != null) {
			delegate.update();
		}
	}
}
