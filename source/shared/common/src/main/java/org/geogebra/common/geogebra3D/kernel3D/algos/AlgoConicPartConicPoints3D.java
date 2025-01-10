/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicPart3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.algos.AlgoConicPartConicPointsND;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Arc or sector defined by a conic, start- and end-point.
 */
public class AlgoConicPartConicPoints3D extends AlgoConicPartConicPointsND {

	// temp parameters
	private PathParameter paramP;
	private PathParameter paramQ;
	private Coords p2d;

	/**
	 * Creates a new arc or sector algorithm. The type is either
	 * GeoConicPart.CONIC_PART_ARC or GeoConicPart.CONIC_PART_ARC
	 */
	public AlgoConicPartConicPoints3D(Construction cons, String label,
			GeoConicND circle, GeoPointND startPoint, GeoPointND endPoint,
			int type) {
		super(cons, label, circle, startPoint, endPoint, type);

	}

	@Override
	protected void setTempValues() {
		paramP = new PathParameter();
		paramQ = new PathParameter();
	}

	@Override
	protected GeoConicND newGeoConicPart(Construction cons1, int type1) {
		if (conic.isGeoElement3D()) {
			return new GeoConicPart3D(cons1, type1);
		}

		return super.newGeoConicPart(cons1, type1);
	}

	@Override
	protected void computeParameters() {

		CoordSys cs = conic.getCoordSys();

		startPoint.getInhomCoordsInD3()
				.projectPlaneInPlaneCoords(cs.getMatrixOrthonormal(), p2d);
		p2d.setZ(1);
		conic.pointChanged(p2d, paramP);

		endPoint.getInhomCoordsInD3()
				.projectPlaneInPlaneCoords(cs.getMatrixOrthonormal(), p2d);
		p2d.setZ(1);
		conic.pointChanged(p2d, paramQ);

	}

	@Override
	protected void initCoords() {
		p2d = new Coords(4);
	}

	@Override
	protected double getStartParameter() {
		return paramP.t;
	}

	@Override
	protected double getEndParameter() {
		return paramQ.t;
	}

}
