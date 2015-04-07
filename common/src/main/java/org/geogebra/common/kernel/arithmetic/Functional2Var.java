package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Matrix.Coords;

/**
 * interface for all classes that can be evaluated as (u,v) -> (x,y,z) surfaces
 * 
 * TODO FunctionalNVar ?
 * 
 * @author matthieu
 *
 */
public interface Functional2Var {

	/**
	 * return point for parameters (u,v)
	 * 
	 * @param u
	 *            x-coord for evaluation
	 * @param v
	 *            y-coord for evaluation
	 * @return point for parameters (u,v)
	 */
	public Coords evaluatePoint(double u, double v);

	/**
	 * return normal vector at parameters (u,v) (return null if none)
	 * 
	 * @param u
	 *            x-coord for evaluation
	 * @param v
	 *            y-coord for evaluation
	 * @return normal vector at parameters (u,v)
	 */
	public Coords evaluateNormal(double u, double v);

	/**
	 * Returns the start parameter value
	 * 
	 * @param index
	 *            of the parameter (0 -> u / 1 -> v)
	 * @return the start parameter value
	 */
	public double getMinParameter(int index);

	/**
	 * Returns the largest possible parameter value
	 * 
	 * @param index
	 *            of the parameter (0 -> u / 1 -> v)
	 * @return the largest possible parameter value
	 */
	public double getMaxParameter(int index);

}
