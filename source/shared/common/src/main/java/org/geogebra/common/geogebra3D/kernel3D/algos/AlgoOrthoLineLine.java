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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute a line orthogonal to two lines
 *
 * @author matthieu
 */
public abstract class AlgoOrthoLineLine extends AlgoElement3D {

	protected GeoLineND line1; // input
	private GeoLine3D line; // output
	protected Coords origin;
	protected Coords direction2;
	protected Coords origin1;
	protected Coords direction1;

	/**
	 * @param cons
	 *            construction
	 * @param line1
	 *            line
	 */
	public AlgoOrthoLineLine(Construction cons, GeoLineND line1) {
		super(cons);
		this.line1 = line1;
		line = new GeoLine3D(cons);
	}

	public GeoLine3D getLine() {
		return line;
	}

	@Override
	public void compute() {

		origin1 = line1.getPointInD(3, 0).getInhomCoordsInSameDimension();
		direction1 = line1.getPointInD(3, 1).sub(origin1);
		setOriginAndDirection2();
		getLine().setCoord(origin, direction1.crossProduct(direction2));

	}

	protected abstract void setOriginAndDirection2();

}
