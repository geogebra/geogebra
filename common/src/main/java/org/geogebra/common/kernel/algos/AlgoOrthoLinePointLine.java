/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoOrthoLinePointLine.java
 *
 * line through P orthogonal to l
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.LocusEquation;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoOrthoLinePointLine extends AlgoElement implements
		SymbolicParametersAlgo, SymbolicParametersBotanaAlgo {

	protected GeoPoint P; // input
	protected GeoLine l; // input
	private GeoLine g; // output
	private Polynomial[] polynomials;

	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;

	/**
	 * Creates new AlgoOrthoLinePointLine
	 * 
	 * @param cons
	 * @param label
	 * @param P
	 * @param l
	 */
	public AlgoOrthoLinePointLine(Construction cons, String label, GeoPoint P,
			GeoLine l) {
		super(cons);
		this.P = P;
		this.l = l;
		g = new GeoLine(cons);
		g.setStartPoint(P);
		setInputOutput(); // for AlgoElement

		// compute line
		compute();

		g.setLabel(label);
		addIncidence();
	}

	/**
	 * @author Tam
	 * 
	 *         for special cases of e.g. AlgoIntersectLineConic
	 */
	private void addIncidence() {
		P.addIncidence(g, true);
	}

	@Override
	public Commands getClassName() {
		return Commands.OrthogonalLine;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_ORTHOGONAL;
	}

	/**
	 * set the inputs
	 */
	protected void setInput() {
		input = new GeoElement[2];
		input[0] = P;
		input[1] = l;
	}

	// for AlgoElement
	@Override
	public void setInputOutput() {

		setInput();
		setOutputLength(1);
		setOutput(0, g);
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
	public GeoLine getl() {
		return l;
	}

	// calc the line g through P and normal to l
	@Override
	public final void compute() {
		GeoVec3D.cross(P, l.x, l.y, 0.0, g);
	}

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (P != null && l != null) {
			P.getFreeVariables(variables);
			l.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}

	public int[] getDegrees() throws NoSymbolicParametersException {
		if (P != null && l != null) {
			int[] degreeP = P.getDegrees();
			int[] degreeL = l.getDegrees();

			int[] result = new int[3];
			result[0] = degreeL[1] + degreeP[2];
			result[1] = degreeL[0] + degreeP[2];
			result[2] = Math.max(degreeL[0] + degreeP[1], degreeL[1]
					+ degreeP[0]);
			return result;
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (P != null && l != null) {
			BigInteger[] pP = P.getExactCoordinates(values);
			BigInteger[] pL = l.getExactCoordinates(values);
			BigInteger[] coords = new BigInteger[3];
			coords[0] = pL[1].multiply(pP[2]).negate();
			coords[1] = pL[0].multiply(pP[2]);
			coords[2] = pL[0].multiply(pP[1]).negate()
					.add(pL[1].multiply(pP[0]));
			return coords;
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if (P != null && l != null) {
			Polynomial[] pP = P.getPolynomials();
			Polynomial[] pL = l.getPolynomials();
			polynomials = new Polynomial[3];
			polynomials[0] = pL[1].multiply(pP[2]).negate();
			polynomials[1] = pL[0].multiply(pP[2]);
			polynomials[2] = pL[0].multiply(pP[1]).negate()
					.add(pL[1].multiply(pP[0]));
			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}
		if (P != null && l != null) {
			Variable[] vP = P.getBotanaVars(P);
			Variable[] vL = l.getBotanaVars(l);

			if (botanaVars == null) {
				botanaVars = new Variable[4]; // storing 2 new variables, plus
												// the coordinates of P
				botanaVars[0] = new Variable();
				botanaVars[1] = new Variable();
				botanaVars[2] = vP[0];
				botanaVars[3] = vP[1];
			}

			botanaPolynomials = new Polynomial[2];

			// We describe the new point simply with rotation of l=:AB around P
			// by 90 degrees.
			// I.e., b1-a1=n2-p2, b2-a2=p1-n1 => b1-a1+p2-n2=0, p1-b2+a2-n1=0
			Polynomial p1 = new Polynomial(vP[0]);
			Polynomial p2 = new Polynomial(vP[1]);
			Polynomial a1 = new Polynomial(vL[0]);
			Polynomial a2 = new Polynomial(vL[1]);
			Polynomial b1 = new Polynomial(vL[2]);
			Polynomial b2 = new Polynomial(vL[3]);
			Polynomial n1 = new Polynomial(botanaVars[0]);
			Polynomial n2 = new Polynomial(botanaVars[1]);
			botanaPolynomials[0] = b1.subtract(a1).add(p2).subtract(n2);
			botanaPolynomials[1] = p1.subtract(b2).add(a2).subtract(n1);

			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {
		return LocusEquation.eqnOrthoLinePointLine(geo, this, scope);
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
	public GeoElement getInput(int i) {
		return getInputMaybeXOYPlane(i);
	}

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlain("LineThroughAPerpendicularToB",
				P.getLabel(tpl), l.getLabel(tpl));
	}

}
