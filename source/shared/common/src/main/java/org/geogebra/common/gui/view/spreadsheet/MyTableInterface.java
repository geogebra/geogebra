package org.geogebra.common.gui.view.spreadsheet;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.main.App;
import org.geogebra.common.spreadsheet.core.SelectionType;
import org.geogebra.common.spreadsheet.style.CellFormatInterface;

/**
 * Spreadsheet table (classic).
 */
public interface MyTableInterface extends HasTableSelection {

	/**
	 * @return parent application
	 */
	public App getApplication();

	/**
	 * Open editor for cell at given coordinates.
	 * @param selectedRow row
	 * @param selectedColumn column
	 * @return success
	 */
	public boolean editCellAt(int selectedRow, int selectedColumn);

	/**
	 * @return cell format handler
	 */
	public CellFormatInterface getCellFormatHandler();

	/**
	 * @return selection type
	 */
	public SelectionType getSelectionType();

	@MissingDoc
	public void selectionChanged();

	/**
	 * Select cell at given coordinates.
	 * @param i row
	 * @param j column
	 * @return success
	 */
	public boolean setSelection(int i, int j);

	/**
	 * @return number of columns
	 */
	public int getColumnCount();

	/**
	 * @return number of rows
	 */
	public int getRowCount();

	/**
	 * @return whether special editors (dropdowns, checkboxes) are allowed
	 */
	public boolean allowSpecialEditor();

	/**
	 * @return cell range processor
	 */
	public CellRangeProcessor getCellRangeProcessor();

	/**
	 * Update cell value
	 * @param value new cell value
	 * @param row row
	 * @param column column
	 */
	public void updateTableCellValue(Object value, int row, int column);

	/**
	 * Repaint the table.
	 */
	public void repaintAll();

}
