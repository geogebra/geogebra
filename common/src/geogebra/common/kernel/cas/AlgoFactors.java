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
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;

/**
 * Try to expand the given function
 * 
 * @author Michael Borcherds
 */
public class AlgoFactors extends AlgoElement implements UsesCAS {

	private GeoFunction f; // input
	private GeoList g; // output

	private StringBuilder sb = new StringBuilder();

	public AlgoFactors(Construction cons, String label, GeoFunction f) {
		super(cons);
		this.f = f;

		g = new GeoList(cons);
		setInputOutput(); // for AlgoElement
		compute();
		g.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoFactors;
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
			String listOut = kernel.evaluateCachedGeoGebraCAS(sb.toString(),arbconst);

			if (listOut == null || listOut.length() == 0) {
				g.setUndefined();
			} else {
				// read result back into list
				g.set(kernel.getAlgebraProcessor().evaluateToList(listOut));
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
