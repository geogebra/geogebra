package geogebra.html5.main;

import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.gui.view.spreadsheet.SpreadsheetViewWeb;

public interface ViewManager {

	public SpreadsheetViewWeb getSpreadsheetView();

	public boolean hasAlgebraView();
	
	public boolean hasSpreadsheetView();

	public void clearAbsolutePanels();

	public boolean hasPlotPanelEuclidianView();

	public EuclidianViewWeb getPlotPanelEuclidanView();

}
