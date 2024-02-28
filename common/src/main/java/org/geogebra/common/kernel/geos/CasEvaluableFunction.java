/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.common.kernel.geos;

import java.util.TreeMap;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.ReplaceChildrenByValues;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Interface to unify object types that allow evaluation with CAS commands, like
 * getting the derivative of a GeoFunction or GeoCurveCartesian.
 * 
 * @author Markus Hohenwarter
 */
public interface CasEvaluableFunction
		extends GeoElementND, ReplaceChildrenByValues {

	/**
	 * Sets this function by applying a GeoGebraCAS command to a function.
	 * 
	 * @param ggbCasCmd
	 *            the GeoGebraCAS command needs to include % in all places where
	 *            the function f should be substituted, e.g. "Derivative(%,x)"
	 * @param f
	 *            the function that the CAS command is applied to
	 * @param symbolic
	 *            true to keep variable names
	 * @param arbconst
	 *            arbitrary constant manager
	 * 
	 */
	public void setUsingCasCommand(String ggbCasCmd, CasEvaluableFunction f,
			boolean symbolic, ArbitraryConstantRegistry arbconst);

	/**
	 * @param tpl
	 *            string template
	 * @return string representation; variables represented by names
	 */
	public String toSymbolicString(StringTemplate tpl);

	/**
	 * 
	 * @param tpl
	 *            string template
	 * @return comma separated list of variables
	 */
	public String getVarString(StringTemplate tpl);

	/**
	 * @return input variables
	 */
	public FunctionVariable[] getFunctionVariables();

	/**
	 * clear cached CAS evaluations
	 */
	public void clearCasEvalMap();

	/**
	 * Prins CAS cache to XML
	 * 
	 * @param sb
	 *            XML builder
	 */
	public void printCASEvalMapXML(StringBuilder sb);

	/**
	 * Updates CAS cache from XML
	 * 
	 * @param casMap
	 *            values from XML
	 */
	public void updateCASEvalMap(TreeMap<String, String> casMap);
}
