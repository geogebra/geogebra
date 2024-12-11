/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * Semicircle defined by two points A and B (start and end point).
 */
public class AlgoSemicircle extends AlgoElement
		implements SymbolicParametersBotanaAlgo {

	private GeoPoint A; // input
	private GeoPoint B; // input
	private GeoConicPart conicPart; // output

	private GeoPoint M; // midpoint of AB
	private GeoConic conic;

	private PPolynomial[] botanaPolynomials;
	private PVariable[] botanaVars;

	/**
	 * Creates new semicircle algorithm
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for the semicircle
	 * @param A
	 *            first endpoint
	 * @param B
	 *            second endpoint
	 */
	public AlgoSemicircle(Construction cons, String label, GeoPoint A,
			GeoPoint B) {
		this(cons, A, B);
		conicPart.setLabel(label);
	}

	/**
	 * Creates new unlabeled semicircle algorithm
	 * 
	 * @param cons
	 *            construction
	 * @param A
	 *            first endpoint
	 * @param B
	 *            second endpoint
	 */
	public AlgoSemicircle(Construction cons, GeoPoint A, GeoPoint B) {
		super(cons);
		this.A = A;
		this.B = B;

		// helper algo to get midpoint
		AlgoMidpoint algom = new AlgoMidpoint(cons, A, B);
		cons.removeFromConstructionList(algom);
		M = algom.getPoint();

		// helper algo to get circle
		AlgoCircleTwoPoints algo = new AlgoCircleTwoPoints(cons, M, B);
		cons.removeFromConstructionList(algo);
		conic = algo.getCircle();

		conicPart = new GeoConicPart(cons, GeoConicNDConstants.CONIC_PART_ARC);
		conicPart.addPointOnConic(A);
		conicPart.addPointOnConic(B);

		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Semicircle;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_SEMICIRCLE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = A;
		input[1] = B;

		setOnlyOutput(conicPart);

		setDependencies();
	}

	/**
	 * Returns the semicercle
	 * 
	 * @return the semicircle
	 */
	public GeoConicPart getSemicircle() {
		return conicPart;
	}

	/**
	 * Returns first endpoint
	 * 
	 * @return first endpoint
	 */
	public GeoPoint getA() {
		return A;
	}

	/**
	 * Returns second endpoint
	 * 
	 * @return second endpoint
	 */
	public GeoPoint getB() {
		return B;
	}

	/**
	 * Returns the full circle
	 * 
	 * @return full circle
	 */
	public GeoConic getConic() {
		return conic;
	}

	@Override
	public void compute() {
		if (!conic.isDefined()) {
			conicPart.setUndefined();
			return;
		}

		double alpha = Math.atan2(B.inhomY - A.inhomY, B.inhomX - A.inhomX);
		double beta = alpha + Math.PI;

		conicPart.set(conic);
		conicPart.setParameters(alpha, beta, true);
	}

	// ///////////////////////////////
	// TRICKS FOR XOY PLANE
	// ///////////////////////////////

	@Override
	protected int getInputLengthForXML() {
		return getInputLengthForXMLMayNeedXOYPlane();
	}

	@Override
	protected int getInputLengthForCommandDescription() {
		return getInputLengthForCommandDescriptionMayNeedXOYPlane();
	}

	@Override
	public GeoElementND getInput(int i) {
		return getInputMaybeXOYPlane(i);
	}

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("SemicircleThroughAandB",
				"Semi-circle through %0 and %1", A.getLabel(tpl),
				B.getLabel(tpl));
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		return botanaVars;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (botanaVars == null) {
			PVariable[] circle1vars;
			PVariable[] centerVars = new PVariable[2];

			circle1vars = ((SymbolicParametersBotanaAlgo) A).getBotanaVars(A);
			centerVars[0] = new PVariable(kernel);
			centerVars[1] = new PVariable(kernel);

			botanaVars = new PVariable[4];
			// Center:
			botanaVars[0] = centerVars[0];
			botanaVars[1] = centerVars[1];
			// Point on the circle:
			botanaVars[2] = circle1vars[0];
			botanaVars[3] = circle1vars[1];

			botanaPolynomials = SymbolicParameters.botanaPolynomialsMidpoint(A,
					B, centerVars);
			return botanaPolynomials;
		}

		// this should not happen:
		throw new NoSymbolicParametersException();
	}
}
