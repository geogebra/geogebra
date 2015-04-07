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
	 * @Override final public void repaint() { if (!repaintTimer.isRunning()) {
	 *           repaintNow(); }else{ repaintTimer.start(); } }
	 */
	public void repaint();
}