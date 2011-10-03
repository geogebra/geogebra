package geogebra.kernel.statistics;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoList;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;

/**
 * Fits an a*b^x to a list of points.
 * Needed for pupils who don't know about e, but in their curriculum are doing
 * mathematical models with growth (exponential) functions.
 * 
 * @author Hans-Petter Ulven
 * @version 2010-02-2010
 */

public class AlgoFitGrowth extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geolist; // input
	private GeoFunction geofunction; // output
	private RegressionMath regMath;

	public AlgoFitGrowth(Construction cons, String label, GeoList geolist) {
		super(cons);

		regMath = kernel.getRegressionMath();

		this.geolist = geolist;
		geofunction = new GeoFunction(cons);
		setInputOutput();
		compute();
		geofunction.setLabel(label);
	}// Constructor

	public String getClassName() {
		return "AlgoFitGrowth";
	}

	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geolist;
		output = new GeoElement[1];
		output[0] = geofunction;
		setDependencies();
	}// setInputOutput()

	public GeoFunction getFitGrowth() {
		return geofunction;
	}

	protected final void compute() {
		int size = geolist.size();
		boolean regok = true;
		double a, b,g;
		if (!geolist.isDefined() || (size < 2)) { 
			geofunction.setUndefined();
			return;
		} else {
			regok = regMath.doExp(geolist);
			if (regok) {
				a = regMath.getP1();
				b = regMath.getP2();
				b=Math.exp(b);
				MyDouble A = new MyDouble(kernel, a);
				MyDouble B = new MyDouble(kernel, b);
				FunctionVariable X = new FunctionVariable(kernel);
				ExpressionValue expr = new ExpressionNode(kernel, B,ExpressionNode.POWER, X);
				ExpressionNode node = new ExpressionNode(kernel, A,ExpressionNode.MULTIPLY, expr);
				Function f = new Function(node, X);
				geofunction.setFunction(f);
				geofunction.setDefined(true);
			} else {
				geofunction.setUndefined();
				return;
			}// if error in regression
		}// if error in parameters
	}// compute()

}// class AlgoFitGrowth