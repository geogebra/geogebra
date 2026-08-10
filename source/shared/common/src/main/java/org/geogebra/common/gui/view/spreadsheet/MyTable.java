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

package org.geogebra.common.gui.view.spreadsheet;

import org.geogebra.common.main.App;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.jspecify.annotations.Nullable;

/**
 * Spreadsheet table component (Classic).
 */
public interface MyTable extends MyTableInterface {

	int TABLE_MODE_STANDARD = 0;
	int TABLE_MODE_AUTOFUNCTION = 1;
	int TABLE_MODE_DROP = 2;

	/**
	 * Set table mode
	 * @param mode one of TABLE_MODE_* constants
	 */
	void setTableMode(int mode);

	/**
	 * @return the spreadsheet view
	 */
	SpreadsheetViewInterface getView();

	/**
	 * @return copy, paste and cut provider
	 */
	CopyPasteCut getCopyPasteCut();

	/**
	 * Set cell selection.
	 * @param targetRange new selection
	 * @return if selection is valid
	 */
	boolean setSelection(TabularRange targetRange);

	/**
	 * Select given cell.
	 * @param y row
	 * @param x column
	 * @param extend whether to extend current selection
	 */
	void changeSelection(int y, int x, boolean extend);

	/**
	 * @return table mode (one of TABLE_MODE_* constants)
	 */
	int getTableMode();

	default @Nullable TabularRange getFirstSelection() {
		return getSelectedRanges().isEmpty() ? null : getSelectedRanges().get(0);
	}

	/**
	 * @param app application
	 * @return processor for spreadsheet tools
	 */
	default SpreadsheetToolProcessor getToolProcessor(App app) {
		return new SpreadsheetToolProcessor(app,
				app.getSpreadsheetTableModel().getCellFormat(this));
	}
}
