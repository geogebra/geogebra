/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */
package org.geogebra.common.euclidian;

/**
 * Interface for view containers (DockPanels) implementing getViewId method
 */
public interface GetViewId {
	/**
	 * @return id of contained view
	 */
	public int getViewId();
}
