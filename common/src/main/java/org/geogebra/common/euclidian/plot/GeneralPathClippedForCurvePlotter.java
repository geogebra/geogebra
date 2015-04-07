package org.geogebra.common.euclidian.plot;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.euclidian.plot.CurvePlotter.Gap;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.util.Cloner;

/**
 * General path clipped with methods for CurvePlotter
 * 
 * @author mathieu
 *
 */
public class GeneralPathClippedForCurvePlotter extends GeneralPathClipped
		implements PathPlotter {

	/**
	 * constructor
	 * 
	 * @param view
	 *            Euclidian view
	 */
	public GeneralPathClippedForCurvePlotter(EuclidianViewInterfaceSlim view) {
		super(view);
	}

	public void lineTo(double[] pos) {
		drawTo(pos, true);
	}

	public void moveTo(double[] pos) {
		drawTo(pos, false);
	}

	public void drawTo(double[] pos, boolean lineTo) {
		double[] p = Cloner.clone(pos);
		((EuclidianView) view).toScreenCoords(p);
		drawTo(p[0], p[1], lineTo);
	}

	private void drawTo(double x, double y, boolean lineTo) {
		GPoint2D point = getCurrentPoint();

		// no points in path yet
		if (point == null) {
			moveTo(x, y);
		}

		// only add points that are more than MIN_PIXEL_DISTANCE
		// from current location
		else if (!Kernel.isEqual(x, point.getX(), view.getMinPixelDistance())
				|| !Kernel.isEqual(y, point.getY(), view.getMinPixelDistance())) {
			if (lineTo) {
				lineTo(x, y);
			} else {
				moveTo(x, y);
			}

		}

	}

	public void corner() {
		MyPoint fp = firstPoint();
		if (fp != null) {
			corner(fp.x, fp.y);
			closePath();
		}

	}

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
			return;
		}

	}

	public void firstPoint(double pos[], Gap moveToAllowed) {

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
		} else if (moveToAllowed == Gap.LINE_TO || moveToAllowed == Gap.CORNER) {
			drawTo(x0, y0, true);
		} else if (moveToAllowed == Gap.RESET_XMIN) {
			double d = getCurrentPoint().getY();
			if (!Kernel.isEqual(d, y0)) {
				drawTo(-10, d, true);
				drawTo(-10, y0, true);
			}
			drawTo(x0, y0, true);

		} else if (moveToAllowed == Gap.RESET_XMAX) {
			double d = getCurrentPoint().getY();
			if (!Kernel.isEqual(d, y0)) {
				drawTo(view.getWidth() + 10, d, true);
				drawTo(view.getWidth() + 10, y0, true);
			}
			drawTo(x0, y0, true);

		} else if (moveToAllowed == Gap.RESET_YMIN) {
			double d = getCurrentPoint().getX();
			if (!Kernel.isEqual(d, x0)) {
				drawTo(d, -10, true);
				drawTo(x0, -10, true);
			}
			drawTo(x0, y0, true);
		} else if (moveToAllowed == Gap.RESET_YMAX) {
			double d = getCurrentPoint().getX();
			if (!Kernel.isEqual(d, x0)) {
				drawTo(getCurrentPoint().getX(), view.getHeight() + 10, true);
				drawTo(x0, view.getHeight() + 10, true);
			}
			drawTo(x0, y0, true);
		}
	}

	public double[] newDoubleArray() {
		return new double[2];
	}

	public boolean copyCoords(MyPoint point, double[] ret) {

		if (!Kernel.isZero(point.getZ())) {
			return false;
		}

		ret[0] = point.x;
		ret[1] = point.y;

		return true;
	}

}
