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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.matrix.Coords;

/**
 *
 * @author ggb3D
 * 
 *         Calculate the ortho vector of a plane (or polygon, ...)
 * 
 */
public class AlgoOrthoVectorPlane extends AlgoElement3D {

	/** plane (input) */
	protected GeoCoordSys2D plane;

	/** ortho vector (output) */
	private GeoVector3D vector;

	/**
	 * output coords
	 */
	protected Coords vCoords;

	/**
	 * Creates new AlgoIntersectLinePlane
	 * 
	 * @param cons
	 *            the construction
	 * @param label
	 *            name of point
	 * @param plane
	 *            orthogonal plane
	 */
	AlgoOrthoVectorPlane(Construction cons, String label, GeoCoordSys2D plane) {
		super(cons);
		vCoords = new Coords(4);
		this.plane = plane;
		vector = new GeoVector3D(cons);
		setInputOutput(new GeoElement[] { (GeoElement) plane },
				new GeoElement[] { vector });

		compute();
		vector.setLabel(label);
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

	/**
	 * Compute coords of the vector
	 */
	protected void updateCoords() {
		if (plane instanceof GeoPlane3D) {
			// get (a, b, c) from ax+by+cz+d=0
			vCoords.setValues(plane.getCoordSys().getEquationVector(), 3);
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

}
