package org.geogebra.common.gui.view.spreadsheet;

import java.util.ArrayList;

import org.geogebra.common.kernel.Kernel;

public interface MyTable extends MyTableInterface {

	public static final int TABLE_MODE_STANDARD = 0;
	public static final int TABLE_MODE_AUTOFUNCTION = 1;
	public static final int TABLE_MODE_DROP = 2;

	public void setTableMode(int mode);

	
	// e.g. for CellRangeProcessor
	public Kernel getKernel();
	
	public SpreadsheetViewInterface getView();
	public CopyPasteCut getCopyPasteCut();


	public ArrayList<CellRange> getSelectedCellRanges();


	public void updateTableCellValue(Object value, int i, int j);


		
	
	
	
}
