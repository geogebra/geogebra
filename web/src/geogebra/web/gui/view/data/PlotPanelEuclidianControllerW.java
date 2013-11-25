package geogebra.web.gui.view.data;

import geogebra.common.kernel.Kernel;
import geogebra.web.euclidian.EuclidianControllerW;

/**
 * @author gabor
 * 
 * EuclidianController for plot panel in web
 *
 */
public class PlotPanelEuclidianControllerW extends EuclidianControllerW {

	/**
	 * @param kernel Kernel
	 */
	public PlotPanelEuclidianControllerW(Kernel kernel) {
	    super(kernel);
    }
	
	@Override
	public void showDrawingPadPopup(geogebra.common.awt.GPoint mouseLoc){
		// do nothing		
	}

}
