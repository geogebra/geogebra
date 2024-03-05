package org.geogebra.common.gui.view.spreadsheet;

import java.util.ArrayList;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.spreadsheet.core.TabularRange;

public interface MyTable extends MyTableInterface {

	public static final int TABLE_MODE_STANDARD = 0;
	public static final int TABLE_MODE_AUTOFUNCTION = 1;
	public static final int TABLE_MODE_DROP = 2;

	public void setTableMode(int mode);

	// e.g. for CellRangeProcessor
	public Kernel getKernel();

	public SpreadsheetViewInterface getView();

	public CopyPasteCut getCopyPasteCut();

	public ArrayList<TabularRange> getSelectedRanges();

	public boolean setSelection(TabularRange targetRange);

	public void changeSelection(int y, int x, boolean b);

	public int getTableMode();

	default @CheckForNull TabularRange getFirstSelection() {
		return getSelectedRanges().isEmpty() ? null : getSelectedRanges().get(0);
	}
}
