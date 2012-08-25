package geogebra.common.kernel.discrete;
import geogebra.common.awt.GPoint2D;

import java.util.Comparator;

/**
 * http://geom-java.sourceforge.net/
 * LGPL
 *
 */
public class CompareByPseudoAngle implements Comparator<GPoint2D.Double>{
	private GPoint2D.Double basePoint;
	@SuppressWarnings("javadoc")
	public CompareByPseudoAngle(GPoint2D.Double base) {
		this.basePoint = base;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(GPoint2D.Double point1, GPoint2D.Double point2) {
		double angle1 = pseudoAngle(basePoint, point1);
		double angle2 = pseudoAngle(basePoint, point2);

		if(angle1<angle2) return -1;
		if(angle1>angle2) return +1;
		//TODO: and what about colinear points ?
		return 0;
	}

	/**
	 * <p>
	 * Computes the pseudo-angle of a line joining the 2 points. The
	 * pseudo-angle has same ordering property has natural angle, but is
	 * expected to be computed faster. The result is given between 0 and 360.
	 * </p>
	 * 
	 * @param p1
	 *            the initial point
	 * @param p2
	 *            the final point
	 * @return the pseudo angle of line joining p1 to p2
	 */
	private static double pseudoAngle(GPoint2D.Double p1, GPoint2D.Double p2) {
		double dx = p2.x - p1.x;
		double dy = p2.y - p1.y;
		double s = Math.abs(dx) + Math.abs(dy);
		double t = (s == 0) ? 0.0 : dy / s;
		if (dx < 0) {
			t = 2 - t;
		} else if (dy < 0) {
			t += 4;
		}
		return t * 90;
	}
}

