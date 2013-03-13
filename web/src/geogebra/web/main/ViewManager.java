package geogebra.web.main;

import geogebra.web.gui.view.spreadsheet.SpreadsheetViewW;

public interface ViewManager {

	public SpreadsheetViewW getSpreadsheetView();

	public boolean hasAlgebraView();
	
	public boolean hasSpreadsheetView();

}
