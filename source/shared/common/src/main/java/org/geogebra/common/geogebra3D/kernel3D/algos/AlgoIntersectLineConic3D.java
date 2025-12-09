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

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 *
 * @author mathieu
 */
public class AlgoIntersectLineConic3D extends AlgoIntersectConic3D {

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param g
	 *            line
	 * @param c
	 *            conic
	 */
	AlgoIntersectLineConic3D(Construction cons, String label, GeoLineND g,
			GeoConicND c) {
		this(cons, g, c);
		LabelManager.setLabels(label, P);
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param g
	 *            line
	 * @param c
	 *            conic
	 */
	AlgoIntersectLineConic3D(Construction cons, String[] labels, GeoLineND g,
			GeoConicND c) {
		this(cons, g, c);
		LabelManager.setLabels(labels, P);
	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECT;
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param g
	 *            line
	 * @param c
	 *            conic
	 */
	AlgoIntersectLineConic3D(Construction cons, GeoLineND g, GeoConicND c) {
		super(cons, (GeoElement) g, c);
	}

	/**
	 * 
	 * @return line input
	 */
	GeoLineND getLine() {
		return (GeoLineND) getFirstGeo();
	}

	@Override
	protected Coords getFirstGeoStartInhomCoords() {
		return getLine().getStartInhomCoords();
	}

	@Override
	protected Coords getFirstGeoDirectionInD3() {
		return getLine().getDirectionInD3();
	}

	@Override
	protected boolean getFirstGeoRespectLimitedPath(Coords p) {
		return getLine().respectLimitedPath(p, Kernel.STANDARD_PRECISION);
	}

	@Override
	protected void checkIsOnFirstGeo(GeoPoint3D p) {
		if (!p.isDefined()) {
			return;
		}
		if (!getLine().respectLimitedPath(p.getCoords(), Kernel.MIN_PRECISION)) {
			p.setUndefined();
		}
	}
}
