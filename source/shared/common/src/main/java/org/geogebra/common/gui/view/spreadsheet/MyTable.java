package org.geogebra.common.gui.view.spreadsheet;

import java.util.ArrayList;

import javax.annotation.CheckForNull;

import org.geogebra.common.spreadsheet.core.TabularRange;

/**
 * Spreadsheet table component (Classic).
 */
public interface MyTable extends MyTableInterface {

	public static final int TABLE_MODE_STANDARD = 0;
	public static final int TABLE_MODE_AUTOFUNCTION = 1;
	public static final int TABLE_MODE_DROP = 2;

	/**
	 * Set table mode
	 * @param mode one of TABLE_MODE_* constants
	 */
	public void setTableMode(int mode);

	/**
	 * @return the spreadsheet view
	 */
	public SpreadsheetViewInterface getView();

	/**
	 * @return copy, paste and cut provider
	 */
	public CopyPasteCut getCopyPasteCut();

	/**
	 * @return selected ranges
	 */
	public ArrayList<TabularRange> getSelectedRanges();

	/**
	 * Set cell selection.
	 * @param targetRange new selection
	 * @return if selection is valid
	 */
	public boolean setSelection(TabularRange targetRange);

	/**
	 * Select given cell.
	 * @param y row
	 * @param x column
	 * @param extend whether to extend current selection
	 */
	public void changeSelection(int y, int x, boolean extend);

	/**
	 * @return table mode (one of TABLE_MODE_* constants)
	 */
	public int getTableMode();

	default @CheckForNull TabularRange getFirstSelection() {
		return getSelectedRanges().isEmpty() ? null : getSelectedRanges().get(0);
	}
}
