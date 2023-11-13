/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoRotateVector.java
 *
 * Created on 24. September 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoVector;

/**
 *
 * @author Markus
 */
public class AlgoRotateVector extends AlgoElement {

	private GeoVector A; // input
	private GeoNumeric angle; // input
	private GeoVector B; // output

	/**
	 * Creates new algo for vector rotation
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            vector
	 * @param angle
	 *            angle
	 */
	AlgoRotateVector(Construction cons, String label, GeoVector A,
			GeoNumeric angle) {
		super(cons);
		this.A = A;
		this.angle = angle;

		// create new Vector
		B = new GeoVector(cons);
		setInputOutput();

		compute();
		B.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Rotate;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_ROTATE_BY_ANGLE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = A;
		input[1] = angle;

		setOnlyOutput(B);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the input vector
	 * 
	 * @return input vector
	 */
	GeoVector getVector() {
		return A;
	}

	/**
	 * Returns the rotation angle
	 * 
	 * @return rotation angle
	 */
	GeoNumeric getAngle() {
		return angle;
	}

	/**
	 * Returns the resulting vector
	 * 
	 * @return resulting vector
	 */
	GeoVector getRotatedVector() {
		return B;
	}

	// calc rotated Vector
	@Override
	public final void compute() {
		B.setCoords(A);
		B.rotate(angle);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("ARotatedByAngleB",
				"%0 rotated by angle %1", A.getLabel(tpl), angle.getLabel(tpl));
	}

}
