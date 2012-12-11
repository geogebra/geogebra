/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra;

import geogebra.main.AppletImplementation3D;

/**
 * GeoGebra applet
 * 
 * @see geogebra.main.AppletImplementation for the actual implementation
 * @author Markus Hohenwarter
 * @date 2008-10-24
 */
public class GeoGebraApplet3D extends GeoGebraApplet {

	/**
	 * Initializes the appletImplementation object. Loads geogebra_main.jar file
	 * and initializes applet if necessary.
	 */
	protected synchronized void initAppletImplementation() {
		if (isAppletFullyLoaded()) return;

		// create delegate object that implements our applet's methods
		AppletImplementation3D applImpl = new AppletImplementation3D(this);

		// initialize applet's user interface, this changes the content pane
		applImpl.initGUI();		

		// remember the applet implementation
		setAppletImplementation(applImpl);
	}		

}
