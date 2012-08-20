/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoElement;

/**
 * Interface for views. A view registered to the kernel gets informed if elements were added, removed or changed.
 */
public interface View {
	/**
	 * Notify this view about newly added geo
	 * @param geo new geo
	 */
	public void add(GeoElement geo);
	/**
	 * Notify this view about removed geo
	 * @param geo removed geo
	 */
	public void remove(GeoElement geo);
	/**
	 * Notify this view about renamed geo
	 * @param geo renamed geo
	 */
	public void rename(GeoElement geo);
	/**
	 * Notify this view about updated geo
	 * @param geo updated geo
	 */
	public void update(GeoElement geo);
	/**
	 * Notify this view about geo with updated visual style
	 * @param geo updated geo
	 */
	public void updateVisualStyle(GeoElement geo);
	/**
	 * Notify this view about updated auxiliary geo
	 * @param geo updated auxiliary geo
	 */
	public void updateAuxiliaryObject(GeoElement geo);
	/**
	 * Repaints all objects
	 */
	public void repaintView();
	/**
	 * Resets the view
	 */
	public void reset(); 
	/**
	 * Remove all geos from this view
	 */
	public void clearView();
	/**
	 * Notify this view about changed mode
	 * @param mode Euclidian view mode
	 */
	public void setMode(int mode);
	/**
	 * @return unique ID of this view
	 */
	public int getViewID();
	
	public boolean hasFocus();
	public void repaint();
	public boolean isShowing();
}
