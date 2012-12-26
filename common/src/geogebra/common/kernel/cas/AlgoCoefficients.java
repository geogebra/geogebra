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
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.PolyFunction;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * returns coefficients of a Polynomial as a list
 * 
 * Uses CAS sometimes, eg Coefficients[x^n] so needs "implements UsesCAS"
 * 
 * @author Michael Borcherds
 */
public class AlgoCoefficients extends AlgoElement implements UsesCAS {

	private GeoFunction f; // input
	private GeoList g; // output

	/**
	 * @param cons construction
	 * @param label label for output
	 * @param f function
	 */
	public AlgoCoefficients(Construction cons, String label, GeoFunction f) {
		this(cons, f);
		g.setLabel(label);
	}
	
	/**
	 * @param cons construction
	 * @param f function
	 */
	public AlgoCoefficients(Construction cons, GeoFunction f) {
		super(cons);
		this.f = f;

		g = new GeoList(cons);
		setInputOutput(); // for AlgoElement
		compute();
	}

	 @Override
		public Commands getClassName() {
	        return Commands.Coefficients;
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
	 * @return resulting function
	 */
	public GeoList getResult() {
		return g;
	}

	@Override
	public final void compute() {
		if (!f.isDefined()) {
			g.setUndefined();
			return;
		}


		Function inFun = f.getFunction();

		// check if it's a polynomial & get coefficients
		PolyFunction poly = inFun.expandToPolyFunction(inFun.getExpression(), false,false);

		if (poly != null) {

			double[] coeffs = poly.getCoeffs();

			g.clear();
			g.setDefined(true);

			for (int i = coeffs.length - 1 ; i >=0 ; i--) {
				g.add(new GeoNumeric(cons, coeffs[i]));
			}

			return;
		}

		// not a polynomial
		g.setUndefined();
		return;


	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getCommandDescription(tpl);
	}

	// TODO Consider locusequability

}
