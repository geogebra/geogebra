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
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.util.StringUtil;

/**
 * Try to expand the given function
 * 
 * @author Michael Borcherds
 */
public class AlgoDegree extends AlgoElement {

	private GeoFunction f; // input
	private GeoNumeric num; // output

	private StringBuilder sb = new StringBuilder();

	public AlgoDegree(Construction cons, String label, GeoFunction f) {
		super(cons);
		this.f = f;

		num = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement
		compute();
		num.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoDegree;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = f;

		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return num;
	}
	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);
	@Override
	public final void compute() {
		if (!f.isDefined()) {
			num.setUndefined();
			return;
		}

		// get function and function variable string using temp variable
		// prefixes,
		// e.g. f(x) = a x^2 returns {"ggbtmpvara ggbtmpvarx^2", "ggbtmpvarx"}
		String[] funVarStr = f.getTempVarCASString(false);

		sb.setLength(0);
		sb.append("Degree(");
		sb.append(funVarStr[0]); // function expression
		sb.append(",");
		sb.append(funVarStr[1]); // function variable
		sb.append(")");
		String functionOut;
		try {
			functionOut = kernel.evaluateCachedGeoGebraCAS(sb.toString(),arbconst);
			num.setValue(StringUtil.parseDouble(functionOut));
		} catch (Throwable e) {
			System.err.println("AlgoDegree: " + e.getMessage());
			num.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getCommandDescription(tpl);
	}

	// TODO Consider locusequability

}
