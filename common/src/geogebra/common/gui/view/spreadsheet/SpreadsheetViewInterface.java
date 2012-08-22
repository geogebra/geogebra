package geogebra.common.gui.view.spreadsheet;

import geogebra.common.kernel.View;
import geogebra.common.main.App;

public interface SpreadsheetViewInterface extends View {

	public MyTableInterface getTable();


	public void rowHeaderRevalidate();
	public void columnHeaderRevalidate();
	public void updateCellFormat(String s);
	public App getApplication();
	public int getMode();
}
