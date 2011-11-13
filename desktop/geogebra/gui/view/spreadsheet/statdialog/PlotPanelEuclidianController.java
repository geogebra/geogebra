package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.euclidian.EuclidianController;
import geogebra.kernel.Kernel;

import java.awt.Point;


public class PlotPanelEuclidianController extends EuclidianController{

	public PlotPanelEuclidianController(Kernel kernel) {
		super(kernel);
	}
	
	@Override
	public void showDrawingPadPopup(Point mouseLoc){
		// do nothing		
	}
}