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
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;

/**
 * Extension of algo when used for extrusion
 * 
 * @author Mathieu
 *
 */
public class AlgoPolyhedronPointsPrismForExtrusion
		extends AlgoPolyhedronPointsPrism implements AlgoForExtrusion {

	private ExtrusionComputer extrusionComputer;

	/**
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            labels
	 * @param polygon
	 *            polygon
	 * @param height
	 *            height
	 */
	public AlgoPolyhedronPointsPrismForExtrusion(Construction c,
			String[] labels, GeoPolygon polygon, NumberValue height) {
		super(c, labels, polygon, height);
	}

	/**
	 * sets the extrusion computer
	 * 
	 * @param extrusionComputer
	 *            extrusion computer
	 */
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
