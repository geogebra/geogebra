package org.geogebra.common.gui.view.spreadsheet;

import org.geogebra.common.main.App;
import org.geogebra.common.spreadsheet.core.SelectionType;
import org.geogebra.common.spreadsheet.style.CellFormatInterface;

public interface MyTableInterface extends HasTableSelection {

	public App getApplication();

	public boolean editCellAt(int selectedRow, int selectedColumn);

	public CellFormatInterface getCellFormatHandler();

	public SelectionType getSelectionType();

	public void selectionChanged();

	public boolean setSelection(int i, int j);

	public int getColumnCount();

	public int getRowCount();

	public boolean allowSpecialEditor();

	public CellRangeProcessor getCellRangeProcessor();

	public void updateTableCellValue(Object value, int row, int column);

	public void repaintAll();

}
