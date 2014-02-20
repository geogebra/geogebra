package geogebra.common.geogebra3D.euclidian3D;

import geogebra.common.kernel.Matrix.Coords;

/**
 * class for rays, spheres, etc. that can hit 3D objects in 3D view
 * @author Proprietaire
 *
 */
public class Hitting {
	
	/**
	 *  origin of the ray
	 */
	public Coords origin;

	/**
	 *  direction of the ray
	 */
	public Coords direction;

	/**
	 * constructor
	 */
	public Hitting(){
		// nothing to do for now
	}
	
	/**
	 * set the hitting ray
	 * @param origin origin
	 * @param direction direction
	 */
	public void set(Coords origin, Coords direction){
		this.origin = origin;
		this.direction = direction;
	}
}
