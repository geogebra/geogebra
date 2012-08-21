package geogebra.common.gui.view.spreadsheet;

import geogebra.common.kernel.Kernel;

public interface MyTable extends MyTableInterface {

	// Selection type
	public static final int CELL_SELECT = 0;
	public static final int ROW_SELECT = 1;
	public static final int COLUMN_SELECT = 2;
	
	public static final int TABLE_MODE_STANDARD = 0;
	public static final int TABLE_MODE_AUTOFUNCTION = 1;
	public static final int TABLE_MODE_DROP = 2;

	// e.g. for CellRangeProcessor
	public abstract Kernel getKernel();
	public abstract CopyPasteCut getCopyPasteCut();
	public abstract int getColumnCount();
}
