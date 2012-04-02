package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.kernel.Kernel;
import geogebra.euclidian.EuclidianController;


public class PlotPanelEuclidianController extends EuclidianController{

	public PlotPanelEuclidianController(Kernel kernel) {
		super(kernel);
	}
	
	@Override
	public void showDrawingPadPopup(geogebra.common.awt.Point mouseLoc){
		// do nothing		
	}
}