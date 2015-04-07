/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAxes.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoAxesQuadricND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoAxes3D extends AlgoAxesQuadricND {

	AlgoAxes3D(Construction cons, String label, GeoQuadricND c) {
		super(cons, label, c);
	}

	public AlgoAxes3D(Construction cons, String[] labels, GeoQuadricND c) {
		super(cons, labels, c);
	}

	@Override
	protected void createInput() {
		int d = c.getDimension();
		axes = new GeoLine3D[d];
		for (int i = 0; i < d; i++) {
			axes[i] = new GeoLine3D(cons);
		}

	}

	private Coords midpoint;

	// calc axes
	@Override
	public final void compute() {

		midpoint = c.getMidpoint3D();

		super.compute();

	}

	@Override
	protected void setAxisCoords(int i) {
		GeoLine3D axis = (GeoLine3D) axes[i];
		axis.setCoord(midpoint, c.getEigenvec3D(i));

	}

}
