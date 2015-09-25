package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCoordSys1D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

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
	 * @param cs1D
	 */
	public DrawCoordSys1D(EuclidianView3D a_view3D, GeoElement cs1D) {

		super(a_view3D, cs1D);
	}

	/**
	 * common constructor for previewable
	 * 
	 * @param a_view3d
	 */
	public DrawCoordSys1D(EuclidianView3D a_view3d) {
		super(a_view3d);

	}

	/**
	 * constructor for previewable
	 * 
	 * @param a_view3D
	 * @param selectedPoints
	 * @param geo
	 */
	public DrawCoordSys1D(EuclidianView3D a_view3D, ArrayList selectedPoints,
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
