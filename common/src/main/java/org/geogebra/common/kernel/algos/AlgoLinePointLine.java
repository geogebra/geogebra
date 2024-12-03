/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoLinePointLine.java
 *
 * line through P parallel to l
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
import org.geogebra.common.kernel.geos.Lineable2D;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 *
 * @author Markus
 */
public class AlgoLinePointLine extends AlgoElement
		implements SymbolicParametersAlgo, SymbolicParametersBotanaAlgo {

	private GeoPoint P; // input
	private Lineable2D l; // input
	private GeoLine g; // output
	private PPolynomial[] polynomials;
	private PPolynomial[] botanaPolynomials;
	private PVariable[] botanaVars;

	/** Creates new AlgoLinePointLine */
	public AlgoLinePointLine(Construction cons, String label, GeoPoint P,
			Lineable2D l) {
		super(cons);
		this.P = P;
		this.l = l;
		g = new GeoLine(cons);
		g.setStartPoint(P);
		setInputOutput(); // for AlgoElement

		// compute line
		compute();

		addIncidence();

		// note: GeoLine's equation form is initialized from construction defaults
		EquationBehaviour equationBehaviour = kernel.getEquationBehaviour();
		if (equationBehaviour != null) {
			g.setEquationForm(equationBehaviour.getLineCommandEquationForm());
		}

		g.setLabel(label);
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
		return Commands.Line;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_PARALLEL;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = P;
		input[1] = l.toGeoElement();

		setOnlyOutput(g);
		setDependencies(); // done by AlgoElement
	}

	public GeoLine getLine() {
		return g;
	}

	// Made public for LocusEqu.
	public GeoPoint getP() {
		return P;
	}

	// Made public for LocusEqu.
	public GeoLine getl() {
		return l instanceof GeoLine ? (GeoLine) l : null;
	}

	// calc the line g through P and parallel to l
	@Override
	public final void compute() {
		// homogenous:
		GeoVec3D.cross(P, l.getY(), -l.getX(), 0.0, g);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("LineThroughAParallelToB",
				"Line through %0 parallel to %1", P.getLabel(tpl),
				((GeoElement) l).getLabel(tpl));

	}

	// Simon Weitzhofer 2012-05-07

	@Override
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {

		if (P != null && l instanceof GeoLine) {
			P.getFreeVariables(variables);
			((GeoLine) l).getFreeVariables(variables);
			return;
		}

		throw new NoSymbolicParametersException();
	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {

		if (P != null && l instanceof GeoLine) {
			int[] degreeP = P.getDegrees(a);
			int[] degreeL = ((GeoLine) l).getDegrees(a);
			int[] degrees = new int[3];
			degrees[0] = degreeL[0] + degreeP[2];
			degrees[1] = degreeL[1] + degreeP[2];
			degrees[2] = Math.max(degreeL[0] + degreeP[0],
					degreeL[1] + degreeP[1]);
			return degrees;
		}

		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {

		if (P != null && l instanceof GeoLine) {
			BigInteger[] coordsP = P.getExactCoordinates(values);
			BigInteger[] coordsL = ((GeoLine) l).getExactCoordinates(values);
			BigInteger[] coords = new BigInteger[3];
			coords[0] = coordsL[0].multiply(coordsP[2]);
			coords[1] = coordsL[1].multiply(coordsP[2]);
			coords[2] = coordsL[0].multiply(coordsP[0])
					.add(coordsL[1].multiply(coordsP[1])).negate();
			return coords;
		}

		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {

		if (polynomials != null) {
			return polynomials;
		}

		if (P != null && l instanceof GeoLine) {
			PPolynomial[] coordsP = P.getPolynomials();
			PPolynomial[] coordsl = ((GeoLine) l).getPolynomials();
			polynomials = new PPolynomial[3];
			polynomials[0] = coordsl[0].multiply(coordsP[2]);
			polynomials[1] = coordsl[1].multiply(coordsP[2]);
			polynomials[2] = coordsl[0].multiply(coordsP[0])
					.add(coordsl[1].multiply(coordsP[1])).negate();
			return polynomials;
		}

		throw new NoSymbolicParametersException();
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
		if (P != null && l instanceof GeoLine) {
			PVariable[] vP = P.getBotanaVars(P); // c1,c2
			PPolynomial c1 = new PPolynomial(vP[0]);
			PPolynomial c2 = new PPolynomial(vP[1]);
			PVariable[] vL = ((GeoLine) l).getBotanaVars((GeoLine) l); // a1,a2,b1,b2
			PPolynomial a1 = new PPolynomial(vL[0]);
			PPolynomial a2 = new PPolynomial(vL[1]);
			PPolynomial b1 = new PPolynomial(vL[2]);
			PPolynomial b2 = new PPolynomial(vL[3]);

			if (botanaVars == null) {
				botanaVars = new PVariable[4]; // storing 2 new variables, plus
												// the coordinates of P
				botanaVars[0] = new PVariable(kernel); // d1
				botanaVars[1] = new PVariable(kernel); // d2
				botanaVars[2] = vP[0];
				botanaVars[3] = vP[1];
			}
			PPolynomial d1 = new PPolynomial(botanaVars[0]);
			PPolynomial d2 = new PPolynomial(botanaVars[1]);

			botanaPolynomials = new PPolynomial[2];
			// d1=c1+(b1-a1), d2=c2+(b2-a2) => d1-c1-b1+a1, d2-c2-b2+a2
			botanaPolynomials[0] = ((d1.subtract(c1)).subtract(b1)).add(a1);
			botanaPolynomials[1] = ((d2.subtract(c2)).subtract(b2)).add(a2);

			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}

}
