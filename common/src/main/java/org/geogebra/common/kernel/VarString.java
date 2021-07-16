package org.geogebra.common.kernel;

import org.geogebra.common.kernel.arithmetic.FunctionVariable;

/**
 * Interface for all functions with named variables
 */
public interface VarString {

	/**
	 * Returns variable names separated by ", "
	 * 
	 * @param tpl
	 *            string template
	 * 
	 * @return variable names separated by ", "
	 */
	public String getVarString(StringTemplate tpl);

	/**
	 * @return function variables
	 */
	public FunctionVariable[] getFunctionVariables();
}
