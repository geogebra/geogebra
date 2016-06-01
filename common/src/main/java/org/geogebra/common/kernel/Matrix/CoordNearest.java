package org.geogebra.common.kernel.Matrix;

/**
 * Used to find the nearest point of a given point.
 * 
 * @author matthieu
 *
 */
public class CoordNearest {
	
	private Coords point;
	private double currentDistance;
	private Coords currentNearest;
	
	/**
	 * 
	 * @param point
	 */
	public CoordNearest(Coords point) {
		this.point = point;
		currentDistance = Double.POSITIVE_INFINITY;
		currentNearest = new Coords(2);
	}
	
	/**
	 * check if point p is nearer than current
	 * @param p
	 * @return true if p is nearer
	 */
	public boolean check(Coords p){
		double distance = p.distance(point);
		if (distance<currentDistance){
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
	public Coords get(){
		return currentNearest;
	}

}
