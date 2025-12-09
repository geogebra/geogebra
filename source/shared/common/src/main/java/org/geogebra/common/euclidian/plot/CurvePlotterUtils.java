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

package org.geogebra.common.euclidian.plot;

import java.util.List;

import org.apache.commons.math3.util.Cloner;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.matrix.CoordSys;

public class CurvePlotterUtils {
	/**
	 * draw list of points
	 *
	 * @param gp
	 *            path plotter that actually draws the points list
	 * @param pointList
	 *            list of points
	 * @param transformSys
	 *            coordinate system to be applied on 2D points
	 * @return last point drawn
	 */
	static public double[] draw(PathPlotter gp,
			List<? extends MyPoint> pointList, CoordSys transformSys) {
		double[] coords = gp.newDoubleArray();
		int size = pointList.size();
		boolean supports = gp.supports(transformSys);
		if (!supports || size == 0) {
			return coords;
		}
		// this is for making sure that there is no lineto from nothing
		// and there is no lineto if there is an infinite point between the
		// points
		boolean linetofirst = true;
		double[] lastMove = null;
		for (MyPoint p : pointList) {
			// don't add infinite points
			// otherwise hit-testing doesn't work
			if (p.isFinite() && gp.copyCoords(p, coords, transformSys)) {
				if (isArcOrCurvePart(p) && !linetofirst) {
					gp.drawTo(coords, p.getSegmentType());
					lastMove = null;
				} else if (p.getLineTo() && !linetofirst) {
					gp.lineTo(coords);
					lastMove = null;
				} else {
					lastMove = moveTo(gp, coords, lastMove);
				}
				linetofirst = false;
			} else {
				linetofirst = true;
			}
		}
		if (lastMove != null) {
			gp.lineTo(lastMove);
		}

		gp.endPlot();

		return coords;
	}

	private static double[] moveTo(PathPlotter gp, double[] coords,
			double[] previousLastMove) {
		double[] lastMove;
		if (previousLastMove != null) {
			gp.lineTo(previousLastMove);
			lastMove = previousLastMove;
		} else {
			lastMove = new double[coords.length];
		}
		gp.moveTo(coords);
		Cloner.cloneTo(coords, lastMove);
		return lastMove;
	}

	private static boolean isArcOrCurvePart(MyPoint p) {
		return p.getSegmentType() == SegmentType.CURVE_TO
				|| p.getSegmentType() == SegmentType.CONTROL
				|| p.getSegmentType() == SegmentType.ARC_TO
				|| p.getSegmentType() == SegmentType.AUXILIARY;
	}
}
