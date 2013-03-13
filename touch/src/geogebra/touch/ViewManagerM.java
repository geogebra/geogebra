package geogebra.touch;

import geogebra.web.gui.view.spreadsheet.SpreadsheetViewW;
import geogebra.web.main.ViewManager;

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
