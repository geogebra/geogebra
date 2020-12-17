/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;

/**
 * Creates ray from point and direction vector
 *
 */
public class AlgoRayPointVector extends AlgoElement {

	private GeoPoint P; // input
	private GeoVector v; // input
	private GeoRay ray; // output

	/**
	 * Creates new ray algo
	 */
	public AlgoRayPointVector(Construction cons, GeoPoint P, GeoVector v) {
		super(cons);
		this.P = P;
		this.v = v;
		ray = new GeoRay(cons, P);
		setInputOutput(); // for AlgoElement

		// compute line through P, Q
		compute();
		setIncidence();
	}

	private void setIncidence() {
		P.addIncidence(ray, true);
	}

	@Override
	public Commands getClassName() {
		return Commands.Ray;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_RAY;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = P;
		input[1] = v;

		setOutputLength(1);
		setOutput(0, ray);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the ray
	 * 
	 * @return resulting ray
	 */
	public GeoRay getRay() {
		return ray;
	}

	/**
	 * Returns the endpoint
	 * 
	 * @return the endpoint
	 */
	GeoPoint getP() {
		return P;
	}

	/**
	 * Returns the direction
	 * 
	 * @return direction vector
	 */
	public GeoVector getv() {
		return v;
	}

	// calc the line g through P and Q
	@Override
	public final void compute() {
		// g = cross(P, v)
		GeoVec3D.lineThroughPointVector(P, v, ray);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("RayThroughAWithDirectionB",
				"Ray through %0 with direction %1", P.getLabel(tpl),
				v.getLabel(tpl));
	}

}
