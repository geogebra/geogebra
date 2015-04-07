package org.geogebra.common.gui.view.spreadsheet;

import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

public interface SpreadsheetViewInterface extends View {

	public MyTableInterface getSpreadsheetTable();


	public void rowHeaderRevalidate();
	public void columnHeaderRevalidate();
	public void updateCellFormat(String s);
	public App getApplication();
	public int getMode();


	public void showTraceDialog(GeoElement geo, CellRange traceCell);
}
