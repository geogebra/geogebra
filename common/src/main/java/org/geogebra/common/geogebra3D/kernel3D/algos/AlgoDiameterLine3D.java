/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDiameterLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoDiameterLineND;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoDiameterLine3D extends AlgoDiameterLineND {

	private Coords direction, direction3D;

	private GeoLine diameter2D;

	private double[] diameterCoords;

	private Coords diameterOrigin, diameterDirection;

	/** Creates new AlgoJoinPoints */
	public AlgoDiameterLine3D(Construction cons, String label, GeoConicND c,
			GeoLineND g) {
		super(cons, label, c, g);
	}

	@Override
	protected void createOutput(Construction cons) {
		diameter = new GeoLine3D(cons);
		diameter2D = new GeoLine(cons);
		diameterCoords = new double[3];
	}

	// calc diameter line of v relativ to c
	@Override
	public final void compute() {

		// check direction is parallel to coord sys
		direction3D = g.getDirectionInD3();
		direction = c.getCoordSys().getNormalProjection(direction3D)[1];
		if (!Kernel.isZero(direction.getZ())) {
			diameter.setUndefined();
			return;
		}

		// update diameter line (2D)
		c.diameterLine(direction.getX(), direction.getY(), diameter2D);

		// update diameter line (3D)
		diameter2D.getCoords(diameterCoords);
		diameterDirection = c.getCoordSys().getVector(-diameterCoords[1],
				diameterCoords[0]);
		if (Kernel.isZero(diameterCoords[0])) {
			diameterOrigin = c.getCoordSys().getPoint(0,
					-diameterCoords[2] / diameterCoords[1]);
		} else {
			diameterOrigin = c.getCoordSys().getPoint(
					-diameterCoords[2] / diameterCoords[0], 0);
		}

		((GeoLine3D) diameter).setCoord(diameterOrigin, diameterDirection);

	}

}
