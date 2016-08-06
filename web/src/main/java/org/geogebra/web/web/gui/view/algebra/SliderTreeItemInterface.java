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
	interface CancelListener {
		void cancel();
	}

	GeoElement getGeo();

	void expandSize(int width);

	void setSliderVisible(boolean visible);

	void setOpenedMinMaxPanel(MinMaxPanel minMaxPanel);

	void setAnimPanelVisible(boolean visible);

	void restoreSize();

	void deferredResize();
}