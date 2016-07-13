package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoRay3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoRayND;

/**
 * Class for drawing a 3D ray.
 * 
 * @author matthieu
 *
 */
public class DrawRay3D extends DrawCoordSys1D {

	/**
	 * common constructor
	 * 
	 * @param a_view
	 * @param ray
	 */
	public DrawRay3D(EuclidianView3D a_view, GeoRayND ray) {
		super(a_view, (GeoElement) ray);
	}

	@Override
	protected boolean updateForItSelf() {

		updateForItSelf(true);

		return true;

	}

	/**
	 * update when the element is modified
	 * 
	 * @param updateDrawMinMax
	 *            update min and max values
	 */
	protected void updateForItSelf(boolean updateDrawMinMax) {

		if (updateDrawMinMax)
			updateDrawMinMax();

		super.updateForItSelf();

	}

	/**
	 * update min and max values
	 */
	protected void updateDrawMinMax() {

		GeoLineND line = (GeoLineND) getGeoElement();

		Coords o = line.getPointInD(3, 0).getInhomCoordsInSameDimension();
		Coords v = line.getPointInD(3, 1).getInhomCoordsInSameDimension()
				.sub(o);

		double[] minmax = getView3D().getIntervalClippedLarge(
				new double[] { 0, Double.POSITIVE_INFINITY }, o, v);

		setDrawMinMax(minmax[0], minmax[1]);

	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom()
				|| getView3D().viewChangedByTranslate()) {
			updateForItSelf();
		}
	}

	private Coords boundsMin = new Coords(3), boundsMax = new Coords(3);

	@Override
	protected void setStartEndPoints(Coords p1, Coords p2) {
		super.setStartEndPoints(p1, p2);
		double[] minmax = getDrawMinMax();

		if (minmax[0] > minmax[1]) {
			// line is not visible
			boundsMin.setX(Double.NaN);
			return;
		}

		for (int i = 1; i <= 3; i++) {
			if (p1.get(i) < p2.get(i)) {
				boundsMin.set(i, p1.get(i));
				boundsMax.set(i, p2.get(i));
			} else {
				boundsMin.set(i, p2.get(i));
				boundsMax.set(i, p1.get(i));
			}
		}
	}

	@Override
	public void enlargeBounds(Coords min, Coords max) {
		if (!Double.isNaN(boundsMin.getX())) {
			enlargeBounds(min, max, boundsMin, boundsMax);
		}
	}

	// //////////////////////////////
	// Previewable interface

	/**
	 * Constructor for previable
	 * 
	 * @param a_view3D
	 * @param selectedPoints
	 */
	@SuppressWarnings("unchecked")
	public DrawRay3D(EuclidianView3D a_view3D, ArrayList selectedPoints) {

		super(a_view3D, selectedPoints, new GeoRay3D(a_view3D.getKernel()
				.getConstruction()));

	}

}
