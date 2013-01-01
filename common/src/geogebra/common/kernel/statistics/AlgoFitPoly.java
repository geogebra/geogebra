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
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;

/**
 * Fits a polynomial with given degree to list of points. Adapted from
 * AlgoFitLine and AlgoPolynomialFromCoordinates (Borcherds)
 * 
 * @author Hans-Petter Ulven
 * @version 24.04.08
 * 
 *          27.01.09: Extended FitPoly to more than 4th degree ToDo: Put in a
 *          max degree limit, after some testing...
 */
public class AlgoFitPoly extends AlgoElement {

	private GeoList geolist; // input
	private NumberValue degree; // input
	private GeoFunction geofunction; // output
	private GeoElement geodegree;

	public AlgoFitPoly(Construction cons, String label, GeoList geolist,
			NumberValue degree) {
		this(cons, geolist, degree);
		geofunction.setLabel(label);
	}// Constructor

	public AlgoFitPoly(Construction cons, GeoList geolist, NumberValue degree) {
		super(cons);
		this.geolist = geolist;
		this.degree = degree;
		geodegree = degree.toGeoElement();
		geofunction = new GeoFunction(cons);
		setInputOutput();
		compute();
	}// Constructor

	@Override
	public Commands getClassName() {
		return Commands.FitPoly;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = geolist;
		input[1] = geodegree;
		setOnlyOutput(geofunction);
		setDependencies();
	}// setInputOutput()

	public GeoFunction getFitPoly() {
		return geofunction;
	}

	@Override
	public final void compute() {
		int size = geolist.size();
		int par;
		boolean regok = true;
		double[] cof = null; // long ms=System.currentTimeMillis();
		par = (int) Math.round(degree.getDouble());
		if (!geolist.isDefined() || (size < 2) || (par >= size)) { // 24.04.08:
																	// size<2 or
																	// par>=size
			geofunction.setUndefined();
			return;
		}
		// if error in parameters :
		RegressionMath regMath = new RegressionMath();
		switch (par) {
		case RegressionMath.LINEAR: // 24.04.08: moved up linear case from
									// default
			regok = regMath.doLinear(geolist);
			if (regok) {
				cof = new double[2];
				cof[0] = regMath.getP1();
				cof[1] = regMath.getP2();
			}// else: ->
			break;
		case RegressionMath.QUAD:
			regok = regMath.doQuad(geolist);
			if (regok) {
				cof = new double[3];
				cof[0] = regMath.getP1();
				cof[1] = regMath.getP2();
				cof[2] = regMath.getP3();
			}// else: ->
			break;
		case RegressionMath.CUBIC:
			regok = regMath.doCubic(geolist);
			if (regok) {
				cof = new double[4];
				cof[0] = regMath.getP1();
				cof[1] = regMath.getP2();
				cof[2] = regMath.getP3();
				cof[3] = regMath.getP4();
			}// else: ->
			break;
		case RegressionMath.QUART:
			regok = regMath.doQuart(geolist);
			if (regok) {
				cof = new double[5];
				cof[0] = regMath.getP1();
				cof[1] = regMath.getP2();
				cof[2] = regMath.getP3();
				cof[3] = regMath.getP4();
				cof[4] = regMath.getP5();
			}// else: ->
			break;
		default:
			if ((par > 4) && (par < 300)) { // ToDo: test speed for max limit!
				regok = regMath.doPolyN(geolist, par);
				if (regok) {
					cof = new double[par + 1];
					cof = regMath.getPar();
				}// else: ->
			} else {
				regok = false; // 24.04.08: Only 1<=degree
			}// if
		}// switch
			// System.out.println("Used: "+(System.currentTimeMillis()-ms));
		if (!regok) {
			geofunction.setUndefined();
			return;
		}
		// if error in regression
		geofunction
				.setFunction(geogebra.common.kernel.algos.AlgoPolynomialFromCoordinates
						.buildPolyFunctionExpression(cons.getKernel(), cof));
		geofunction.setDefined(true);
	}// compute()

	// TODO Consider locusequability

}// class AlgoFitPoly

