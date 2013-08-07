package geogebra.touch;

import geogebra.html5.gui.view.spreadsheet.SpreadsheetViewWeb;
import geogebra.html5.main.ViewManager;

public class ViewManagerM implements ViewManager {

	@Override
	public SpreadsheetViewWeb getSpreadsheetView() {
		return null;
	}

	@Override
	public boolean hasAlgebraView() {
		return true;
	}

	@Override
	public boolean hasSpreadsheetView() {
		return false;
	}

}
