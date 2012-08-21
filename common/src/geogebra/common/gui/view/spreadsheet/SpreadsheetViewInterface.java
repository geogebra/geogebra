package geogebra.common.gui.view.spreadsheet;

import geogebra.common.kernel.View;

public interface SpreadsheetViewInterface extends View {

	public MyTableInterface getTable();


	public void rowHeaderRevalidate();
	public void columnHeaderRevalidate();
}
