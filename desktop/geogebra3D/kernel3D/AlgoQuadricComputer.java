package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.Matrix.Coords;

/**
 * Computer for quadric algos, saying if it's a cone, cylinder, etc.
 * 
 * @author mathieu
 *
 */
public abstract class AlgoQuadricComputer {
	
	/**
	 * 
	 * @param c
	 * @return a new quadric
	 */
	public GeoQuadric3D newQuadric(Construction c){
		return new GeoQuadric3D(c);
	}
	

	/**
	 * 
	 * @return the class name of the algo calling
	 */
	abstract public String getClassName() ;
	

	/**
	 * sets the quadric
	 * @param quadric
	 * @param origin
	 * @param direction
	 * @param number
	 */
	abstract public void setQuadric(GeoQuadric3D quadric, Coords origin, Coords direction, double number);


	/**
	 * return Double.NaN if no usable number
	 * @param v
	 * @return usable number for the quadric
	 */
	abstract public double getNumber(double v);

}
