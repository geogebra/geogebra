package geogebra.web.gui.view.data;

import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.html5.euclidian.EuclidianControllerW;

import java.util.ArrayList;

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
	
	@Override
	public void wrapMouseWheelMoved(int x, int y, double delta,
	        boolean shiftOrMeta, boolean alt) {
		//scolling disabled
		return;
	}

	@Override
	protected void showPopupMenuChooseGeo(ArrayList<GeoElement> selectedGeos1, Hits hits){
		
	}
		
}
