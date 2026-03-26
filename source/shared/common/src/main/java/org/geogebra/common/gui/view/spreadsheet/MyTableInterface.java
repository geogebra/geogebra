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

import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.spreadsheet.core.SelectionType;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.spreadsheet.style.CellFormatInterface;

/**
 * Spreadsheet table (classic).
 */
public interface MyTableInterface extends HasTableSelection {

	/**
	 * @return parent application
	 */
	App getApplication();

	/**
	 * Open editor for cell at given coordinates.
	 * @param selectedRow row
	 * @param selectedColumn column
	 * @return success
	 */
	boolean editCellAt(int selectedRow, int selectedColumn);

	/**
	 * @return cell format handler
	 */
	CellFormatInterface getCellFormatHandler();

	/**
	 * @return selection type
	 */
	SelectionType getSelectionType();

	/**
	 * Called when selection changed.
	 */
	void selectionChanged();

	/**
	 * Select cell at given coordinates.
	 * @param i row
	 * @param j column
	 * @return success
	 */
	boolean setSelection(int i, int j);

	/**
	 * @return number of columns
	 */
	int getColumnCount();

	/**
	 * @return number of rows
	 */
	int getRowCount();

	/**
	 * @return whether special editors (dropdowns, checkboxes) are allowed
	 */
	boolean allowSpecialEditor();

	/**
	 * @return cell range processor
	 */
	CellRangeProcessor getCellRangeProcessor();

	/**
	 * Update cell value
	 * @param value new cell value
	 * @param row row
	 * @param column column
	 */
	void updateTableCellValue(Object value, int row, int column);

	/**
	 * @return selected ranges
	 */
	List<TabularRange> getSelectedRanges();

}
