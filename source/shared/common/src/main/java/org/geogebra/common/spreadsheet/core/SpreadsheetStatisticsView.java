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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.table.regression.RegressionSpecification;

/**
 * An auto-updating view of statistics calculations on a spreadsheet range.
 * <p>
 * A client receiving an instance of this type should set itself as the (single) listener straight
 * away to make sure that no new results are missed (the result can change any time).
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
	}

	/**
	 * @return The localization key of this view's title.
	 */
	@Nonnull String getTitleLocalizationKey();

	/**
	 * @return The current input.
	 */
	@Nonnull I getInput();

	/**
	 * Set new input values. This will immediately cause recalculation of the result and trigger
	 * the change listener.
	 * @param input The input (cell range(s), regression model) for the statistics calculation.
	 */
	void setInput(@Nonnull I input);

	/**
	 * @return The current result of the statistics calculation.
	 */
	@Nonnull SpreadsheetStatistics.Result getResult();

	/**
	 * Attach a change listener to the view.
	 * @param listener The listener. Will be notified when the statistics result changed due to
	 * changes in the spreadsheet data.
	 */
	void setChangeListener(@CheckForNull Consumer<SpreadsheetStatistics.Result> listener);

	/**
	 * Tear down the view when it is no longer used (e.g., the UI closes).
	 * @apiNote It is safe to call this method multiple times.
	 */
	void tearDown();
}
