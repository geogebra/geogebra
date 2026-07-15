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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.gui.view.table.regression.RegressionSpecification;

/**
 * Calculate statistics from spreadsheet contents.
 * <p>
 * <em>Design Notes</em>
 * <p>
 * This interface exists mostly to decouple the {@code spreadsheet.core} package
 * from the {@code kernel} package.
 * <p>
 * {@link SpreadsheetReference} is used as the data type for cell ranges because
 * both the data type itself as well as corresponding parsing functions (see
 * {@link SpreadsheetReferenceParsing}) are contained within {@code spreadsheet.core},
 * avoiding dependencies on external packages.
 * <p>
 * Introducing the three input types and making the use cases generic over the
 * input type gives us strong typing in the API, and it should (hopefully) impossible to inject
 * values of the wrong type anywhere.
 */
public interface SpreadsheetStatistics {

	/**
	 * Errors for spreadsheet statistics.
	 */
	enum Error {
		NUMERIC_DATA_RANGE_REQUIRED("Statistics.Error.NumericDataRangeRequired"),
		TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED(
				"Statistics.Error.TwoNumericDataRangesRequired");

		/** The translation key for this error. */
		public final @Nonnull String localizationKey;

		Error(@Nonnull String localizationKey) {
			this.localizationKey = localizationKey;
		}
	}

	/**
	 * Identifies one of an {@link Input}'s data ranges.
	 * For {@link Input.OneVarInput}, the sole {@code cellRange} maps to {@link #X}.
	 */
	enum DataRange {
		X,
		Y
	}

	/**
	 * The input type for statistics calculations.
	 */
	sealed interface Input {
		/**
		 * One-variable statistics input.
		 * @param cellRange A spreadsheet cell range.
		 */
		record OneVarInput(@CheckForNull SpreadsheetReference cellRange) implements Input {
			/**
			 * Converting constructor, accepting a {@link TabularRange}.
			 * @param range If finite (bounded in both directions), the result will be a
			 * {@link SpreadsheetReference} truncated to {@code range}'s first column. An unbounded
			 * {@code range} will give {@code null}.
			 */
			public OneVarInput(@Nonnull TabularRange range) {
				this(SpreadsheetReference.fromRange(range.firstColumn()));
			}
		}

		/**
		 * Two-variable statistics input.
		 * @param cellRangeX A spreadsheet cell range for variable X.
		 * @param cellRangeY A spreadsheet cell range for variable Y.
		 */
		record TwoVarInput(@CheckForNull SpreadsheetReference cellRangeX,
						   @CheckForNull SpreadsheetReference cellRangeY) implements Input {
			/**
			 * Converting constructor, accepting a {@link TabularRange}.
			 * @param range If finite (bounded in both directions), the result will be two
			 * {@link SpreadsheetReference}s truncated to {@code range}'s first (X) and second (Y)
			 * column, respectively.
			 */
			public TwoVarInput(@Nonnull TabularRange range) {
				this(SpreadsheetReference.fromRange(range.firstColumn()),
						SpreadsheetReference.fromRange(range.secondColumn()));
			}
		}

		/**
		 * Regression input.
		 * @param cellRangeX A spreadsheet cell range for variable X.
		 * @param cellRangeY A spreadsheet cell range for variable Y.
		 * @param regression The regression model. If {@code null}, the default (first) regression
		 * model will be used for calculation.
		 */
		record RegressionInput(@CheckForNull SpreadsheetReference cellRangeX,
							   @CheckForNull SpreadsheetReference cellRangeY,
							   @CheckForNull RegressionSpecification regression) implements Input {
			/**
			 * Converting constructor, accepting a {@link TabularRange}.
			 * @param range If finite (bounded in both directions), the result will be two
			 * {@link SpreadsheetReference}s truncated to {@code range}'s first (X) and second (Y)
			 * column, respectively.
			 */
			public RegressionInput(@Nonnull TabularRange range) {
				this(SpreadsheetReference.fromRange(range.firstColumn()),
						SpreadsheetReference.fromRange(range.secondColumn()),
						null);
			}
		}
	}

	/**
	 * The result type for statistics calculations.
	 */
	sealed interface Result {
		/**
		 * Valid input, calculation successful.
		 * @param statisticGroups calculation result
		 */
		record Valid(@Nonnull List<StatisticGroup> statisticGroups) implements Result { }

		/**
		 * Invalid input, error.
		 * @param error what went wrong
		 * @param dataRange the range that requires user attention
		 */
		record Invalid(@Nonnull Error error,
					   @CheckForNull DataRange dataRange) implements Result { }
	}

	/**
	 * Create an auto-updating view for one-variable statistics.
	 * @param range The spreadsheet range used for the statistics calculation (will be validated).
	 * @return a view providing auto-updating one-variable statistics
	 */
	@Nonnull SpreadsheetStatisticsView.OneVar getOneVarStatistics(
			@Nonnull TabularRange range);

	/**
	 * Create an auto-updating view for two-variable statistics.
	 * @param range The spreadsheet range used for the statistics calculation (will be validated).
	 * @return a view providing auto-updating two-variable statistics
	 */
	@Nonnull SpreadsheetStatisticsView.TwoVar getTwoVarStatistics(
			@Nonnull TabularRange range);

	/**
	 * Create an auto-updating view for regression metrics.
	 * @param range The spreadsheet range used for the statistics calculation (will be validated).
	 * @return a view providing auto-updating regression metrics
	 */
	@Nonnull SpreadsheetStatisticsView.Regression getRegression(
			@Nonnull TabularRange range);
}
