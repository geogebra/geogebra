/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Vector v = P - (0, 0)
 * 
 * @author Markus
 */
public class AlgoVectorPoint extends AlgoElement {

	private GeoPointND P; // input
	private GeoVectorND v; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param P
	 *            input point
	 */
	public AlgoVectorPoint(Construction cons, String label, GeoPointND P) {
		this(cons, P);
		v.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param P
	 *            input point
	 */
	public AlgoVectorPoint(Construction cons, GeoPointND P) {
		super(cons);
		this.P = P;

		// create new vector
		v = createNewVector();
		setInputOutput();

		compute();
	}

	/**
	 * @return new vector (overridden in 3D)
	 */
	protected GeoVectorND createNewVector() {

		return new GeoVector(cons);
	}

	@Override
	public Commands getClassName() {
		return Commands.Vector;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_VECTOR_FROM_POINT;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = (GeoElement) P;

		setOnlyOutput(v);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return output vector
	 */
	public GeoVectorND getVector() {
		return v;
	}

	/**
	 * @return input point
	 */
	public GeoPointND getP() {
		return P;
	}

	// calc vector OP
	@Override
	public final void compute() {
		if (P.isFinite()) {
			setCoords();
		} else {
			v.setUndefined();
		}
	}

	/**
	 * Updates coords of v using P
	 */
	protected void setCoords() {
		GeoVector v2D = (GeoVector) v;
		v2D.x = ((GeoPoint) P).inhomX;
		v2D.y = ((GeoPoint) P).inhomY;
		v2D.z = 0.0;
	}

}
