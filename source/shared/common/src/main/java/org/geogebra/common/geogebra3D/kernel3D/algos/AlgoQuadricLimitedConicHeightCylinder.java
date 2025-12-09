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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Algo for cylinder from a conic and a height
 * 
 * @author mathieu
 *
 */
public class AlgoQuadricLimitedConicHeightCylinder
		extends AlgoQuadricLimitedConicHeight {

	/**
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            labels
	 * @param bottom
	 *            bottom side
	 * @param height
	 *            height
	 */
	public AlgoQuadricLimitedConicHeightCylinder(Construction c,
			String[] labels, GeoConicND bottom, GeoNumberValue height) {
		super(c, labels, bottom, height, getExtrusionType(bottom));
	}

	private static int getExtrusionType(GeoConicND type) {
		switch (type.getType()) {
		case GeoConicNDConstants.CONIC_HYPERBOLA:
			return GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_CYLINDER;
		case GeoConicNDConstants.CONIC_PARABOLA:
			return GeoQuadricNDConstants.QUADRIC_PARABOLIC_CYLINDER;
		case GeoConicNDConstants.CONIC_DOUBLE_LINE:
			return GeoQuadricNDConstants.QUADRIC_PLANE;
		case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
			return GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES;
		default:
			return GeoQuadricNDConstants.QUADRIC_CYLINDER;
		}
	}

	@Override
	protected void setQuadric(Coords o1, Coords o2, Coords d, Coords eigen,
			double r, double r2, double min, double max) {
		switch (getExtrusionType(getBottomFace())) {
		default:
			// do nothing
			break;
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			getQuadric().setCylinder(o1, d, r, min, max);
			break;
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_CYLINDER:
			getQuadric().setHyperbolicCylinder(o1, d, r, min, max);
			break;
		case GeoQuadricNDConstants.QUADRIC_PARABOLIC_CYLINDER:
			getQuadric().setParabolicCylinder(o1, d, r, min, max);
			break;
		}

	}

	@Override
	public Commands getClassName() {
		return Commands.Cylinder;
	}

}
