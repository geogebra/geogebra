/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * Interface for views. A view registered to the kernel gets informed if elements were added, removed or changed.
 */
public interface View {
	public void add(GeoElement geo);
	public void remove(GeoElement geo);
	public void rename(GeoElement geo);
	public void update(GeoElement geo);	 
	public void updateVisualStyle(GeoElement geo);	 
	public void updateAuxiliaryObject(GeoElement geo);
	public void repaintView();
	public void reset(); 
	public void clearView();
	public void setMode(int mode);
	public int getViewID();
}
