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

import org.geogebra.common.kernel.algos.AlgoMirror;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Mirror
 * 
 * @author Zbynek
 * 
 */
public class TransformMirror extends Transform {

	/** Element used for mirroring */
	protected GeoElement mirror;

	/**
	 * @param cons
	 *            construction
	 * @param mirror
	 *            mirror
	 */
	protected TransformMirror(Construction cons, GeoElement mirror) {
		this.mirror = mirror;
		this.cons = cons;
	}

	/**
	 * @param cons
	 *            construction
	 * @param mirrorPoint
	 *            mirror point
	 */
	public TransformMirror(Construction cons, GeoPoint mirrorPoint) {
		mirror = mirrorPoint;
		this.cons = cons;
	}

	/**
	 * @param cons
	 *            construction
	 * @param mirrorCircle
	 *            mirror circle
	 */
	public TransformMirror(Construction cons, GeoConicND mirrorCircle) {
		mirror = mirrorCircle;
		this.cons = cons;
	}

	/**
	 * @param cons
	 *            construction
	 * @param mirrorLine
	 *            mirror line
	 */
	public TransformMirror(Construction cons, GeoLine mirrorLine) {
		mirror = mirrorLine;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoMirror algo = null;
		if (mirror.isGeoLine()) {
			algo = new AlgoMirror(cons, geo, (GeoLine) mirror);
		} else if (mirror.isGeoPoint()) {
			algo = new AlgoMirror(cons, geo, (GeoPointND) mirror);
		} else {
			algo = new AlgoMirror(cons, geo, (GeoConicND) mirror);
		}
		return algo;
	}

	@Override
	public boolean isAffine() {
		return !mirror.isGeoConic();
	}

	@Override
	public boolean changesOrientation() {
		return mirror.isGeoLine() || mirror.isGeoConic();
	}

}
