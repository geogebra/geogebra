/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.euclidian;

import java.awt.BasicStroke;

/**
 * @author Markus Hohenwarter
 */
public class MyBasicStroke extends BasicStroke {
	
	public MyBasicStroke(float width) {
		super(width, CAP_ROUND, JOIN_ROUND);
		//super(width, CAP_ROUND, JOIN_MITER, 5.0f);
	}

}
