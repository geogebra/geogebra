package org.geogebra.common.kernel.matrix;

/**
 * Used to find the nearest point of a given point.
 * 
 * @author Mathieu
 *
 */
public class CoordNearest {

	private Coords point;
	private double currentDistance;
	private Coords currentNearest;

	/**
	 * 
	 * @param point
	 *            reference point
	 */
	public CoordNearest(Coords point) {
		this.point = point;
		currentDistance = Double.POSITIVE_INFINITY;
		currentNearest = new Coords(2);
	}

	/**
	 * check if point p is nearer than current
	 * 
	 * @param p
	 *            point
	 * @return true if p is nearer to reference point
	 */
	public boolean check(Coords p) {
		double distance = p.distance(point);
		if (distance < currentDistance) {
			currentDistance = distance;
			currentNearest.set2(p);
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @return nearest point
	 */
	public Coords get() {
		return currentNearest;
	}

}
