/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Interface for views. A view registered to the kernel gets informed if
 * elements were added, removed or changed.
 */
public interface View {
	/**
	 * Notify this view about newly added geo
	 * 
	 * @param geo
	 *            new geo
	 */
	public void add(GeoElement geo);

	/**
	 * Notify this view about removed geo
	 * 
	 * @param geo
	 *            removed geo
	 */
	public void remove(GeoElement geo);

	/**
	 * Notify this view about renamed geo
	 * 
	 * @param geo
	 *            renamed geo
	 */
	public void rename(GeoElement geo);

	/**
	 * Notify this view about updated geo
	 * 
	 * @param geo
	 *            updated geo
	 */
	public void update(GeoElement geo);

	/**
	 * Notify this view about geo with updated visual style
	 * 
	 * @param geo
	 *            updated geo
	 * @param prop
	 *            property updated
	 */
	public void updateVisualStyle(GeoElement geo, GProperty prop);

	/**
	 * Notify this view about geo with updated highlighting
	 * 
	 * @param geo
	 *            updated geo
	 */
	public void updateHighlight(GeoElementND geo);

	/**
	 * Notify this view about updated auxiliary geo
	 * 
	 * @param geo
	 *            updated auxiliary geo
	 */
	public void updateAuxiliaryObject(GeoElement geo);

	/**
	 * Repaints all objects
	 */
	public void repaintView();

	/**
	 * Suggest repaint now
	 * 
	 * @return true when repaint happened or is planned, false when sleeping
	 */
	public boolean suggestRepaint();

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
	 * 
	 * @param mode
	 *            Euclidian view mode
	 * @param m
	 *            how did the mode change happen
	 */
	public void setMode(int mode, ModeSetter m);

	/**
	 * @return unique ID of this view
	 */
	public int getViewID();

	/**
	 * @return whether this view is focused
	 */
	public boolean hasFocus();

	/**
	 * Notification for update batch
	 */
	public void startBatchUpdate();

	/**
	 * Notification for end of update batch
	 */
	public void endBatchUpdate();

	/**
	 * @param geos
	 *            input bar elements
	 */
	public void updatePreviewFromInputBar(GeoElement[] geos);
}
