package org.geogebra.common.kernel.statistics;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.plugin.Operation;

/**
 * Fits a+bln(x) to a list of points. Adapted from AlgoFitLine and
 * AlgoPolynomialFromCoordinates (Borcherds)
 * 
 * @author Hans-Petter Ulven
 * @version 24.04.08
 */
public class AlgoFitLog extends AlgoElement implements FitAlgo {

	private GeoList geolist; // input
	private GeoFunction geofunction; // output
	private final RegressionMath regMath;

	public AlgoFitLog(Construction cons, String label, GeoList geolist) {
		this(cons, geolist);
		geofunction.setLabel(label);
		
		
	}

	public AlgoFitLog(Construction cons, GeoList geolist) {
		super(cons);		
		this.geolist = geolist;
		geofunction = new GeoFunction(cons);
		regMath = new RegressionMath();
		setInputOutput();
		compute();
	}// Constructor

	@Override
	public Commands getClassName() {
		return Commands.FitLog;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geolist;
		setOnlyOutput(geofunction);
		setDependencies();
	}// setInputOutput()

	public GeoFunction getFitLog() {
		return geofunction;
	}

	@Override
	public final void compute() {
		int size = geolist.size();
		boolean regok = true;
		double a, b;
		if (!geolist.isDefined() || (size < 2)) { // 24.04.08: 2
			geofunction.setUndefined();
			return;
		}
		regok = regMath.doLog(geolist);
		if (regok) {
			a = regMath.getP1();
			b = regMath.getP2();
			MyDouble A = new MyDouble(kernel, a);
			MyDouble B = new MyDouble(kernel, b);
			FunctionVariable X = new FunctionVariable(kernel);
			ExpressionValue expr = new ExpressionNode(kernel, X, Operation.LOG,
					X);
			expr = new ExpressionNode(kernel, B, Operation.MULTIPLY, expr);
			ExpressionNode node = new ExpressionNode(kernel, A, Operation.PLUS,
					expr);
			Function f = new Function(node, X);
			geofunction.setFunction(f);
			geofunction.setDefined(true);
		} else {
			geofunction.setUndefined();
			return;
		}// if error in regression
			// if error in parameters
	}
	
	@Override
	public double[] getCoeffs() {
		double[] ret = {regMath.getP1(), regMath.getP2()};
		return ret;
	}

	

}// class AlgoFitLog