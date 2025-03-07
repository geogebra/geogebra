/* 
 GeoGebra - Dynamic Mathematics for Everyone
 
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */

package org.geogebra.desktop;

import org.geogebra.desktop.gui.app.GeoGebraFrame3D;

public class GeoGebra3D {

	public static void main(String[] cmdArgs) {
		GeoGebra.doMain(cmdArgs, GeoGebraFrame3D::new);
	}

}