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

package org.geogebra.common.cas.giac;

import static org.geogebra.common.cas.giac.GiacMacro.last;
import static org.geogebra.common.cas.giac.GiacMacro.when;

import java.util.function.BiConsumer;

public class SolveODEGiac {

	/**
	 * @param commandMap consumer for (signature, giac syntax) pairs
	 */
	public static void add(BiConsumer<String, String> commandMap) {
		commandMap.accept("SolveODE.1",
				last("[solveodeans:=?]",
						"[solveodeans:="
						+ when("(%0)[0]==equal",
						// case the equation contains only y and other variable
						// as x,by default use for variable list y,x
						// #5099
						 when(
						 "size(lname(%0) intersect [x])==0&&size(lname(quote(%0)) intersect [y])>0&&size(lname(quote(%0)) minus [y])>0",
						 "normal(map(desolve(%0,x,y),x->y=x))",
						 "normal(map(desolve(%0),x->y=x))"),
						// add y'= if it's missing
						 "normal(map(desolve(y'=%0),x->y=x))") + "],",
						 "when(length(solveodeans)==1,solveodeans[0],solveodeans)"));
		// goes through 1 point
		// SolveODE[y''=x,(1,1)]
		// goes through 1 point,y'= missing
		// SolveODE[x,(1,1)]
		// goes through 2 points
		// SolveODE[y''=x,{(1,1),(2,2)}]
		// can't do [solveodearg0:=%0] as y' is immediately simplified to 1
		commandMap.accept("SolveODE.2", "normal(y=" + when("type(%1)==DOM_LIST",
				// list of 2 points
				"desolve([%0,y(xcoord(%1[0]))=ycoord(%1[0]),y(xcoord(%1[1]))=ycoord(%1[1])],x,y)[0]",
				// one point
				"check_derivative(desolve(when((%0)[0]==equal,%0,y'=%0),x,y,%1),%1)")
				+ ")");

		// used by AlgoSolveODECAS.java
		commandMap.accept("SolveODEPoint.2",
				"regroup(check_derivative(desolve(y'=%0,x,y,%1),%1))");

		commandMap.accept("SolveODE.3",
				when("(%0)[0]==equal",
						"normal(map(desolve(%0,%2,%1),(type(%1)==DOM_IDENT)?(x->%1=x):(x->y=x))[0])",
						// add y'= if it's missing
						"normal(map(desolve(y'=%0,%2,%1),(type(%1)==DOM_IDENT)?(x->%1=x):(x->y=x))[0])"));
		commandMap.accept("SolveODE.4", when("(%0)[0]==equal",
				"normal(map(desolve(%0,%2,%1,%3),x->%1=x)[0])",
				// add y'= if it's missing
				"normal(map(desolve(y'=%0,%2,%1,%3),x->%1=x)[0])"));
		commandMap.accept("SolveODE.5", // SolveODE[y''=x,y,x,A,{B}]
				"normal(map(desolve(%0,%2,%1,%3,%4),x->%1=x)[0])");
	}
}
