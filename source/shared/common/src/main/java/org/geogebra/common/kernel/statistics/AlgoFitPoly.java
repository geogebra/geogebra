/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoPolynomialFromCoordinates;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.jspecify.annotations.Nullable;

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
public class AlgoFitPoly extends AlgoElement implements FitAlgo {

	private GeoList geolist; // input
	private GeoNumberValue degree; // input
	private GeoFunction geofunction; // output
	private final RegressionMath regMath;
	private double @Nullable [] coeff;

	/**
	 * @param cons
	 *            construction
	 * @param geolist
	 *            points
	 * @param degree
	 *            degree
	 */
	public AlgoFitPoly(Construction cons, GeoList geolist, GeoNumberValue degree) {
		super(cons);
		regMath = new RegressionMath();
		this.geolist = geolist;
		this.degree = degree;
		geofunction = new GeoFunction(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.FitPoly;
	}

	@Override
	protected void setInputOutput() {
		if (degree == null) {
			input = new GeoElement[] {geolist};
		} else {
			input = new GeoElement[] {geolist, degree.toGeoElement()};
		}
		setOnlyOutput(geofunction);
		setDependencies();
	}

	/**
	 * @return fit polynomial
	 */
	public GeoFunction getFitPoly() {
		return geofunction;
	}

	@Override
	public final void compute() {
		coeff = null;
		int size = geolist.size();
		int par;
		par = degree == null ? size - 1 : (int) Math.round(degree.getDouble());
		if (!geolist.isDefined() || (size < 2) || (par < 0) || (par >= size)) {
			geofunction.setUndefined();
			return;
		}
		if (par == size - 1) {
			coeff = new double[size];
			AlgoPolynomialFromCoordinates.setFromPoints(geofunction, geolist, coeff);
			if (!geofunction.isDefined()) {
				coeff = null;
			}
			return;
		}
		if (par >= 300) {
			geofunction.setUndefined();
			return;
		}
		double[] fittedCoefficients = new double[par + 1];
		Function fittedFunction = regMath.fitPolynomial(geolist, par, fittedCoefficients);
		if (fittedFunction == null) {
			geofunction.setUndefined();
			return;
		}
		coeff = fittedCoefficients;
		geofunction.setFunction(fittedFunction);
		geofunction.setDefined(true);
	}

	@Override
	public double @Nullable [] getCoeffs() {
		return coeff;
	}
}
