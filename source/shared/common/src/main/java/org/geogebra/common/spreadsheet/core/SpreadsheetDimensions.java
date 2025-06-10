package org.geogebra.common.spreadsheet.core;

import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Provides the number of rows and columns in the spreadsheet, and the column/row sizes.
 *
 * @apiNote This interface only exists to abstract out the {@code SpreadsheetSettings} from the
 * {@code spreadsheet.core} package (only SpreadsheetSettings implements this interface).
 */
public interface SpreadsheetDimensions {
	/**
	 * @return column widths
	 */
	@Nonnull Map<Integer, Double> getColumnWidths();

	/**
	 * @return row heights
	 */
	@Nonnull Map<Integer, Double> getRowHeights();

	/**
	 * @return number of rows
	 */
	int getRows();

	/**
	 * @return number of columns
	 */
	int getColumns();
}
