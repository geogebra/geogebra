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

package org.geogebra.common.geogebra3D.kernel3D.transform;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoDilate3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.TransformDilate;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * 3D dilate
 * 
 * @author mathieu
 *
 */
public class TransformDilate3D extends TransformDilate {

	/**
	 * constructor
	 * 
	 * @param cons
	 *            construction
	 * @param ratio
	 *            ratio for dilate
	 * @param center
	 *            center for dilate
	 * 
	 */
	public TransformDilate3D(Construction cons, GeoNumberValue ratio,
			GeoPointND center) {
		super(cons, ratio, center);

	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTransformation algo = new AlgoDilate3D(cons, geo, ratio, center);
		return algo;
	}

}
