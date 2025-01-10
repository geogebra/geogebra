/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoJoinPointsSegment
 *
 * Created on 21. August 2003
 */

package org.geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 *
 * @author Markus
 */
public class AlgoJoinPointsSegment extends AlgoElement
		implements AlgoJoinPointsSegmentInterface, SymbolicParametersBotanaAlgo,
		SymbolicParametersAlgo {

	private GeoPoint P; // input
	private GeoPoint Q; // input
	private GeoSegment s; // output: GeoSegment subclasses GeoLine

	private GeoPolygon poly; // for polygons

	private PVariable[] botanaVars;
	private PPolynomial[] polynomials;

	/**
	 * Creates new AlgoJoinPoints
	 *
	 * @param cons
	 *            construction
	 * @param P
	 *            start point
	 * @param Q
	 *            end point
	 */
	public AlgoJoinPointsSegment(Construction cons, GeoPoint P,
			GeoPoint Q) {
		this(cons, P, Q, null, true);
	}

	/**
	 * @param cons
	 *            construction
	 * @param P
	 *            start point
	 * @param Q
	 *            end point
	 * @param poly
	 *            parent polygon
	 * @param addToConstructionList
	 *            add to construction?
	 */
	public AlgoJoinPointsSegment(Construction cons, GeoPoint P, GeoPoint Q,
			GeoPolygon poly, boolean addToConstructionList) {
		super(cons, addToConstructionList);

		// make sure that this helper algorithm is updated right after its
		// parent polygon
		if (poly != null) {
			setUpdateAfterAlgo(poly.getParentAlgorithm());
			setProtectedInput(true);
		}

		this.poly = poly;
		this.P = P;
		this.Q = Q;

		s = new GeoSegment(cons, P, Q);
		s.setFromMeta(poly);
		setInputOutput(); // for AlgoElement

		// compute line through P, Q
		compute();

		setIncidence();

		// note: GeoSegment's equation form is initialized from construction defaults
		EquationBehaviour equationBehaviour = kernel.getEquationBehaviour();
		if (equationBehaviour != null) {
			s.setEquationForm(equationBehaviour.getLineCommandEquationForm());
		}
	}

	private void setIncidence() {
		P.addIncidence(s, true);
		Q.addIncidence(s, true);
	}

	@Override
	public Commands getClassName() {
		return Commands.Segment;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_SEGMENT;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		GeoElement[] efficientInput = new GeoElement[2];
		efficientInput[0] = P;
		efficientInput[1] = Q;

		if (poly == null) {
			input = efficientInput;
		} else {
			input = new GeoElement[3];
			input[0] = P;
			input[1] = Q;
			input[2] = poly;
			// input = new GeoElement[2];
			// input[0] = P;
			// input[1] = Q;
		}

		setOnlyOutput(s);

		// setDependencies();
		setEfficientDependencies(input, efficientInput);
	}

	@Override
	public void modifyInputPoints(GeoPointND A, GeoPointND B) {

		// same points : return
		if ((P == A && Q == B) || (Q == A && P == B)) {
			return;
		}

		for (int i = 0; i < input.length; i++) {
			input[i].removeAlgorithm(this);
		}

		P = (GeoPoint) A;
		Q = (GeoPoint) B;
		s.setPoints(P, Q);
		setInputOutput();

		compute();
	}

	/**
	 * @return resulting segment
	 */
	public GeoSegment getSegment() {
		return s;
	}

	/**
	 * @return start point
	 */
	public GeoPoint getP() {
		return P;
	}

	/**
	 * @return end point
	 */
	public GeoPoint getQ() {
		return Q;
	}

	@Override
	public GeoPolygon getPoly() {
		return poly;
	}

	// calc the line g through P and Q
	@Override
	public final void compute() {
		// g = P v Q <=> g_n : n = P x Q
		// g = cross(P, Q)
		GeoVec3D.lineThroughPoints(P, Q, s);
		s.calcLength();
	}

	@Override
	public void remove() {
		if (removed) {
			return;
		}
		super.remove();
		if (poly != null) {
			poly.remove();
		}
	}

	/**
	 * Only removes this segment and does not remove parent polygon (if poly !=
	 * null)
	 */
	public void removeSegmentOnly() {
		super.remove();
	}

	@Override
	public int getConstructionIndex() {
		if (poly != null) {
			return poly.getConstructionIndex();
		}
		return super.getConstructionIndex();
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("SegmentAB", "Segment %0, %1",
					P.getLabel(tpl), Q.getLabel(tpl));
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (botanaVars == null) {
			botanaVars = SymbolicParameters.addBotanaVarsJoinPoints(input);
		}
		return botanaVars;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		// It's OK, polynomials for lines/segments are only created when a third
		// point is lying on them, too:
		return null;
	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {
		if (P != null && Q != null) {
			P.getFreeVariables(variables);
			Q.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();

	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {
		if (P != null && Q != null) {
			int[] degree1 = P.getDegrees(a);
			int[] degree2 = Q.getDegrees(a);
			return SymbolicParameters.crossDegree(degree1, degree2);
		}
		throw new NoSymbolicParametersException();

	}

	@Override
	public BigInteger[] getExactCoordinates(
			final HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (P != null && Q != null) {
			BigInteger[] coords1 = P.getExactCoordinates(values);
			BigInteger[] coords2 = Q.getExactCoordinates(values);
			if (coords1 != null && coords2 != null) {
				return SymbolicParameters.crossProduct(coords1, coords2);
			}
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if (P != null && Q != null) {
			PPolynomial[] coords1 = P.getPolynomials();
			PPolynomial[] coords2 = Q.getPolynomials();
			if (coords1 != null && coords2 != null) {
				polynomials = PPolynomial.crossProduct(coords1, coords2);

				return polynomials;
			}
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public ArrayList<GeoElementND> getFreeInputPoints() {
		if (poly == null
				|| !(poly.getParentAlgorithm() instanceof AlgoPolygonRegular)) {
			return super.getFreeInputPoints();
		}

		return poly.getParentAlgorithm().getFreeInputPoints();
	}

	@Override
	public boolean hasOnlyFreeInputPoints(EuclidianViewInterfaceSlim view) {
		return view.getFreeInputPoints(this).size() == 2;
	}

}
