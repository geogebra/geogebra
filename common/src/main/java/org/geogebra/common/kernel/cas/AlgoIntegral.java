/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoCasBase;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Integral of a function
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntegral extends AlgoCasBase {

	private GeoNumeric var;
	private boolean allowConstant;
	private boolean computedSymbolically = true;
	private boolean numeric;
	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);

	/**
	 * @param cons
	 *            construction
	 * @param f
	 *            function
	 * @param var
	 *            variable
	 * @param allowConstant
	 *            whether arbitrary constants are allowed
	 * @param info
	 *            evaluation info
	 * @param numeric
	 *            whether NIntegral command was used
	 */
	public AlgoIntegral(Construction cons, CasEvaluableFunction f,
			GeoNumeric var, boolean allowConstant, EvalInfo info,
			boolean numeric) {
		super(cons, f, numeric ? Commands.NIntegral : Commands.Integral, info);
		this.var = var;
		this.allowConstant = allowConstant && info.isUsingCAS();
		this.numeric = numeric || !info.isUsingCAS();
		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	protected void setInputOutput() {
		int length = 1;
		if (var != null) {
			length++;
		}

		input = new GeoElement[length];
		length = 0;
		input[0] = f.toGeoElement();
		if (var != null) {
			input[++length] = var;
		}

		setOutputLength(1);
		setOutput(0, g.toGeoElement());
		setDependencies(); // done by AlgoElement
	}

	@Override
	protected void applyCasCommand(StringTemplate tpl) {
		computedSymbolically = true;
		if (f instanceof GeoFunction) {
			Function inFun = ((GeoFunction) f).getFunction();
			if (inFun == null) {
				g.setUndefined();
				return;
			}
			if (!kernel.useCASforIntegrals() || numeric) {

				inFun = inFun.getIntegralNoCAS();

				((GeoFunction) g).setFunction(inFun);
				((GeoFunction) g).setDefined(true);
				updateSecret();
				computedSymbolically = false;
				return;
			}
			// check if it's a polynomial
			PolyFunction polyDeriv = inFun.getNumericPolynomialIntegral();

			// it it is...
			if (polyDeriv != null) {
				// ... we can calculate the derivative without loading the CAS
				// (*much* faster, especially in web)
				Function funDeriv = polyDeriv.getFunction(kernel,
						inFun.getFunctionVariable(), true);

				// Log.debug(f.toString());
				// Log.debug(funDeriv.toString());

				((GeoFunction) g).setFunction(funDeriv);
				((GeoFunction) g).setDefined(true);
				computedSymbolically = false;
				updateSecret();
				return;
			}
		}

		if (f instanceof GeoFunctionNVar && numeric) {
			FunctionNVar inFun = ((GeoFunctionNVar) f).getFunction();
			if (inFun == null) {
				g.setUndefined();
				return;
			}
			FunctionVariable fv = inFun.getFunctionVariables()[0];
			for (int i = 1; i < inFun.getVarNumber(); i++) {
				if (inFun.getFunctionVariables()[i]
						.toString(StringTemplate.defaultTemplate)
						.equals(var.getLabel(StringTemplate.defaultTemplate))) {
					fv = inFun.getFunctionVariables()[i];
				}
			}
			inFun = inFun.getIntegralNoCAS(fv);

			((GeoFunctionNVar) g).setFunction(inFun);
			((GeoFunctionNVar) g).setDefined(true);
			updateSecret();
			computedSymbolically = false;
			return;
		}
		// var.getLabel() can return a number in wrong alphabet (need ASCII)

		// get variable string with tmp prefix,
		// e.g. "x" becomes "ggbtmpvarx" here
		String varStr = var != null ? var.getLabel(tpl) : f.getVarString(tpl);

		sbAE.setLength(0);
		sbAE.append("Integral[%");
		sbAE.append(",");
		sbAE.append(varStr);
		sbAE.append("]");

		// find symbolic derivative of f
		g.setUsingCasCommand(sbAE.toString(), f, true,
				this.allowConstant ? arbconst : null);
		updateSecret();
	}

	private void updateSecret() {
		if (g instanceof FunctionalNVar) {
			((FunctionalNVar) g).setSecret(numeric ? this : null);
		}

	}

	@Override
	final public String toString(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();

		if (var != null) {
			// Integral[ a x^2, x ]
			sb.append(super.toString(tpl));
		} else {
			// Michael Borcherds 2008-03-30
			// simplified to allow better Chinese translation
			sb.append(getLoc().getPlainDefault("IntegralOfA", "Integral of %0",
					f.toGeoElement().getLabel(tpl)));
		}

		if (!f.toGeoElement().isIndependent()) { // show the symbolic
													// representation too
			sb.append(": ");
			sb.append(g.toGeoElement().getLabel(tpl));
			if (g.toGeoElement() instanceof GeoFunction) {
				sb.append('(');
				sb.append(((GeoFunction) g.toGeoElement()).getVarString(tpl));
				sb.append(')');
			}
			sb.append(" = ");
			sb.append(g.toSymbolicString(tpl));
		}

		return sb.toString();
	}

	/**
	 * @return true if this was done using CAS, false if polynomial shortcut or
	 *         non-CAS integral was used
	 */
	public boolean isComputedSymbolically() {
		return computedSymbolically;
	}

}
