/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoJoinPoints.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.util.debug.Log;

/**
 *
 * @author Markus
 */
public class AlgoJoinPoints extends AlgoElement
		implements SymbolicParametersAlgo, SymbolicParametersBotanaAlgo {

	private GeoPoint P; // input
	private GeoPoint Q; // input
	private GeoLine g; // output
	private PPolynomial[] polynomials;
	private PVariable[] botanaVars;

	/** Creates new AlgoJoinPoints */
	public AlgoJoinPoints(Construction cons, String label, GeoPoint P,
			GeoPoint Q) {
		this(cons, P, Q);
		g.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param P
	 *            start point
	 * @param Q
	 *            end point
	 */
	public AlgoJoinPoints(Construction cons, GeoPoint P, GeoPoint Q) {
		super(cons);
		this.P = P;
		this.Q = Q;
		g = new GeoLine(cons);
		g.setStartPoint(P);
		g.setEndPoint(Q);

		setInputOutput(); // for AlgoElement

		// compute line through P, Q
		compute();

		addIncidence();

		// note: GeoLine's equation form is initialized from construction defaults
		EquationBehaviour equationBehaviour = kernel.getEquationBehaviour();
		if (equationBehaviour != null) {
			g.setEquationForm(equationBehaviour.getLineCommandEquationForm());
		}
	}

	/**
	 * @author Tam
	 * 
	 *         for special cases of e.g. AlgoIntersectLineConic
	 */
	private void addIncidence() {
		P.addIncidence(g, true);
		Q.addIncidence(g, true);
	}

	@Override
	public Commands getClassName() {
		return Commands.Line;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_JOIN;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = P;
		input[1] = Q;

		setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	public GeoLine getLine() {
		return g;
	}

	// Made public for LocusEqu
	public GeoPoint getP() {
		return P;
	}

	// Made public for LocusEqu
	public GeoPoint getQ() {
		return Q;
	}

	// calc the line g through P and Q
	@Override
	public final void compute() {
		// g = P v Q <=> g_n : n = P x Q
		// g = cross(P, Q)
		GeoVec3D.lineThroughPoints(P, Q, g);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("LineAB", "Line %0, %1",
					P.getLabel(tpl), Q.getLabel(tpl));

	}

	// Simon Weitzhofer 2012-04-03
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
				Log.debug("polys(" + g.getLabelSimple() + "): "
						+ polynomials[0].toString() + ","
						+ polynomials[1].toString() + ","
						+ polynomials[2].toString());

				return polynomials;
			}
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (botanaVars != null) {
			return botanaVars;
		}
		botanaVars = SymbolicParameters.addBotanaVarsJoinPoints(input);
		return botanaVars;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		// It's OK, polynomials for lines are only created when a third point is
		// lying on them, too:
		return null;
	}

}
