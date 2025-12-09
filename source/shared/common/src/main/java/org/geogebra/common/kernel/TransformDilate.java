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

import org.geogebra.common.kernel.algos.AlgoDilate;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Dilation
 * 
 * @author Zbynek
 * 
 */
public class TransformDilate extends Transform {

	/** dilation coefficient */
	protected GeoNumberValue ratio;
	/** dilation center */
	protected GeoPointND center;

	/**
	 * @param cons
	 *            construction
	 * @param ratio
	 *            dilation ratio
	 */
	public TransformDilate(Construction cons, GeoNumberValue ratio) {
		this.ratio = ratio;
		this.cons = cons;
	}

	/**
	 * @param cons
	 *            construction
	 * @param ratio
	 *            dilation ratio
	 * @param center
	 *            dilation center
	 */
	public TransformDilate(Construction cons, GeoNumberValue ratio,
			GeoPointND center) {
		this.ratio = ratio;
		this.center = center;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoDilate algo = new AlgoDilate(cons, geo, ratio, center);
		return algo;
	}

}
