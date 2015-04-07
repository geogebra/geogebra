/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoJoinPointsSegment
 *
 * Created on 21. August 2003
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;

/**
 *
 * @author ggb3D
 * @version
 * 
 *          Calculate the ortho vector of a plane (or polygon, ...)
 * 
 */
public class AlgoOrthoVectorPlane extends AlgoElement3D {

	// input
	/** plane */
	protected GeoCoordSys2D plane;

	// output
	/** ortho vector */
	private GeoVector3D vector;

	/**
	 * Creates new AlgoIntersectLinePlane
	 * 
	 * @param cons
	 *            the construction
	 * @param label
	 *            name of point
	 */
	AlgoOrthoVectorPlane(Construction cons, String label, GeoCoordSys2D plane) {

		super(cons);

		vCoords = new Coords(4);

		this.plane = plane;

		vector = new GeoVector3D(cons);

		setInputOutput(new GeoElement[] { (GeoElement) plane },
				new GeoElement[] { vector });

		vector.setLabel(label);

		compute();

	}

	/**
	 * return the ortho vector
	 * 
	 * @return the ortho vector
	 */
	public GeoVector3D getVector() {
		return vector;
	}

	// /////////////////////////////////////////////
	// COMPUTE

	@Override
	public void compute() {

		if (!((GeoElement) plane).isDefined()) {
			vector.setUndefined();
			return;
		}

		updateCoords();

		vector.setCoords(vCoords);

	}

	protected Coords vCoords;

	/**
	 * 
	 * @return coords of the vector
	 */
	protected void updateCoords() {
		if (plane instanceof GeoPlane3D) {
			vCoords.setValues(plane.getCoordSys().getEquationVector(), 3); // get
																			// (a,
																			// b,
																			// c)
																			// from
																			// ax+by+cz+d=0
		} else {
			vCoords = plane.getCoordSys().getVz();
		}
	}

	@Override
	public Commands getClassName() {

		return Commands.OrthogonalVector;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("VectorPerpendicularToA",
				((GeoElement) plane).getLabel(tpl));

	}

	// TODO Consider locusequability

}
