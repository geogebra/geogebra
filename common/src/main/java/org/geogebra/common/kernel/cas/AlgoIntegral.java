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
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
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

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 * @param var
	 *            variable
	 */
	public AlgoIntegral(Construction cons, String label,
			CasEvaluableFunction f, GeoNumeric var) {
		this(cons, f, var, true);
		g.toGeoElement().setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param f
	 *            function
	 * @param var
	 *            variable
	 * @param allowConstant
	 *            whether arbitrary constants are allowed
	 */
	public AlgoIntegral(Construction cons, CasEvaluableFunction f,
			GeoNumeric var, boolean allowConstant) {
		super(cons, f, Commands.Integral);
		this.var = var;
		this.allowConstant = allowConstant;

		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	protected void setInputOutput() {
		int length = 1;
		if (var != null)
			length++;

		input = new GeoElement[length];
		length = 0;
		input[0] = f.toGeoElement();
		if (var != null)
			input[++length] = var;

		setOutputLength(1);
		setOutput(0, g.toGeoElement());
		setDependencies(); // done by AlgoElement
	}

	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);

	@Override
	protected void applyCasCommand(StringTemplate tpl) {
		computedSymbolically = true;
		if (f instanceof GeoFunction) {
			Function inFun = ((GeoFunction) f).getFunction();

			if (!kernel.useCASforIntegrals()) {

				inFun = inFun.getIntegralNoCAS();

				if (inFun == null) {
					((GeoFunction) g).setDefined(false);
					return;
				}

				((GeoFunction) g).setFunction(inFun);
				((GeoFunction) g).setDefined(true);
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
						inFun.getFunctionVariable());

				// App.debug(f.toString());
				// App.debug(funDeriv.toString());

				((GeoFunction) g).setFunction(funDeriv);
				((GeoFunction) g).setDefined(true);
				computedSymbolically = false;
				return;
			}
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
			sb.append(getLoc().getPlain("IntegralOfA",
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

	// TODO Consider locusequability

}
