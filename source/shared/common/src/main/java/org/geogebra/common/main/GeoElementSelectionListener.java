/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.main;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Listens to changes of selection
 */
public interface GeoElementSelectionListener {

	/**
	 * @param geo
	 *            selected element
	 * @param addToSelection
	 *            true to add to selection
	 */
	public void geoElementSelected(GeoElement geo, boolean addToSelection);

}
