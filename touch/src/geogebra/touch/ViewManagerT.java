package geogebra.touch;

import geogebra.html5.gui.view.spreadsheet.SpreadsheetViewWeb;
import geogebra.html5.main.ViewManager;

class ViewManagerT implements ViewManager {
	private final TouchApp app;
	
	public ViewManagerT(TouchApp app){
		this.app = app;
	}
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

	@Override
	public void clearAbsolutePanels() {
		app.getTouchGui().getEuclidianViewPanel().removeGBoxes();
		
	}

}
