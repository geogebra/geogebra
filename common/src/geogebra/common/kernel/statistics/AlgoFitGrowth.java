package geogebra.common.kernel.statistics;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.plugin.Operation;

/**
 * Fits an a*b^x to a list of points. Needed for pupils who don't know about e,
 * but in their curriculum are doing mathematical models with growth
 * (exponential) functions.
 * 
 * @author Hans-Petter Ulven
 * @version 2010-02-2010
 */

public class AlgoFitGrowth extends AlgoElement {

	private GeoList geolist; // input
	private GeoFunction geofunction; // output
	private RegressionMath regMath;

	public AlgoFitGrowth(Construction cons, String label, GeoList geolist) {
		this(cons, geolist);
		geofunction.setLabel(label);
	}// Constructor

	public AlgoFitGrowth(Construction cons, GeoList geolist) {
		super(cons);

		regMath = new RegressionMath();

		this.geolist = geolist;
		geofunction = new GeoFunction(cons);
		setInputOutput();
		compute();
	}// Constructor

	@Override
	public Algos getClassName() {
		return Algos.AlgoFitGrowth;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geolist;

		setOnlyOutput(geofunction);
		setDependencies();
	}// setInputOutput()

	public GeoFunction getFitGrowth() {
		return geofunction;
	}

	@Override
	public final void compute() {
		int size = geolist.size();
		boolean regok = true;
		double a, b;
		if (!geolist.isDefined() || (size < 2)) {
			geofunction.setUndefined();
			return;
		}

		regok = regMath.doExp(geolist);
		if (regok) {
			a = regMath.getP1();
			b = regMath.getP2();
			b = Math.exp(b);
			MyDouble A = new MyDouble(kernel, a);
			MyDouble B = new MyDouble(kernel, b);
			FunctionVariable X = new FunctionVariable(kernel);
			ExpressionValue expr = new ExpressionNode(kernel, B,
					Operation.POWER, X);
			ExpressionNode node = new ExpressionNode(kernel, A,
					Operation.MULTIPLY, expr);
			Function f = new Function(node, X);
			geofunction.setFunction(f);
			geofunction.setDefined(true);
		} else {
			geofunction.setUndefined();
			return;
		}// if error in regression
	}// compute()

	// TODO Consider locusequability

}// class AlgoFitGrowth