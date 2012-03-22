/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.gui.view.properties;

import geogebra.common.kernel.View;
import geogebra.common.main.GeoElementSelectionListener;

/**
 * Properties view
 *
 */
public interface PropertiesView extends View, GeoElementSelectionListener{
	/**
	 * Update selection
	 */
	public void updateSelection();
}
