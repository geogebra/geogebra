package org.geogebra.common.gui.view.spreadsheet;

import org.geogebra.common.main.App;
import org.geogebra.common.spreadsheet.style.CellFormatInterface;

public interface MyTableInterface extends HasTableSelection {

	// Selection type
	public static final int CELL_SELECT = 0;
	public static final int ROW_SELECT = 1;
	public static final int COLUMN_SELECT = 2;

	public App getApplication();

	public boolean editCellAt(int selectedRow, int selectedColumn);

	public CellFormatInterface getCellFormatHandler();

	public int getSelectionType();

	public void selectionChanged();

	public boolean setSelection(int i, int j);

	public int getColumnCount();

	public int getRowCount();

	public boolean allowSpecialEditor();

	public CellRangeProcessor getCellRangeProcessor();

	public void updateTableCellValue(Object value, int row, int column);

	public void repaintAll();

}
