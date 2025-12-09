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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Extension used for extrusion
 * 
 * @author Mathieu
 *
 */
public class AlgoQuadricLimitedConicHeightCylinderForExtrusion extends
		AlgoQuadricLimitedConicHeightCylinder implements AlgoForExtrusion {

	private ExtrusionComputer extrusionComputer;

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
	public AlgoQuadricLimitedConicHeightCylinderForExtrusion(Construction c,
			String[] labels, GeoConicND bottom, GeoNumberValue height) {
		super(c, labels, bottom, height);
	}

	@Override
	public void setExtrusionComputer(ExtrusionComputer extrusionComputer) {
		this.extrusionComputer = extrusionComputer;
	}

	@Override
	public void compute() {
		super.compute();
		if (extrusionComputer != null) {
			extrusionComputer.compute();
		}
	}

	@Override
	public GeoElement getGeoToHandle() {
		return getTopFace();
	}

	@Override
	public void setOutputPointsEuclidianVisible(boolean visible) {
		super.setOutputPointsEuclidianVisible(visible);
	}
}
