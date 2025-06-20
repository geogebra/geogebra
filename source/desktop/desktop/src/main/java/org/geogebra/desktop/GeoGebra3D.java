/* 
 GeoGebra - Dynamic Mathematics for Everyone
 
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package org.geogebra.desktop;

import org.geogebra.desktop.gui.app.GeoGebraFrame3D;

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */
public class GeoGebra3D {

	/**
	 * Run the app.
	 * @param cmdArgs command line arguments
	 */
	public static void main(String[] cmdArgs) {
		GeoGebra.doMain(cmdArgs, GeoGebraFrame3D::new);
	}

}