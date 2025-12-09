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

package org.geogebra.common.kernel.geos;

import java.util.TreeMap;

import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.arithmetic.ArbitraryConstantRegistry;
import org.geogebra.common.kernel.arithmetic.ReplaceChildrenByValues;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Interface to unify object types that allow evaluation with CAS commands, like
 * getting the derivative of a GeoFunction or GeoCurveCartesian.
 * 
 * @author Markus Hohenwarter
 */
public interface CasEvaluableFunction
		extends GeoElementND, ReplaceChildrenByValues, VarString {

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
	 * clear cached CAS evaluations
	 */
	public void clearCasEvalMap();

	/**
	 * Prins CAS cache to XML
	 * 
	 * @param sb
	 *            XML builder
	 */
	public void printCASEvalMapXML(XMLStringBuilder sb);

	/**
	 * Updates CAS cache from XML
	 * 
	 * @param casMap
	 *            values from XML
	 */
	public void updateCASEvalMap(TreeMap<String, String> casMap);
}
