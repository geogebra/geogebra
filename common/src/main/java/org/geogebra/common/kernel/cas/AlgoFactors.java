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
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Try to expand the given function
 * 
 * @author Michael Borcherds
 */
public class AlgoFactors extends AlgoElement implements UsesCAS {

	private GeoFunction f; // input
	private GeoList g; // output

	private StringBuilder sb = new StringBuilder();

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 */
	public AlgoFactors(Construction cons, String label, GeoFunction f) {
		super(cons);
		cons.addCASAlgo(this);
		this.f = f;

		g = new GeoList(cons);
		setInputOutput(); // for AlgoElement
		compute();
		g.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Factors;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = f;

		setOutputLength(1);
		setOutput(0, g);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return list of factors
	 */
	public GeoList getResult() {
		return g;
	}

	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);

	@Override
	public final void compute() {
		if (!f.isDefined()) {
			g.setUndefined();
			return;
		}

		try {
			// get function and function variable string using temp variable
			// prefixes,
			// e.g. f(x) = a x^2 returns {"ggbtmpvara ggbtmpvarx^2",
			// "ggbtmpvarx"}
			String[] funVarStr = f.getTempVarCASString(false);

			sb.setLength(0);
			sb.append("Numeric(Factors(");
			sb.append(funVarStr[0]); // function expression
			sb.append("))");
			// cached evaluation of MPReduce as we are only using variable
			// values
			String listOut = kernel.evaluateCachedGeoGebraCAS(sb.toString(),
					arbconst);

			if (listOut == null || listOut.length() == 0) {
				g.setUndefined();
			} else {
				// read result back into list
				g.set(kernel.getAlgebraProcessor().evaluateToList(listOut));
				// force first element in each row to be a function, even if
				// constant
				for (int i = 0; i < g.size() && g.get(i) instanceof GeoList; i++) {
					GeoList factor = (GeoList) g.get(i);
					if (factor.get(0) instanceof GeoNumeric) {
						GeoElement constant = factor.get(0);
						GeoElement exponent = factor.get(1);
						factor.remove(1);
						factor.remove(0);
						GeoFunction fn = new GeoFunction(cons);
						fn.set(constant);
						factor.add(fn);
						factor.add(exponent);
					}
				}
			}
		} catch (Throwable th) {
			g.setUndefined();
		}
	}

	// TODO Consider locusequability

}
