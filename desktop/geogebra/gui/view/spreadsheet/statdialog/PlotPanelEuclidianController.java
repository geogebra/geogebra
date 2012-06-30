package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.kernel.Kernel;
import geogebra.euclidian.EuclidianControllerD;


public class PlotPanelEuclidianController extends EuclidianControllerD{

	public PlotPanelEuclidianController(Kernel kernel) {
		super(kernel);
	}
	
	@Override
	public void showDrawingPadPopup(geogebra.common.awt.GPoint mouseLoc){
		// do nothing		
	}
}