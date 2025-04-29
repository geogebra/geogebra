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

	int getRows();

	int getColumns();

	void setCustomRowAndColumnSizeProvider(@CheckForNull SpreadsheetCustomRowAndColumnSizeProvider
			customRowAndColumnSizeProvider);
}
