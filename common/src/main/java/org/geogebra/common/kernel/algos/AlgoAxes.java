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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoAxes extends AlgoAxesQuadricND {

	private GeoPoint P;

	AlgoAxes(Construction cons, String label, GeoConic c) {
		super(cons, label, c);
	}

	public AlgoAxes(Construction cons, String[] labels, GeoConic c) {
		super(cons, labels, c);
	}

	@Override
	protected void createInput() {
		axes = new GeoLine[2];
		axes[0] = new GeoLine(cons);
		axes[1] = new GeoLine(cons);

		P = new GeoPoint(cons);
		((GeoLine) axes[0]).setStartPoint(P);
		((GeoLine) axes[1]).setStartPoint(P);
	}

	// calc axes
	@Override
	public final void compute() {

		super.compute();

		P.setCoords(((GeoConic) c).b.getX(), ((GeoConic) c).b.getY(), 1.0);
	}

	@Override
	protected void setAxisCoords(int i) {
		GeoLine axis = (GeoLine) axes[i];
		axis.x = -((GeoConic) c).eigenvec[i].getY();
		axis.y = ((GeoConic) c).eigenvec[i].getX();
		axis.z = -(axis.x * ((GeoConic) c).b.getX() + axis.y
				* ((GeoConic) c).b.getY());

	}

}
