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

package org.geogebra.common.spreadsheet.core;

import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.gui.view.table.regression.RegressionSpecification;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * An auto-updating view of statistics calculations on a spreadsheet range.
 * <p>
 * A client receiving an instance of this type should set its listeners straight away to make sure
 * that no input or result updates are missed.
 * </p>
 * <p>
 * When the view is no longer needed (because the UI gets closed), clients are expected to
 * tear down the view by calling {@link #tearDown()}.
 * </p>
 * <p>
 * <em>Design Notes</em>
 * <p>
 * This type is called {@code View} although it is not technically a {@code kernel.View}, to convey
 * the fact that it can update / emit new results when elements in the input range change.
 *
 * @param <I> Input type
 */
public interface SpreadsheetStatisticsView<I extends SpreadsheetStatistics.Input> {

	/** Delegate facing the UI layer, notifying when the spreadsheet statistics view changes. */
	interface Delegate {
		/** Called when the current statistics view changes. */
		void statisticsViewChanged();
	}

	/**
	 * One variable statistics view.
	 */
	interface OneVar extends SpreadsheetStatisticsView<SpreadsheetStatistics.Input.OneVarInput> {
	}

	/**
	 * Two variable statistics view.
	 */
	interface TwoVar extends SpreadsheetStatisticsView<SpreadsheetStatistics.Input.TwoVarInput> {
	}

	/**
	 * Regression statistics view.
	 */
	interface Regression
			extends SpreadsheetStatisticsView<SpreadsheetStatistics.Input.RegressionInput> {

		/**
		 * @return The list of available regression specifications.
		 */
		List<RegressionSpecification> getRegressionSpecifications();

		/**
		 * Add regression graph to construction to plot it in graphics.
		 */
		void plotResult();
	}

	/**
	 * @return The localization key of this view's title.
	 */
	@NonNull String getTitleLocalizationKey();

	/**
	 * @return The current input.
	 */
	@NonNull I getInput();

	/**
	 * Updates the input during an ongoing user edit.
	 * @param input the current input
	 */
	void setInput(@NonNull I input);

	/** Commits the input when the user has finished editing. */
	void commitInput();

	/**
	 * Sets the currently focused data range (input field), communicating focus changes from the UI.
	 * @param focusedDataRange the focused data range, or {@code null} if none is focused
	 */
	void setFocusedDataRange(SpreadsheetStatistics.@Nullable DataRange focusedDataRange);

	/**
	 * @return the currently focused data range, or {@code null} if none is focused
	 */
	SpreadsheetStatistics.@Nullable DataRange getFocusedDataRange();

	/**
	 * @return The current result of the statistics calculation.
	 */
	SpreadsheetStatistics.@NonNull Result getResult();

	/**
	 * Attach an input change listener to the view.
	 * @param listener notified when the current input changes
	 * @apiNote UI integrations should only update the input fields if the received cell range is
	 * not {@code null}.
	 */
	void setInputChangeListener(@Nullable Consumer<@NonNull I> listener);

	/**
	 * Attach a result change listener to the view.
	 * @param listener notified when the calculated result changes
	 */
	void setResultChangeListener(
			@Nullable Consumer<SpreadsheetStatistics.@NonNull Result> listener);

	/** Tear down the view when it is no longer used (e.g. view is replaced or the UI closes). */
	void tearDown();
}
