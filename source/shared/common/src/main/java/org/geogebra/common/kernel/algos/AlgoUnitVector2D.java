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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.util.MyMath;

/**
 *
 * @author Markus
 */
public abstract class AlgoUnitVector2D extends AlgoUnitVector {

	protected double x;
	protected double y;

	/** Creates new AlgoOrthoVectorVector */
	public AlgoUnitVector2D(Construction cons, GeoElement inputGeo,
			boolean normalize) {
		super(cons, inputGeo, normalize);
	}

	@Override
	final protected GeoVectorND createVector(Construction cons1) {
		GeoVector ret = new GeoVector(cons1);
		ret.z = 0.0d;
		return ret;
	}

	// line through P normal to v
	@Override
	final public void compute() {
		setXY();
		length = normalize ? MyMath.length(x, y) : 1;
		((GeoVec3D) u).x = x / length;
		((GeoVec3D) u).y = y / length;
	}

	/**
	 * 
	 * set x, y to compute vector
	 */
	abstract protected void setXY();

}
