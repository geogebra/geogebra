/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.main;

import geogebra.CommandLineArguments;
import geogebra3D.App3D;

import javax.swing.JApplet;

/**
 * GeoGebra applet implementation operating on a given JApplet object.
 */
public class AppletImplementation3D extends AppletImplementation {

	public AppletImplementation3D(JApplet applet) {
		super(applet);
	}

	protected AppD buildApplication(CommandLineArguments args,
			boolean undoActive) {
		return new App3D(args, this, undoActive);
	}


}
