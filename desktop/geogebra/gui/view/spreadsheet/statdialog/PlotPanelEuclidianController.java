package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.kernel.AbstractKernel;
import geogebra.euclidian.EuclidianController;

import java.awt.Point;


public class PlotPanelEuclidianController extends EuclidianController{

	public PlotPanelEuclidianController(AbstractKernel kernel) {
		super(kernel);
	}
	
	@Override
	public void showDrawingPadPopup(geogebra.common.awt.Point mouseLoc){
		// do nothing		
	}
}