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
import org.geogebra.common.kernel.algos.DrawInformationAlgo;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Area between two functions (GeoFunction) f(x) and g(x) over an interval [a,
 * b]. The value equals Integral[f(x) - g(x), a, b] = Integral[f(x), a, b] -
 * Integral[g(x), a, b]
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntegralFunctions extends AlgoElement implements
		DrawInformationAlgo, AlgoIntegralDefiniteInterface {

	private GeoFunction f, g; // input
	private NumberValue a, b; // input
	private GeoBoolean evaluate; // input
	private GeoElement ageo, bgeo;
	private GeoNumeric n; // output n = integral(f(x) - g(x), x, a, b)

	private GeoNumeric intF, intG;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param f
	 *            upper function
	 * @param g
	 *            lower function
	 * @param a
	 *            left bound
	 * @param b
	 *            right bound
	 */
	public AlgoIntegralFunctions(Construction cons, String label,
			GeoFunction f, GeoFunction g, GeoNumberValue a, GeoNumberValue b) {
		this(cons, label, f, g, a, b, null);
		n.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param f
	 *            upper function
	 * @param g
	 *            lower function
	 * @param a
	 *            left bound
	 * @param b
	 *            right bound
	 * @param evaluate
	 *            true to evaluate, false = just shade
	 */
	public AlgoIntegralFunctions(Construction cons, String label,
			GeoFunction f, GeoFunction g, GeoNumberValue a, GeoNumberValue b,
			GeoBoolean evaluate) {
		super(cons);
		this.f = f;
		this.g = g;
		this.a = a;
		this.b = b;
		this.evaluate = evaluate;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();

		// helper algorithms for integral f and g
		AlgoIntegralDefinite algoInt = new AlgoIntegralDefinite(cons, f, a, b,
				evaluate);
		cons.removeFromConstructionList(algoInt);
		intF = algoInt.getIntegral();

		algoInt = new AlgoIntegralDefinite(cons, g, a, b, evaluate);
		cons.removeFromConstructionList(algoInt);
		intG = algoInt.getIntegral();

		// output: intF - intG
		n = new GeoNumeric(cons);

		setInputOutput(); // for AlgoElement
		compute();
		n.setDrawable(true);
		n.setLabel(label);
	}

	/**
	 * @param f
	 *            lower function
	 * @param g
	 *            upper function
	 * @param a
	 *            left bound
	 * @param b
	 *            right bound
	 * @param evaluate
	 *            true to evaluate, false = just shade
	 */
	public AlgoIntegralFunctions(GeoFunction f, GeoFunction g, MyDouble a,
			MyDouble b, GeoBoolean evaluate) {
		super(f.getConstruction(), false);
		this.f = f;
		this.g = g;
		this.a = a;
		this.b = b;
		this.evaluate = evaluate;
	}

	@Override
	public Commands getClassName() {
		return Commands.IntegralBetween;
	}

	public AlgoIntegralFunctions copy() {

		return new AlgoIntegralFunctions((GeoFunction) f.copy(),
				(GeoFunction) g.copy(), new MyDouble(kernel, a.getDouble()),
				new MyDouble(kernel, b.getDouble()), evaluate == null ? null
						: (GeoBoolean) evaluate.copy());

	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		if (evaluate == null) {
			input = new GeoElement[4];
			input[0] = f;
			input[1] = g;
			input[2] = ageo;
			input[3] = bgeo;
		} else {
			input = new GeoElement[5];
			input[0] = f;
			input[1] = g;
			input[2] = ageo;
			input[3] = bgeo;
			input[4] = evaluate;
		}
		setOutputLength(1);
		setOutput(0, n);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return integral result
	 */
	public GeoNumeric getIntegral() {
		return n;
	}

	/**
	 * @return upper function
	 */
	public GeoFunction getF() {
		return f;
	}

	/**
	 * @return lower function
	 */
	public GeoFunction getG() {
		return g;
	}

	/**
	 * @return left bound
	 */
	public NumberValue getA() {
		return a;
	}

	/**
	 * @return right bound
	 */
	public NumberValue getB() {
		return b;
	}

	@Override
	public final void compute() {
		if (!f.isDefined() || !g.isDefined() || !ageo.isDefined()
				|| !bgeo.isDefined()) {
			n.setUndefined();
			return;
		}

		// return if it should not be evaluated (i.e. is shade-only)
		if (evaluateOnly()) {
			n.setValue(Double.NaN);
			return;
		}

		// Integral[f(x) - g(x), a, b] = Integral[f(x), a, b] - Integral[g(x),
		// a, b]
		n.setValue(intF.getValue() - intG.getValue());
	}

	public boolean evaluateOnly() {
		return evaluate != null && !evaluate.getBoolean();
	}

	public void replaceChildrenByValues(GeoElement geo) {
		f.replaceChildrenByValues(geo);
		g.replaceChildrenByValues(geo);

	}
	

}
