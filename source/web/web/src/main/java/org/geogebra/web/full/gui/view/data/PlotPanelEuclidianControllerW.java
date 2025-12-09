/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
