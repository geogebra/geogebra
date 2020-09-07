/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.algos.AlgoElement;

/**
 * Interface for GeoFunction and GeoFunctionNVar
 * 
 * @author Markus
 *
 */
public interface FunctionalNVar extends Evaluate2Var, VarString {
	/**
	 * @param vals
	 *            values of variables
	 * @return value at vals
	 */
	public double evaluate(double[] vals);

	/**
	 * @return list of inequalities
	 */
	public IneqTree getIneqs();

	/**
	 * Returns true iff the function is boolean
	 * 
	 * @return true iff the function is boolean
	 */
	public boolean isBooleanFunction();

	/**
	 * @param label
	 *            new label
	 */
	public void setLabel(String label);

	/**
	 * @return kernel
	 */
	public Kernel getKernel();

	/**
	 * For GeoElements sets the usual defined flag, also works for
	 * ValidExpressions
	 * 
	 * @param b
	 *            whether this is defined
	 */
	public void setDefined(boolean b);

	/**
	 * @return is lhs just y= (or z=)
	 */
	public String getShortLHS();

	/**
	 * @param shortLHS
	 *            whether lhs should be just y= (or z=)
	 */
	public void setShortLHS(String shortLHS);

	/**
	 * GGB-605
	 * 
	 * @param algo
	 *            algorithm to be used for value string instead of secret
	 *            expression
	 */
	public void setSecret(AlgoElement algo);

}
