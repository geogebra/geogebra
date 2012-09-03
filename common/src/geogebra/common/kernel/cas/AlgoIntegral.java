/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.cas;

import geogebra.common.kernel.AlgoCasBase;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * Integral of a function
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntegral extends AlgoCasBase {

	private GeoNumeric var;

	public AlgoIntegral(Construction cons, String label,
			CasEvaluableFunction f, GeoNumeric var) {
		this(cons, f, var);
		g.toGeoElement().setLabel(label);
	}

	public AlgoIntegral(Construction cons, CasEvaluableFunction f,
			GeoNumeric var) {
		super(cons, f);
		this.var = var;

		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoIntegral;
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

		// var.getLabel() can return a number in wrong alphabet (need ASCII)
		
		// get variable string with tmp prefix,
		// e.g. "x" becomes "ggbtmpvarx" here
		String varStr = var != null ? var.getLabel(tpl) : f.getVarString(tpl);
		
		sbAE.setLength(0);
		sbAE.append("Integral(%");
		sbAE.append(",");
		sbAE.append(varStr);
		sbAE.append(")");

		// find symbolic derivative of f
		g.setUsingCasCommand(sbAE.toString(), f, true,arbconst);
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
			sb.append(app.getPlain("IntegralOfA", f.toGeoElement().getLabel(tpl)));
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

	// TODO Consider locusequability

}
