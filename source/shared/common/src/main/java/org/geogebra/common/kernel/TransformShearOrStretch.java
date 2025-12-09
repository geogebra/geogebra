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

package org.geogebra.common.kernel;

import org.geogebra.common.kernel.algos.AlgoShearOrStretch;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoVec3D;

/**
 * Shear or stretch
 * 
 * @author Zbynek
 * 
 */
public class TransformShearOrStretch extends Transform {

	private boolean shear;
	private GeoVec3D line;
	private GeoNumberValue num;

	/**
	 * @param cons
	 *            construction
	 * @param line
	 *            line determining shear/stretch direction
	 * @param num
	 *            shear/stretch ratio
	 * @param shear
	 *            true to shear, false to stretch
	 */
	public TransformShearOrStretch(Construction cons, GeoVec3D line,
			GeoNumeric num, boolean shear) {
		this.shear = shear;
		this.line = line;
		this.num = num;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoShearOrStretch algo = new AlgoShearOrStretch(cons, geo, line, num,
				shear);
		return algo;
	}

	@Override
	public boolean isSimilar() {
		return false;
	}

	@Override
	public boolean changesOrientation() {
		return !shear && num.getDouble() < 0;
	}

}
