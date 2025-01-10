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
import org.geogebra.common.kernel.prover.adapters.OrthoLinePointLineAdapter;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 *
 * @author Markus
 */
public class AlgoOrthoLinePointLine extends AlgoElement
		implements SymbolicParametersAlgo, SymbolicParametersBotanaAlgo {

	protected GeoPoint P; // input
	protected Lineable2D l; // input
	private final GeoLine g; // output
	private PPolynomial[] polynomials;
	private OrthoLinePointLineAdapter proverAdapter;
	private final double[] normal = new double[3];

	/**
	 * Creates new AlgoOrthoLinePointLine.
	 * 
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param P
	 *            point
	 * @param l
	 *            line result should be orthogonal to
	 */
	public AlgoOrthoLinePointLine(Construction cons, String label, GeoPoint P,
			Lineable2D l) {
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
		input[1] = l.toGeoElement();
	}

	// for AlgoElement
	@Override
	public void setInputOutput() {
		setInput();
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
	public GeoLine getl() {
		return l instanceof GeoLine ? (GeoLine) l : null;
	}

	// calc the line g through P and normal to l
	@Override
	public final void compute() {
		GeoVec3D.cross(P, l.getX(), l.getY(), 0.0, g);

		// avoid overflow to infinity from repeated use
		// (but keep nice answers for smaller numbers
		// eg PerpendicularLine[ (1,2), Segment[(1,6),(6,1)]
		// eg https://www.geogebra.org/m/wv8xnjnc
		g.getNormalizedCoefficients(normal, 10E6, 1E-2);
		g.x = normal[0];
		g.y = normal[1];
		g.z = normal[2];

	}

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

			int[] result = new int[3];
			result[0] = degreeL[1] + degreeP[2];
			result[1] = degreeL[0] + degreeP[2];
			result[2] = Math.max(degreeL[0] + degreeP[1],
					degreeL[1] + degreeP[0]);
			return result;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (P != null && l instanceof GeoLine) {
			BigInteger[] pP = P.getExactCoordinates(values);
			BigInteger[] pL = ((GeoLine) l).getExactCoordinates(values);
			BigInteger[] coords = new BigInteger[3];
			coords[0] = pL[1].multiply(pP[2]).negate();
			coords[1] = pL[0].multiply(pP[2]);
			coords[2] = pL[0].multiply(pP[1]).negate()
					.add(pL[1].multiply(pP[0]));
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
			PPolynomial[] pP = P.getPolynomials();
			PPolynomial[] pL = ((GeoLine) l).getPolynomials();
			polynomials = new PPolynomial[3];
			polynomials[0] = pL[1].multiply(pP[2]).negate();
			polynomials[1] = pL[0].multiply(pP[2]);
			polynomials[2] = pL[0].multiply(pP[1]).negate()
					.add(pL[1].multiply(pP[0]));
			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		if (proverAdapter == null) {
			proverAdapter = new OrthoLinePointLineAdapter();
		}
		return proverAdapter.getBotanaVars();
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (proverAdapter == null) {
			proverAdapter = new OrthoLinePointLineAdapter();
		}
		return proverAdapter.getBotanaPolynomials((GeoLine) l, P);
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
		return getLoc().getPlainDefault("LineThroughAPerpendicularToB",
				"Line through %0 perpendicular to %1",
				P.getLabel(tpl), ((GeoElement) l).getLabel(tpl));
	}

}
