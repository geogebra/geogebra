/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDiameterLineVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoDiameterVectorND;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 *
 * @author Markus
 */
public class AlgoDiameterVector3D extends AlgoDiameterVectorND {

	private Coords direction;

	private GeoLine diameter2D;

	private double[] diameterCoords;

	private Coords diameterOrigin;
	private Coords diameterDirection;

	/**
	 * Creates new 3D algo for Diameter
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param c
	 *            conic
	 * @param v
	 *            direction vector
	 */
	public AlgoDiameterVector3D(Construction cons, String label, GeoConicND c,
			GeoVectorND v) {
		super(cons, label, c, v);
	}

	@Override
	protected void createOutput(Construction cons1) {
		diameter = new GeoLine3D(cons1);
		diameter2D = new GeoLine(cons1);
		diameterCoords = new double[3];
	}

	// calc diameter line of v relative to c
	@Override
	public final void compute() {

		// check direction is parallel to coord sys
		direction = c.getCoordSys().getNormalProjection(v.getCoordsInD3())[1];
		if (!DoubleUtil.isZero(direction.getZ())) {
			diameter.setUndefined();
			return;
		}

		// update diameter line (2D)
		c.diameterLine(direction.getX(), direction.getY(), diameter2D);

		// update diameter line (3D)
		diameter2D.getCoords(diameterCoords);
		diameterDirection = c.getCoordSys().getVector(-diameterCoords[1],
				diameterCoords[0]);
		if (DoubleUtil.isZero(diameterCoords[0])) {
			diameterOrigin = c.getCoordSys().getPoint(0,
					-diameterCoords[2] / diameterCoords[1]);
		} else {
			diameterOrigin = c.getCoordSys()
					.getPoint(-diameterCoords[2] / diameterCoords[0], 0);
		}

		((GeoLine3D) diameter).setCoord(diameterOrigin, diameterDirection);
	}

}
