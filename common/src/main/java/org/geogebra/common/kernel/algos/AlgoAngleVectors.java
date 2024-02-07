/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAngleVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Angle between vectors.
 */
public class AlgoAngleVectors extends AlgoAngleVectorsND {

	/**
	 * @param cons
	 *            construction
	 * @param v
	 *            first vector
	 * @param w
	 *            second vector
	 */
	public AlgoAngleVectors(Construction cons, GeoVectorND v,
			GeoVectorND w) {
		super(cons, v, w, null);
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) v;
		input[1] = (GeoElement) w;

		setOnlyOutput(angle);
		setDependencies(); // done by AlgoElement
	}

	// calc angle between vectors v and w
	// angle in range [0, 2pi)
	// use normalvector to
	@Override
	public void compute() {
		// |v| * |w| * sin(alpha) = det(v, w)
		// cos(alpha) = v . w / (|v| * |w|)
		// tan(alpha) = sin(alpha) / cos(alpha)
		// => tan(alpha) = det(v, w) / v . w
		double det = ((GeoVector) v).x * ((GeoVector) w).y
				- ((GeoVector) v).y * ((GeoVector) w).x;
		double prod = ((GeoVector) v).x * ((GeoVector) w).x
				+ ((GeoVector) v).y * ((GeoVector) w).y;
		double value = Math.atan2(det, prod);
		angle.setValue(value);
	}

	// ///////////////////////////////
	// TRICKS FOR XOY PLANE
	// ///////////////////////////////

	@Override
	protected int getInputLengthForXML() {
		return getInputLengthForXMLMayNeedXOYPlane();
	}

	@Override
	protected int getInputLengthForCommandDescription() {
		return getInputLengthForCommandDescriptionMayNeedXOYPlane();
	}

	@Override
	public GeoElementND getInput(int i) {
		return getInputMaybeXOYPlane(i);
	}

}
