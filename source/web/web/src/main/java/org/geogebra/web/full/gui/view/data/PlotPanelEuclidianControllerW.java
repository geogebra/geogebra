package org.geogebra.web.full.gui.view.data;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
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
	public void showDrawingPadPopup(GPoint mouse) {
		// do nothing
	}
	
	@Override
	public boolean wrapMouseWheelMoved(int x, int y, double delta,
			boolean shiftOrMeta, boolean alt) {
		return false; // scrolling disabled
	}

	@Override
	protected void showPopupMenuChooseGeo(ArrayList<GeoElement> selectedGeos1,
			Hits hits) {
		// kill menu in plot panel
	}

}
