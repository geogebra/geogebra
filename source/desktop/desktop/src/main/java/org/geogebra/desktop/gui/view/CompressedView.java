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

package org.geogebra.desktop.gui.view;

import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * * This class will collect update events in a time slice and bundles them in a
 * Set and after the time slice it will handle them to its attached view.
 * (Multiple updates of the same GeoElement in a time slice are only handled
 * down to the extends AlgebraView once at the end of the time slice.)
 * 
 * @author Lucas Binter
 */
public interface CompressedView extends View {
	/**
	 * This function should invoke view.update(geo) directly
	 * 
	 * @param geo
	 *            the GeoElement which has changed
	 */
	public void updateNow(GeoElement geo);

	/**
	 * Calls the original repaint() function You need to overwrite the repaint
	 * function with an empty one to get the desired effect
	 * 
	 */
	public void repaintNow();

	/**
	 * e.g.:
	 * 
	 * &#x40;Override final public void repaint() { if (!repaintTimer.isRunning()) {
	 *           repaintNow(); }else{ repaintTimer.start(); } }
	 */
	public void repaint();
}