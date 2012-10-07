/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.StringTemplate;


/**
 * Interface for GeoFunction and GeoFunctionNVar
 * 
 * @author Markus
 *
 */
public interface FunctionalNVar extends ExpressionValue{
	/**
	 * @param vals values of variables
	 * @return value at vals
	 */
	public double evaluate(double[] vals);
	/**
	 * @return function
	 */
	public FunctionNVar getFunction();
	/**
	 * @return list of inequalities
	 */
	public IneqTree getIneqs();
	
	/**
	 * Returns true iff the function is boolean
	 * @return true iff the function is boolean
	 */
	public boolean isBooleanFunction();
	//public GeoFunctionNVar getGeoDerivative(int order, int nvar);
	/**
	 * @param tpl string template
	 * @return comma separated variable names
	 */
	public String getVarString(StringTemplate tpl);
	/**
	 * @return function expression
	 */
	public ExpressionNode getFunctionExpression();
	/**
	 * @return whether this function is defined or not
	 */
	public boolean isDefined();
	/**
	 * @param label new label
	 */
	public void setLabel(String label);
	
	
	/**
	 * 
	 * @return whether or not it's an inequality
	 * (could be GeoFunction or GeoFunctionNVar)
	 */
	public boolean isInequality();
}
