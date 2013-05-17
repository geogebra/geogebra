/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.web.euclidian;

import geogebra.html5.awt.GBasicStrokeW;



/**
 * @author Markus Hohenwarter
 */
public class MyBasicStrokeW extends GBasicStrokeW {
	
	public MyBasicStrokeW(float width) {
		super(width, CAP_ROUND, JOIN_ROUND);
	}
	
}
