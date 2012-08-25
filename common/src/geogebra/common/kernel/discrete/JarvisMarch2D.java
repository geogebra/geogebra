/**
 * http://geom-java.sourceforge.net/
 * LGPL
 */

package geogebra.common.kernel.discrete;

import geogebra.common.awt.GPoint2D;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Computes the convex hull of a set of points as a single Polygon2D.
 * 
 * @author dlegland
 * 
 * Adapted for GeoGebra by Michael Borcherds
 * 
 * added check: if y-coord equal choose lowest x-coord
 * 
 */
public class JarvisMarch2D {
    
    /**
     * Computes the convex hull of a set of points as a single Polygon2D.
     * Current implementation start at the point with lowest y-coord. The points
     * are considered in counter-clockwise order. Result is an instance of
     * SimplePolygon2D. Complexity is O(n*h), with n number of points, h number
     * of points of the hull. Worst case complexity is O(n^2).
     * @param points list of points
     * @return convex hull
     */
    public static ArrayList<GPoint2D.Double> convexHull(Collection<? extends GPoint2D.Double> points) {
        // Init iteration on points
    	GPoint2D.Double lowestPoint = null;
        double y;
        double ymin = Double.MAX_VALUE;
        double xmin = Double.MAX_VALUE;

        // Iteration on the set of points to find point with lowest y-coord
        for (GPoint2D.Double point : points) {
            y = point.getY();
            if (y<ymin) {
                ymin = y;
                xmin = point.getX();
                lowestPoint = point;
            } else if (y == ymin) {
            	// if y-coord equal choose lowest x-coord
            	// needed if all Points have the same y-coord
            	double x = point.getX();
            	if (x < xmin) {
                    //ymin = y; not needed
                    xmin = x;
                    lowestPoint = point;           		
            	}
            }
        }

        // initialize array of points located on convex hull
        ArrayList<GPoint2D.Double> hullPoints = new ArrayList<GPoint2D.Double>();

        // Init iteration on points
        GPoint2D.Double currentPoint = lowestPoint;
        GPoint2D.Double nextPoint = null;
        double angle = 0;

        // Iterate on point set to find point with smallest angle with respect
        // to previous line
        do {
            hullPoints.add(currentPoint);
            nextPoint = findNextPoint(currentPoint, angle, points);
            angle = horizontalAngle(currentPoint, nextPoint);
            currentPoint = nextPoint;
        } while (currentPoint!=lowestPoint);

        // Create a polygon with points located on the convex hull
        return hullPoints;
    }

    private static GPoint2D.Double findNextPoint(GPoint2D.Double basePoint, double startAngle,
            Collection<? extends GPoint2D.Double> points) {
    	GPoint2D.Double minPoint = null;
        double minAngle = Double.MAX_VALUE;
        double angle;

        for (GPoint2D.Double point : points) {
            // Avoid to test same point
            if (basePoint.equals(point))
                continue;

            // Compute angle between current direction and next point
            angle = horizontalAngle(basePoint, point);
            angle = formatAngle(angle-startAngle);

            // Keep current point if angle is minimal
            if (angle<minAngle) {
                minAngle = angle;
                minPoint = point;
            }
        }

        return minPoint;
    }
    
	private final static double M_2PI 	= Math.PI * 2;

	/**
	 * Returns the horizontal angle formed by the line joining the two given
	 * points.
	 */
	private static double horizontalAngle(GPoint2D.Double p1,	GPoint2D.Double p2) {
		return (Math.atan2(p2.y - p1.y, p2.x - p1.x) + M_2PI) % (M_2PI);
	}

	/**
	 * Formats an angle between 0 and 2*PI.
	 * 
	 * @param angle
	 *            the angle before formatting
	 * @return the same angle, between 0 and 2*PI.
	 */
	private static double formatAngle(double angle) {
		return ((angle % M_2PI) + M_2PI) % M_2PI;
	}


}
