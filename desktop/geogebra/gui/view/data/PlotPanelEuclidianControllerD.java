package geogebra.gui.view.data;

import geogebra.common.kernel.Kernel;
import geogebra.euclidian.EuclidianControllerD;


public class PlotPanelEuclidianControllerD extends EuclidianControllerD{

	public PlotPanelEuclidianControllerD(Kernel kernel) {
		super(kernel);
	}
	
	@Override
	public void showDrawingPadPopup(geogebra.common.awt.GPoint mouseLoc){
		// do nothing		
	}
}