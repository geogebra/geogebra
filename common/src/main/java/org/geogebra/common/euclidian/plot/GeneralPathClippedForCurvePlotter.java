package org.geogebra.common.euclidian.plot;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Cloner;
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
	private final List<MyPoint> cache;

	private boolean lineDrawn;
	private Coords tmpCoords = new Coords(4);

	/**
	 * constructor
	 *
	 * @param view
	 *            Euclidian view
	 */
	public GeneralPathClippedForCurvePlotter(EuclidianViewInterfaceSlim view) {
		this(view, new ArrayList<MyPoint>());
	}

	/**
	 * constructor
	 *
	 * @param view
	 *            Euclidian view
	 * @param cache
	 * 			  Point cache
	 */
	public GeneralPathClippedForCurvePlotter(EuclidianViewInterfaceSlim view, List<MyPoint> cache) {
		super(view);
		this.cache = cache;
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
		double[] p = Cloner.clone(pos);
		((EuclidianView) view).toScreenCoords(p);
		drawTo(p[0], p[1], segmentType);
		MyPoint myPoint = new MyPoint(p[0], p[1], segmentType);

		cache.add(myPoint);
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
		double[] p = Cloner.clone(pos);
		((EuclidianView) view).toScreenCoords(p);
		corner(p[0], p[1]);
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
			drawTo(x, -10, true);
			drawTo(x0, -10, true);
			return;
		}

		if ((y < 0 && y0 > h) || (y > h && y0 < 0)) {
			drawTo(-10, y, true);
			drawTo(-10, y0, true);
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
		double[] p = Cloner.clone(pos);
		((EuclidianView) view).toScreenCoords(p);
		final double x0 = p[0];
		final double y0 = p[1];

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
				drawTo(-10, d, true);
				drawTo(-10, y0, true);
			}
			drawTo(x0, y0, true);

		} else if (moveToAllowed == Gap.RESET_XMAX) {
			double d = getCurrentPoint().getY();
			if (!DoubleUtil.isEqual(d, y0)) {
				drawTo(view.getWidth() + 10, d, true);
				drawTo(view.getWidth() + 10, y0, true);
			}
			drawTo(x0, y0, true);

		} else if (moveToAllowed == Gap.RESET_YMIN) {
			double d = getCurrentPoint().getX();
			if (!DoubleUtil.isEqual(d, x0)) {
				drawTo(d, -10, true);
				drawTo(x0, -10, true);
			}
			drawTo(x0, y0, true);
		} else if (moveToAllowed == Gap.RESET_YMAX) {
			double d = getCurrentPoint().getX();
			if (!DoubleUtil.isEqual(d, x0)) {
				drawTo(getCurrentPoint().getX(), view.getHeight() + 10, true);
				drawTo(x0, view.getHeight() + 10, true);
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

		Coords coords = new Coords(point.x, point.y, point.getZ(), 1);
		if (transformSys != CoordSys.XOY) {
			transformSys.getPointFromOriginVectors(coords, tmpCoords);
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
