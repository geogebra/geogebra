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

import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algo for Angle(Vector)
 */
public abstract class AlgoAngleVectorND extends AlgoAngle {
	/** input vector */
	protected GeoElement vec;
	/** output angle */
	protected GeoAngle angle;

	/**
	 * @param cons
	 *            construction
	 * @param vec
	 *            vector
	 */
	public AlgoAngleVectorND(Construction cons, GeoElement vec) {
		super(cons);
		this.vec = vec;

		angle = newGeoAngle(cons);
		setInputOutput(); // for AlgoElement
		compute();
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = vec;

		setOnlyOutput(angle);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting angle
	 */
	public GeoAngle getAngle() {
		return angle;
	}

	@Override
	public final String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("AngleOfA", "Angle of %0",
				vec.getLabel(tpl));

	}

	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec,
			DrawAngle drawable) {
		if (vec.isGeoVector()) {
			GeoPointND vertex = ((GeoVector) vec).getStartPoint();
			if (vertex != null) {
				vertex.getInhomCoords(m);
			}
			return vertex != null && vertex.isDefined() && !vertex.isInfinite();
		}
		m[0] = 0;
		m[1] = 0;
		return vec.isDefined();
	}

}
