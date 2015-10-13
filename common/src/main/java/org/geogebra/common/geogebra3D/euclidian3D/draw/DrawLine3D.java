package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.ArrayList;

import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 * Class for drawing lines
 * 
 * @author matthieu
 *
 */
public class DrawLine3D extends DrawCoordSys1D implements Previewable {

	/**
	 * common constructor
	 * 
	 * @param a_view3D
	 * @param line
	 */
	public DrawLine3D(EuclidianView3D a_view3D, GeoLineND line) {

		this(a_view3D, line, null);
	}

	/**
	 * Constructor for helpers
	 * 
	 * @param a_view3D
	 * @param line
	 * @param geo2
	 */
	public DrawLine3D(EuclidianView3D a_view3D, GeoLineND line, GeoElement geo2) {

		super(a_view3D);
		init((GeoElement) line, geo2);

	}

	/**
	 * @param line
	 *            line
	 * @param geo2
	 *            geo caller
	 */
	protected void init(GeoElement line, GeoElement geo2) {

		super.init(line);

	}


	@Override
	protected boolean updateForItSelf() {

		updateForItSelf(true);
		return true;

	}

	/**
	 * update the drawable when the element changes
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

		GeoLineND line = getLine();

		Coords o = line.getPointInD(3, 0).getInhomCoordsInSameDimension();
		Coords v = line.getPointInD(3, 1).getInhomCoordsInSameDimension()
				.sub(o);

		double[] minmax = getView3D().getIntervalClippedLarge(
				new double[] { Double.NEGATIVE_INFINITY,
						Double.POSITIVE_INFINITY }, o, v);

		setDrawMinMax(minmax[0], minmax[1]);
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom()
				|| getView3D().viewChangedByTranslate())
			updateForItSelf();
	}

	// //////////////////////////////
	// Previewable interface

	/**
	 * constructor for previewable
	 * 
	 * @param a_view3D
	 * @param selectedPoints
	 */
	@SuppressWarnings("unchecked")
	public DrawLine3D(EuclidianView3D a_view3D, ArrayList selectedPoints) {

		super(a_view3D, selectedPoints, new GeoLine3D(a_view3D.getKernel()
				.getConstruction()));

	}

}
