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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Transform;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Generic affine transform
 * 
 * @author Zbynek
 * 
 */
public class TransformApplyMatrix extends Transform {

	private GeoList matrix;

	/**
	 * @param cons
	 *            construction
	 * @param matrix
	 *            transform matrix (2x2 or 3x3 for 2D)
	 */
	public TransformApplyMatrix(Construction cons, GeoList matrix) {
		this.matrix = matrix;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		return new AlgoApplyMatrix(cons, geo, matrix);
	}

	@Override
	public boolean isSimilar() {
		return false;
	}

	@Override
	public boolean changesOrientation() {
		AlgoTransformation at = getTransformAlgo(new GeoPoint(cons));
		cons.removeFromConstructionList(at);
		return at.swapOrientation(null);
	}

}
