/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.
//
This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.gui.view.algebra.AlgebraController;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * AlgebraView with tree for free and dependent objects.
 * 
 */

public class AlgebraViewW extends AlgebraViewWeb {

	/** Creates new AlgebraView */
	public AlgebraViewW(AlgebraController algCtrl) {

		super(algCtrl);


		
	}

	public Object getPathForLocation(int x, int y) {
		// TODO: auto-generated method stub
		return null;
	}


	// temporary proxies for the temporary implementation of AlgebraController in common
	public GeoElement getGeoElementForPath(Object tp) {
		//return getGeoElementForPath((TreePath)tp);
		return null;
	}

	public GeoElement getGeoElementForLocation(Object tree, int x, int y) {
		//return getGeoElementForLocation((JTree)tree, x, y);
		return null;
	}

	public Object getPathBounds(Object tp) {
		//return getPathBounds((TreePath)tp);
		return null;
	}
	// temporary proxies end







} // AlgebraView
