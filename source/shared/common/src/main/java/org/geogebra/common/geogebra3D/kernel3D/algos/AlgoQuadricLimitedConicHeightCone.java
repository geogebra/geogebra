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
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Algo for cone from a conic and a height
 * 
 * @author mathieu
 *
 */
public class AlgoQuadricLimitedConicHeightCone
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
	public AlgoQuadricLimitedConicHeightCone(Construction c, String[] labels,
			GeoConicND bottom, GeoNumberValue height) {
		super(c, labels, bottom, height, GeoQuadricNDConstants.QUADRIC_CONE);
	}

	@Override
	protected void setQuadric(Coords o1, Coords o2, Coords d, Coords eigen,
			double r, double r2, double min, double max) {
		// getQuadric().setCone(o1,d,r, min, max);
		getQuadric().setCone(o2, d, r / max, -max, 0);
	}

	@Override
	public Commands getClassName() {
		return Commands.Cone;
	}

}
