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

public class GeoGebra3D extends GeoGebra {

	// hopefully not needed in JOGL 2.2.0 or 2.3.2
	// static {
	// RendererJogl.initSingleton();
	// }

	public static void main(String[] cmdArgs) {
		(new GeoGebra3D()).doMain(cmdArgs);
	}

	@Override
	protected void startGeoGebra(CommandLineArguments args) {
		// create and open first GeoGebra window
		GeoGebraFrame3D.main(args);
	}

}