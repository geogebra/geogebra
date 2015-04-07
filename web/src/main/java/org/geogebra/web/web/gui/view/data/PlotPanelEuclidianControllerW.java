package org.geogebra.web.web.gui.view.data;

import java.util.ArrayList;

import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.euclidian.EuclidianControllerW;

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
	public void showDrawingPadPopup(org.geogebra.common.awt.GPoint mouseLoc){
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
