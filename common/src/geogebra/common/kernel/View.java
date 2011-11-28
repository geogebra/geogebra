/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoElementInterface;

/**
 * Interface for views. A view registered to the kernel gets informed if elements were added, removed or changed.
 */
public interface View {
	public void add(GeoElementInterface geo);
	public void remove(GeoElementInterface geo);
	public void rename(GeoElementInterface geo);
	public void update(GeoElementInterface geo);	 
	public void updateVisualStyle(GeoElementInterface geo);	 
	public void updateAuxiliaryObject(GeoElementInterface geo);
	public void repaintView();
	public void reset(); 
	public void clearView();
	public void setMode(int mode);
	public int getViewID();
}
