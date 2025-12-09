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

import org.geogebra.common.kernel.algos.AlgoRotate;
import org.geogebra.common.kernel.algos.AlgoRotatePoint;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Rotation
 * 
 * @author Zbynek
 * 
 */
public class TransformRotate extends Transform {
	/** center of rotation */
	protected GeoPointND center;
	/** angle of rotation */
	protected GeoNumberValue angle;

	/**
	 * @param cons
	 *            construction
	 * @param angle
	 *            rotation angle
	 */
	public TransformRotate(Construction cons, GeoNumberValue angle) {
		this.angle = angle;
		this.cons = cons;
	}

	/**
	 * @param cons
	 *            construction
	 * @param angle
	 *            rotation angle
	 * @param center
	 *            rotation center
	 */
	public TransformRotate(Construction cons, GeoNumberValue angle,
			GeoPointND center) {
		this.angle = angle;
		this.center = center;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTransformation algo = null;
		if (center == null) {
			algo = new AlgoRotate(cons, geo, angle);
		} else {
			algo = new AlgoRotatePoint(cons, geo, angle, center);
		}
		return algo;
	}

}
