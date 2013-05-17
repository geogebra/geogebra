package geogebra.touch;

import geogebra.html5.main.ViewManager;
import geogebra.web.gui.view.spreadsheet.SpreadsheetViewW;

public class ViewManagerM implements ViewManager {

	@Override
	public SpreadsheetViewW getSpreadsheetView() {
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
