package org.geogebra.common.spreadsheet.core;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Enumeration representing possible validation errors when generating charts
 * from spreadsheet data.
 *
 * <p>Each constant corresponds to a specific validation state used when
 * checking whether the selected tabular data can be used to create a chart.
 * The {@code errorKey} is used for localization (e.g., to fetch a translated
 * message string from a resource bundle).</p>
 */
public enum ChartError {
	NONE(""),
	NoData("StatsDialog.NoData"),
	TwoColumnsNeeded("ChartError.TwoColumns"),
	InvalidData("InvalidInput");

	private final String errorKey;

	/**
	 * Constructs a {@code ChartError} with the given localization key.
	 *
	 * @param errorKey localization key for this error
	 */
	ChartError(String errorKey) {
		this.errorKey = errorKey;
	}

	/**
	 * Returns the localization key associated with this error.
	 *
	 * @return the error key string (may be empty)
	 */
	public String getErrorKey() {
		return errorKey;
	}

	/**
	 * Validates a list of {@link TabularRange} objects to ensure that they
	 * represent data suitable for a chart requiring exactly two columns.
	 *
	 * @param ranges list of tabular ranges to validate
	 * @return {@link #NONE} if the selection is valid;
	 *         {@link #NoData} or {@link #TwoColumnsNeeded} otherwise
	 */
	public static ChartError validateForTwoColumns(List<TabularRange> ranges) {
		if (ranges.size() == 1) {
			return validateSingleRange(ranges.get(0));
		}
		return validateMultiRange(ranges);
	}

	private static ChartError validateMultiRange(List<TabularRange> ranges) {
		if (ranges.size() == 2) {
			TabularRange r1 = ranges.get(0);
			TabularRange r2 = ranges.get(1);
			if (r1.getWidth() == 1 && r2.getWidth() == 1
					&& r1.getMinRow() == r2.getMinRow() && r1.getMaxRow() == r2.getMaxRow()) {
				return NONE;
			}
		}
		return TwoColumnsNeeded;
	}

	private static ChartError validateSingleRange(TabularRange range) {
		if (range.getWidth() == 2) {
			return ChartError.NONE;
		}
		return noDataOrTwoColumnNeeded(range);
	}

	private static ChartError noDataOrTwoColumnNeeded(TabularRange range) {
		return range.isSingleCell() ? NoData : TwoColumnsNeeded;
	}

	/**
	 * Validates whether the provided ranges are suitable for a chart that
	 * requires two or more columns of data.
	 *
	 * @param ranges list of ranges to validate
	 * @return {@link #NONE} if the selection is valid;
	 *         {@link #NoData} or {@link #TwoColumnsNeeded} otherwise
	 */
	public static ChartError validateForMoreColumns(List<TabularRange> ranges) {
		if (ranges.size() == 1) {
			TabularRange range = ranges.get(0);
			return range.getWidth() >= 2 ? NONE : noDataOrTwoColumnNeeded(range);
		}
		return validateMultiRange(ranges);
	}

	/**
	 * Checks if selected ranges are valid for BoxPlot.
	 * @param ranges One or two ranges. The first range may be a 2xN or Nx2 rectangular range,
	 * in which case there must only be one range.
	 * @return {@link #NONE} if the ranges are valid, or a validation error otherwise.
	 */
	public static ChartError validateRangesForBoxPlot(@Nonnull List<TabularRange> ranges) {
		if (ranges.isEmpty()) {
			return ChartError.NoData; // not enough ranges
		}
		if (ranges.size() > 2) {
			return ChartError.InvalidData; // too many ranges
		}
		TabularRange range1 = ranges.get(0);
		if (range1.isSingleCell()) {
			return ChartError.NoData; // not enough cells
		}
		if (range1.isEntireColumn() || range1.isEntireRow()) {
			return ChartError.InvalidData; // too many cells
		}
		if (ranges.size() == 2) {
			TabularRange range2 = ranges.get(1);
			if (range2.isEntireColumn() || range2.isEntireRow()
					// if we have 2 ranges, they must not be wider than 1 row or column
					|| !range2.is1D() || !range1.is1D()) {
				return ChartError.InvalidData; // too many cells
			}
			int size1 = range1.isPartialColumn() ? range1.getHeight() : range1.getWidth();
			int size2 = range2.isPartialColumn() ? range2.getHeight() : range2.getWidth();
			if (size1 != size2) {
				return ChartError.InvalidData;
			}
		} else {
			// if we have just one range which is rectangular (e.g., 3x2), the smaller dimension
			// must not exceed 2 (i.e., 4x3 is not allowed)
			if (Math.min(range1.getWidth(), range1.getHeight()) > 2) {
				return ChartError.InvalidData;
			}
		}
		return ChartError.NONE;
	}
}
