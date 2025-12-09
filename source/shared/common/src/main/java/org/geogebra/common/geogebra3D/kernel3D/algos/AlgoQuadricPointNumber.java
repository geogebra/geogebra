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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author ggb3D
 *
 */
public abstract class AlgoQuadricPointNumber extends AlgoQuadric {

	private GeoPointND origin;

	/**
	 * @param c
	 *            construction
	 */
	public AlgoQuadricPointNumber(Construction c, String label,
			GeoPointND origin, GeoElementND secondInput, GeoNumberValue r,
			AlgoQuadricComputer computer) {
		super(c, secondInput, r, computer);

		this.origin = origin;

		setInputOutput(
				new GeoElement[] { (GeoElement) origin,
						(GeoElement) secondInput, (GeoElement) r },
				new GeoElement[] { getQuadric() });

		compute();

		getQuadric().setLabel(label);
	}

	@Override
	public void compute() {

		// check origin
		if (!origin.isDefined() || origin.isInfinite()) {
			getQuadric().setUndefined();
			return;
		}

		// check direction
		Coords d = getDirection();

		if (d.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
			getQuadric().setUndefined();
			return;
		}

		// check number
		double r = getComputer().getNumber(getNumber().getDouble());
		if (Double.isNaN(r)) {
			getQuadric().setUndefined();
			return;
		}

		// compute the quadric
		getQuadric().setDefined();
		getComputer().setQuadric(getQuadric(), origin.getInhomCoordsInD3(), d,
				null, r, r);

	}

	/**
	 * 
	 * @return origin point
	 */
	protected GeoPointND getOrigin() {
		return origin;
	}

	/**
	 * 
	 * @return plain name
	 */
	abstract protected String getPlainName();

}
