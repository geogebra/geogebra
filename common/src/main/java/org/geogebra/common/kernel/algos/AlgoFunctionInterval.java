/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * Function limited to interval [a, b]
 */
public class AlgoFunctionInterval extends AlgoElement {

	private GeoFunction f; // input
	private NumberValue a, b; // input
	private GeoElement ageo, bgeo;
	private GeoFunction g; // output g

	/** Creates new AlgoDependentFunction */
	public AlgoFunctionInterval(Construction cons, String label, GeoFunction f,
			GeoNumberValue a, GeoNumberValue b) {
		this(cons, f, a, b);
		g.setLabel(label);
	}

	public AlgoFunctionInterval(Construction cons, GeoFunction f,
			GeoNumberValue a, GeoNumberValue b) {
		super(cons);
		this.f = f;
		this.a = a;
		this.b = b;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();

		// g = new GeoFunction(cons); // output
		// g = new GeoFunction(cons); // output

		g = (GeoFunction) f.copyInternal(cons);

		// buildFunction();
		// g = initHelperAlgorithm();

		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Function;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = f;
		input[1] = ageo;
		input[2] = bgeo;

		super.setOutputLength(1);
		super.setOutput(0, g);
		setDependencies(); // done by AlgoElement
	}

	public GeoFunction getFunction() {
		return g;
	}

	@Override
	public final void compute() {
		if (!(f.isDefined() && ageo.isDefined() && bgeo.isDefined()))
			g.setUndefined();

		// check if f has changed
		if (!hasEqualExpressions(f)) {
			g.set(f);
		}

		double ad = a.getDouble();
		double bd = b.getDouble();
		if (ad > bd) {
			g.setUndefined();
		} else {
			boolean defined = g.setInterval(ad, bd);
			g.setDefined(defined);
		}

	}

	private boolean hasEqualExpressions(GeoFunction f) {
		boolean equal;

		ExpressionNode en = f.getFunctionExpression();

		equal = exp == en;
		exp = en;

		return equal;
	}

	private ExpressionNode exp; // current expression of f
												// (needed to notice change of
												// f)

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("FunctionAonIntervalBC", f.getLabel(tpl),
				ageo.getLabel(tpl), bgeo.getLabel(tpl));

	}

	

}
