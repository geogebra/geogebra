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
	double evaluate(double[] vals);

	/**
	 * @return list of inequalities
	 */
	IneqTree getIneqs();

	/**
	 * Returns true iff the function is boolean
	 * 
	 * @return true iff the function is boolean
	 */
	boolean isBooleanFunction();

	/**
	 * Set a flag to force this to resolve to inequality
	 * @param inequality inequality flag
	 */
	void setForceInequality(boolean inequality);

	/**
	 * @return whether this is forced to be processed as inequality
	 */
	boolean isForceInequality();

	/**
	 * @param label
	 *            new label
	 */
	void setLabel(String label);

	/**
	 * @return kernel
	 */
	Kernel getKernel();

	/**
	 * For GeoElements sets the usual defined flag, also works for
	 * ValidExpressions
	 * 
	 * @param b
	 *            whether this is defined
	 */
	void setDefined(boolean b);

	/**
	 * @return is lhs just y= (or z=)
	 */
	String getShortLHS();

	/**
	 * @param shortLHS
	 *            whether lhs should be just y= (or z=)
	 */
	void setShortLHS(String shortLHS);

	/**
	 * GGB-605
	 * 
	 * @param algo
	 *            algorithm to be used for value string instead of secret
	 *            expression
	 */
	void setSecret(AlgoElement algo);
}
