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
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Class for drawing lines
 * 
 * @author mathieu
 *
 */
public class DrawLine3D extends DrawCoordSys1D {
	private Coords boundsMin = new Coords(3);
	private Coords boundsMax = new Coords(3);

	/**
	 * common constructor
	 * 
	 * @param a_view3D
	 *            view
	 * @param line
	 *            line
	 */
	public DrawLine3D(EuclidianView3D a_view3D, GeoLineND line) {
		this(a_view3D, line, null);
	}

	/**
	 * Constructor for helpers
	 * 
	 * @param a_view3D
	 *            view
	 * @param line
	 *            line
	 * @param geo2
	 *            parent geo
	 */
	public DrawLine3D(EuclidianView3D a_view3D, GeoLineND line,
			GeoElement geo2) {
		super(a_view3D);
		init((GeoElement) line, geo2);
	}

	/**
	 * constructor for previewable
	 * 
	 * @param a_view3D
	 *            view
	 * @param selectedPoints
	 *            endpoints
	 */
	public DrawLine3D(EuclidianView3D a_view3D, ArrayList<GeoPointND> selectedPoints) {
		super(a_view3D, selectedPoints, new GeoLine3D(a_view3D.getKernel().getConstruction()));
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
		if (updateDrawMinMax) {
			updateDrawMinMax();
		}

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

		double[] minmax = getView3D().getIntervalClippedLarge(new double[] {
				Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY }, o, v);

		setDrawMinMax(minmax[0], minmax[1]);
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom()
				|| getView3D().viewChangedByTranslate()) {
			updateForItSelf();
		}
	}

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
	public void enlargeBounds(Coords min, Coords max, boolean dontExtend) {
		if (!Double.isNaN(boundsMin.getX())) {
            if (dontExtend) {
                reduceBounds(boundsMin, boundsMax);
            }
			enlargeBounds(min, max, boundsMin, boundsMax);
		}
	}

}
