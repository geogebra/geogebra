/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
