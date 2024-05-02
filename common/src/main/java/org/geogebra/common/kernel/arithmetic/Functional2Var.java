package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.matrix.Coords;

/**
 * interface for all classes that can be evaluated as (u,v) -&gt; (x,y,z) surfaces
 * 
 * TODO FunctionalNVar ?
 * 
 * @author Mathieu
 *
 */
public interface Functional2Var {

	/**
	 * set point for parameters (u,v)
	 * 
	 * @param u
	 *            x-coord for evaluation
	 * @param v
	 *            y-coord for evaluation
	 * @param point
	 *            for parameters (u,v)
	 * 
	 */
	public void evaluatePoint(double u, double v, Coords point);

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
	 *            of the parameter (0 -&gt; u / 1 -&gt; v)
	 * @return the start parameter value
	 */
	public double getMinParameter(int index);

	/**
	 * Returns the largest possible parameter value
	 * 
	 * @param index
	 *            of the parameter (0 -&gt; u / 1 -&gt; v)
	 * @return the largest possible parameter value
	 */
	public double getMaxParameter(int index);

}
