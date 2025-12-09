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
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCoordSys1D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Class for drawing 1D coord sys (lines, segments, ...)
 * 
 * @author matthieu
 *
 */
public abstract class DrawCoordSys1D extends DrawJoinPoints {

	/**
	 * common constructor
	 * 
	 * @param a_view3D
	 *            3D view
	 * @param cs1D
	 *            line
	 */
	public DrawCoordSys1D(EuclidianView3D a_view3D, GeoElement cs1D) {

		super(a_view3D, cs1D);
	}

	/**
	 * common constructor for previewable
	 * 
	 * @param a_view3d
	 *            view
	 */
	public DrawCoordSys1D(EuclidianView3D a_view3d) {
		super(a_view3d);

	}

	/**
	 * constructor for previewable
	 * 
	 * @param a_view3D
	 *            view
	 * @param selectedPoints
	 *            preview points
	 * @param geo
	 *            preview line
	 */
	public DrawCoordSys1D(EuclidianView3D a_view3D,
			ArrayList<GeoPointND> selectedPoints,
			GeoElement geo) {
		super(a_view3D, selectedPoints, geo);
	}

	/**
	 * 
	 * @return line
	 */
	protected GeoLineND getLine() {
		return (GeoLineND) getGeoElement();
	}

	@Override
	protected void setPreviewableCoords(GeoPointND firstPoint,
			GeoPointND secondPoint) {
		((GeoCoordSys1D) getGeoElement()).setCoordFromPoints(
				firstPoint.getInhomCoordsInD3(),
				secondPoint.getInhomCoordsInD3());
	}

	@Override
	protected Coords[] calcPoints() {
		GeoLineND cs = getLine();
		double[] minmax = getDrawMinMax();
		return new Coords[] {
				cs.getPointInD(3, minmax[0]).getInhomCoordsInSameDimension(),
				cs.getPointInD(3, minmax[1]).getInhomCoordsInSameDimension() };

	}

}
