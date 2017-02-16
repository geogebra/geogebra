/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Slider item for Algebra View.
 * 
 * @author laszlo
 *
 */
public interface SliderTreeItemInterface {
	/**
	 * Cancel event listener
	 *
	 */
	interface CancelListener {
		/**
		 * Callback for ESC key
		 */
		void cancel();
	}

	/**
	 * @return slider geo
	 */
	GeoElement getGeo();

	/**
	 * @param width
	 *            new width
	 */
	void expandSize(int width);

	/**
	 * @param visible
	 *            whether to show the slider
	 */
	void setSliderVisible(boolean visible);


	/**
	 * @param visible
	 *            whether to show animation panel
	 */
	void setAnimPanelVisible(boolean visible);

	/**
	 * Restore panel size
	 */
	void restoreSize();

	/**
	 * Resize after delay
	 */
	void deferredResize();
}