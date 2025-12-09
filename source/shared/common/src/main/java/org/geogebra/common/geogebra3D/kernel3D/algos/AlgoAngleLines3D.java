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

import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoAngleLinesND;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 *
 * @author mathieu
 */
public class AlgoAngleLines3D extends AlgoAngleLinesND {
	/** normal vector */
	protected Coords vn;
	private Coords o;
	private Coords v1;
	private Coords v2;

	/**
	 * @param cons
	 *            construction
	 * @param g
	 *            line
	 * @param h
	 *            line
	 * @param orientation
	 *            orientation
	 */
	AlgoAngleLines3D(Construction cons, GeoLineND g, GeoLineND h,
			GeoDirectionND orientation) {
		super(cons, g, h, orientation);
	}

	/**
	 * @param cons
	 *            construction
	 * @param g
	 *            line
	 * @param h
	 *            line
	 */
	AlgoAngleLines3D(Construction cons, GeoLineND g,
			GeoLineND h) {
		this(cons, g, h, null);
	}

	@Override
	protected GeoAngle newGeoAngle(Construction cons1) {
		GeoAngle ret = new GeoAngle3D(cons1);
		ret.setDrawableNoSlider();
		return ret;
	}

	@Override
	public void compute() {
		o = Coords.UNDEFINED; // for drawing
		if (!getg().isDefined() || !geth().isDefined()) {
			getAngle().setUndefined();
			return;
		}

		// lines origins and directions
		Coords o1 = getg().getStartInhomCoords();
		v1 = getg().getDirectionInD3();
		Coords o2 = geth().getStartInhomCoords();
		v2 = geth().getDirectionInD3();

		// normal vector
		vn = v1.crossProduct4(v2);

		if (!vn.isDefined() || vn.isZero()) { // parallel lines
			// check if lines are opposite rays
			if (!getg().isGeoRay()
					|| !geth().isGeoRay()
					|| DoubleUtil.isGreaterEqual(v1.dotproduct(v2), 0)) {
				getAngle().setValue(0);
				return;
			}

			getAngle().setValue(Math.PI);
			if (o1.equalsForKernel(o2)) { // opposite rays
				o = o1.copyVector();
				v1.completeOrthonormal(vn);
			} else {
				o = Coords.UNDEFINED; // for drawing
				return;
			}
		} else { // non parallel lines
					// nearest points
			Coords[] points = CoordMatrixUtil.nearestPointsFromTwoLines(o1, v1,
					o2, v2);

			if (!points[0].equalsForKernel(points[1])) { // lines are not
															// coplanar
				getAngle().setUndefined();
				return;
			}

			o = points[0];
			vn.normalize();
		}

		v1.calcNorm();
		double l1 = v1.getNorm();
		v2.calcNorm();
		double l2 = v2.getNorm();

		double c = v1.dotproduct(v2) / (l1 * l2); // cosinus of the angle

		getAngle().setValue(AlgoAnglePoints3D.acos(c));

	}

	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec,
			DrawAngle drawable) {

		if (drawable == null) { // TODO : this is a pgf / asymptote / pstricks
								// call
			return false;
		}

		if (!o.isDefined()) {
			return false;
		}

		Coords ov = drawable.getCoordsInView(o);
		if (!drawable.inView(ov)) {
			return false;
		}

		m[0] = ov.get()[0];
		m[1] = ov.get()[1];

		Coords v1v = drawable.getCoordsInView(v1);
		if (!drawable.inView(v1v)) {
			return false;
		}

		Coords v2v = drawable.getCoordsInView(v2);
		if (!drawable.inView(v2v)) {
			return false;
		}

		firstVec[0] = v1v.get()[0];
		firstVec[1] = v1v.get()[1];

		return true;
	}

	@Override
	public Coords getVn() {
		return vn;
	}

	@Override
	public boolean getCoordsInD3(Coords[] drawCoords) {

		if (!o.isDefined()) {
			return false;
		}

		drawCoords[0] = o;
		drawCoords[1] = v1;
		drawCoords[2] = v2;

		return true;
	}

	private AlgoAngleLines3D(GeoLineND g, GeoLineND h) {
		super(g, h);
	}

	@Override
	public AlgoAngleLines3D copy() {
		AlgoAngleLines3D copy = new AlgoAngleLines3D(g.copy(), h.copy());
		copy.o = o.copy();
		copy.v1 = v1.copy();
		copy.v2 = v2.copy();
		copy.vn = vn.copy();
		return copy;
	}

}
