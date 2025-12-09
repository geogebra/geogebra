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

package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoForExtrusion;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolyhedronPointsPyramidForExtrusion;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricLimitedConicHeightConeForExtrusion;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Class for extrusions
 * 
 * @author matthieu
 *
 */
public class DrawConify3D extends DrawExtrusionOrConify3D {

	/**
	 * constructor
	 * 
	 * @param a_view3D
	 *            view
	 * @param selectedPolygons
	 *            polygons
	 * @param selectedConics
	 *            conics
	 */
	public DrawConify3D(EuclidianView3D a_view3D,
			ArrayList<GeoPolygon> selectedPolygons,
			ArrayList<GeoConicND> selectedConics) {
		super(a_view3D, selectedPolygons, selectedConics);
	}

	@Override
	protected AlgoForExtrusion getAlgo(GeoPolygon basis, GeoNumeric height) {
		return new AlgoPolyhedronPointsPyramidForExtrusion(
				getView3D().getKernel().getConstruction(), null, basis, height);
	}

	@Override
	protected AlgoForExtrusion getAlgo(GeoConicND basis, GeoNumeric height) {
		return new AlgoQuadricLimitedConicHeightConeForExtrusion(
				getView3D().getKernel().getConstruction(), null, basis, height);
	}

}
