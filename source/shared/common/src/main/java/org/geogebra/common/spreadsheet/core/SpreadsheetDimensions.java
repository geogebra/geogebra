package org.geogebra.common.spreadsheet.core;

import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.main.settings.SpreadsheetSettings;

/**
 * @apiNote This interface only exists to abstract out the {@link SpreadsheetSettings} from the
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

	/**
	 * Set provider for row and column size overrides.
	 * @param customRowAndColumnSizeProvider size override provider
	 */
	void setCustomRowAndColumnSizeProvider(@CheckForNull SpreadsheetCustomRowAndColumnSizeProvider
			customRowAndColumnSizeProvider);
}
