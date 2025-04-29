package org.geogebra.common.spreadsheet.core;

import java.util.Map;

import javax.annotation.Nonnull;

import org.geogebra.common.main.settings.SpreadsheetSettings;

/**
 * Provides row heights and column widths that have been changed by the user.
 *
 * @apiNote This interface only exists to abstract out the {@link SpreadsheetSettings} from the
 * {@code spreadsheet.core} package. It serves as a "back reference" for injecting the TableLayout
 * into the SpreadsheetSettings (which needs this information when serializing the spreadsheet to
 * XML).
 */
public interface SpreadsheetCustomRowAndColumnSizeProvider {

	/**
	 * @return map column=&gt;width containing all widths that are not the default
	 */
	@Nonnull Map<Integer, Double> getCustomColumnWidths();

	/**
	 * @return map row=&gt;height containing all heights that are not the default
	 */
	@Nonnull Map<Integer, Double> getCustomRowHeights();
}
