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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
 */
public class AlgoUnitVectorVector extends AlgoUnitVector2D {

	/** Creates new AlgoOrthoVectorVector */
	public AlgoUnitVectorVector(Construction cons, VectorNDValue v,
			boolean normalize) {
		super(cons, (GeoElement) v, normalize);
	}

	@Override
	final protected void setXY() {
		x = ((GeoVec3D) inputGeo).x;
		y = ((GeoVec3D) inputGeo).y;
	}

	@Override
	final protected GeoPointND getInputStartPoint() {
		if (inputGeo instanceof GeoVector) {
			return ((GeoVector) inputGeo).getStartPoint();
		}
		return null;
	}

}
