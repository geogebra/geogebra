/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;

/**
 * Polynomial common denominator
 * 
 * @author Michael Borcherds
 */
public class AlgoPolynomialCD extends AlgoElement {

	private GeoFunction f1, f2; // input
	private GeoFunction g; // output

	private StringBuilder sb = new StringBuilder();
	/**
     * @param cons construction
     * @param label label for output
     * @param f1 first function
     * @param f2 second function
     */
	public AlgoPolynomialCD(Construction cons, String label, GeoFunction f1,
			GeoFunction f2) {
		super(cons);
		this.f1 = f1;
		this.f2 = f2;

		g = new GeoFunction(cons);
		setInputOutput(); // for AlgoElement
		compute();
		g.setLabel(label);
	}

	@Override
	public Commands getClassName() {
    	return Commands.CommonDenominator;
    } 

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = f1;
		input[1] = f2;

		setOutputLength(1);
		setOutput(0, g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting common denominator
	 */
	public GeoFunction getResult() {
		return g;
	}
	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);
	@Override
	public final void compute() {
		if (!f1.isDefined() || !f2.isDefined()) {
			g.setUndefined();
			return;
		}

		try {
			// get function and function variable string using temp variable
			// prefixes,
			// e.g. f(x) = a x^2 returns {"ggbtmpvara ggbtmpvarx^2",
			// "ggbtmpvarx"}
			String[] funVarStr1 = f1.getTempVarCASString(false);
			String[] funVarStr2 = f2.getTempVarCASString(false);

			sb.setLength(0);
			sb.append("CommonDenominator(");
			sb.append(funVarStr1[0]); // function f1 expression
			sb.append(",");
			sb.append(funVarStr2[0]); // function f2 expression
			sb.append(")");
			// cached evaluation of MPReduce as we are only using variable
			// values
			String functionOut = kernel
					.evaluateCachedGeoGebraCAS(sb.toString(),arbconst);
			if (functionOut == null || functionOut.length() == 0) {
				g.setUndefined();
			} else {
				// read result back into function
				g.set(kernel.getAlgebraProcessor().evaluateToFunction(
						functionOut, false));
			}
		} catch (Throwable th) {
			g.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getCommandDescription(tpl);
	}

	// TODO Consider locusequability

}
