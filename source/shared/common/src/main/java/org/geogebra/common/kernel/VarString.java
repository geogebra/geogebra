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
