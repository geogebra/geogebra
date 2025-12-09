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

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * General path clipped with methods for CurvePlotter
 *
 * @author mathieu
 *
 */
public class GeneralPathClippedForCurvePlotter extends GeneralPathClipped
		implements PathPlotter {

	private static final double EPSILON = 0.0001;

	public static final double MIN_PIXEL_DISTANCE = 0.5; // pixels
	private static final double OFFSCREEN_PX = 10;

	private boolean lineDrawn;
	private final Coords tmpCoords = new Coords(4);
	private final boolean default2dView;

	/**
	 * constructor
	 *
	 * @param view
	 *            Euclidian view
	 */
	public GeneralPathClippedForCurvePlotter(EuclidianViewInterfaceSlim view) {
		super(view);
		default2dView = view.isDefault2D();
	}

	@Override
	public void lineTo(double[] pos) {
		drawTo(pos, SegmentType.LINE_TO);
	}

	@Override
	public void moveTo(double[] pos) {
		drawTo(pos, SegmentType.MOVE_TO);
	}

	@Override
	public void drawTo(double[] pos, SegmentType segmentType) {
		double x = view.toScreenCoordXd(pos[0]);
		double y = view.toScreenCoordYd(pos[1]);
		drawTo(x, y, segmentType);
	}

	/**
	 *
	 * @param x coordinate.
	 * @param y coordinate.
	 * @param lineTo type for drawing the segment
	 */
	public void drawTo(double x, double y, SegmentType lineTo) {
		GPoint2D point = getCurrentPoint();

		// no points in path yet
		if (point == null) {
			moveTo(x, y);
			lineDrawn = false;
			return;
		}

		boolean distant = !DoubleUtil.isEqual(x, point.getX(), MIN_PIXEL_DISTANCE)
				|| !DoubleUtil.isEqual(y, point.getY(), MIN_PIXEL_DISTANCE);
		if (lineTo == SegmentType.CONTROL || lineTo == SegmentType.CURVE_TO
				|| lineTo == SegmentType.ARC_TO
				|| lineTo == SegmentType.AUXILIARY) {
			distant = true;
		}
		// only add points that are more than MIN_PIXEL_DISTANCE
		// from current location
		boolean isLine = lineTo != SegmentType.MOVE_TO;
		if (!distant && isLine == lineDrawn) {
			return;
		}

		if (lineTo == SegmentType.CONTROL || lineTo == SegmentType.CURVE_TO) {
			addPoint(x, y, lineTo);
			lineDrawn = true;
			return;
		}

		if (isLine) {
			// Safari does not draw points with moveTo lineTo
			if (x == point.getX() && y == point.getY()) {
				addPoint(x + EPSILON, y, lineTo);
			} else {
				addPoint(x, y, lineTo);
			}
			lineDrawn = true;
		} else {
			moveTo(x, y);
			lineDrawn = false;
		}
	}

	private void drawTo(double x, double y, boolean lineTo) {
		drawTo(x, y, lineTo ? SegmentType.LINE_TO : SegmentType.MOVE_TO);
	}

	@Override
	public void corner() {
		MyPoint fp = firstPoint();
		if (fp != null) {
			corner(fp.x, fp.y);
			closePath();
		}
	}

	@Override
	public void corner(double[] pos) {
		double x = view.toScreenCoordXd(pos[0]);
		double y = view.toScreenCoordYd(pos[1]);
		corner(x, y);
	}

	private void corner(double x0, double y0) {
		int w = view.getWidth();
		int h = view.getHeight();
		GPoint2D pt = getCurrentPoint();
		if (pt == null) {
			return;
		}
		double x = pt.getX();
		double y = pt.getY();

		if ((x < 0 && x0 > w) || (x > w && x0 < 0)) {
			drawTo(x, -OFFSCREEN_PX, true);
			drawTo(x0, -OFFSCREEN_PX, true);
			return;
		}

		if ((y < 0 && y0 > h) || (y > h && y0 < 0)) {
			drawTo(-OFFSCREEN_PX, y, true);
			drawTo(-OFFSCREEN_PX, y0, true);
			return;
		}

		if ((x > w || x < 0) && (y0 < 0 || y0 > h)) {
			drawTo(x, y0, true);
			return;
		}

		if ((x0 > w || x0 < 0) && (y < 0 || y > h)) {
			drawTo(x0, y, true);
		}
	}

	@Override
	public void firstPoint(double[] pos, Gap moveToAllowed) {
		double x0 = view.toScreenCoordXd(pos[0]);
		double y0 = view.toScreenCoordYd(pos[1]);

		// FIRST POINT
		// c(t1) and c(t2) are defined, lets go ahead and move to our first
		// point (x0, y0)
		// note: lineTo will automatically do a moveTo if this is the first gp
		// point
		if (moveToAllowed == Gap.MOVE_TO) {
			drawTo(x0, y0, false);
		} else if (moveToAllowed == Gap.LINE_TO
				|| moveToAllowed == Gap.CORNER) {
			drawTo(x0, y0, true);
		} else if (moveToAllowed == Gap.RESET_XMIN) {
			double d = getCurrentPoint().getY();
			if (!DoubleUtil.isEqual(d, y0)) {
				drawTo(-OFFSCREEN_PX, d, true);
				drawTo(-OFFSCREEN_PX, y0, true);
			}
			drawTo(x0, y0, true);

		} else if (moveToAllowed == Gap.RESET_XMAX) {
			double d = getCurrentPoint().getY();
			if (!DoubleUtil.isEqual(d, y0)) {
				drawTo(view.getWidth() + OFFSCREEN_PX, d, true);
				drawTo(view.getWidth() + OFFSCREEN_PX, y0, true);
			}
			drawTo(x0, y0, true);

		} else if (moveToAllowed == Gap.RESET_YMIN) {
			double d = getCurrentPoint().getX();
			if (!DoubleUtil.isEqual(d, x0)) {
				drawTo(d, -OFFSCREEN_PX, true);
				drawTo(x0, -OFFSCREEN_PX, true);
			}
			drawTo(x0, y0, true);
		} else if (moveToAllowed == Gap.RESET_YMAX) {
			double d = getCurrentPoint().getX();
			if (!DoubleUtil.isEqual(d, x0)) {
				drawTo(getCurrentPoint().getX(), view.getHeight() + OFFSCREEN_PX, true);
				drawTo(x0, view.getHeight() + OFFSCREEN_PX, true);
			}
			drawTo(x0, y0, true);
		}
	}

	@Override
	public double[] newDoubleArray() {
		return new double[2];
	}

	@Override
	public boolean copyCoords(MyPoint point, double[] ret,
			CoordSys transformSys) {
		boolean noTransform = transformSys == CoordSys.XOY;
		if (noTransform && default2dView) {
			ret[0] = point.getX();
			ret[1] = point.getY();
			return point.getZ() == 0;
		}
		Coords coords = new Coords(point.x, point.y, point.getZ(), 1);
		if (!noTransform) {
			transformSys.getPointFromOriginVectors(point.x, point.y, tmpCoords);
			coords.set(tmpCoords);
		}
		Coords projection = ((EuclidianView) view).getCoordsForView(coords);

		ret[0] = projection.getX();
		ret[1] = projection.getY();
		return true;
	}

	@Override
	public void endPlot() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean supports(CoordSys transformSys) {
		return view.isInPlane(transformSys);
	}

}
