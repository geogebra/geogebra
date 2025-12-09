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

package org.geogebra.common.cas.view;

import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.App;

/**
 * CAS table
 */
public interface CASTable {

	/**
	 * @return number of rows
	 */
	int getRowCount();

	/**
	 * Update localized labels
	 */
	void setLabels();

	/**
	 * @param n
	 *            row index
	 * @return row content
	 */
	GeoCasCell getGeoCasCell(int n);

	/**
	 * @return app
	 */
	App getApplication();

	/**
	 * delete all rows
	 */
	void deleteAllRows();

	/**
	 * @param rows
	 *            row index
	 * @param casCell
	 *            value
	 * @param startEditing
	 *            whether to start editing right away
	 */
	void insertRow(int rows, GeoCasCell casCell, boolean startEditing);

	/**
	 * @return selected row indices
	 */
	int[] getSelectedRows();

	/**
	 * @return first selected row
	 */
	int getSelectedRow();

	/**
	 * Stop editing
	 */
	void stopEditing();

	/**
	 * @param selectedRow
	 *            index of edited row
	 */
	void startEditingRow(int selectedRow);

	/**
	 * @return editor
	 */
	CASTableCellEditor getEditor();

	/**
	 * @param rowNumber
	 *            row index
	 */
	void deleteRow(int rowNumber);

	/**
	 * @param rowNumber
	 *            row index
	 * @param casCell
	 *            value
	 */
	void setRow(int rowNumber, GeoCasCell casCell);

	/**
	 * @return whether editor is active
	 */
	boolean isEditing();

	/**
	 * On web, if we insert one or more new row, or delete some, needed change
	 * the row numbers manually after the inserted or deleted rows.
	 * 
	 * @param from
	 *            the first row number to change
	 */
	void resetRowNumbers(int from);

	/**
	 * @return whether is not null
	 */
	boolean hasEditor();

	/**
	 * @param failure
	 *            whether current input fails to evaluate
	 * @param rowNum
	 *            row number
	 * @return whether to keep editing
	 */
	boolean keepEditing(boolean failure, int rowNum);
}
