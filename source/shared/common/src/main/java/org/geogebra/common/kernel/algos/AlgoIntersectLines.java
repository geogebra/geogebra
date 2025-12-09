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

package org.geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 *
 * @author Markus
 */
public class AlgoIntersectLines extends AlgoIntersectAbstract
		implements SymbolicParametersAlgo, SymbolicParametersBotanaAlgo {

	private GeoLine g; // input
	private GeoLine h; // input
	private GeoPoint S; // output
	private PPolynomial[] polynomials;
	private PPolynomial[] botanaPolynomials;
	private PVariable[] botanaVars;

	/** Creates new AlgoJoinPoints */
	public AlgoIntersectLines(Construction cons, String label, GeoLine g,
			GeoLine h) {
		super(cons);
		this.g = g;
		this.h = h;
		S = new GeoPoint(cons);
		setInputOutput(); // for AlgoElement

		// compute line through P, Q
		compute();

		S.setLabel(label);
		addIncidence();

	}

	/**
	 * @author Tam
	 * 
	 *         for special cases of e.g. AlgoIntersectLineConic
	 */
	private void addIncidence() {
		S.addIncidence(g, false);
		S.addIncidence(h, false);
	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECT;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = g;
		input[1] = h;

		setOnlyOutput(S);
		setDependencies(); // done by AlgoElement
	}

	public GeoPoint getPoint() {
		return S;
	}

	/**
	 * TODO unused?
	 * @return first line
	 */
	public GeoLine geth() {
		return g;
	}

	/**
	 * TODO unused?
	 * @return second line
	 */
	public GeoLine getg() {
		return h;
	}

	// calc intersection S of lines g, h
	@Override
	public final void compute() {
		GeoVec3D.cross(g, h, S);

		// test the intersection point
		// this is needed for the intersection of segments
		if (S.isDefined()) {
			if (!(g.isIntersectionPointIncident(S, Kernel.MIN_PRECISION)
					&& h.isIntersectionPointIncident(S, Kernel.MIN_PRECISION))) {
				S.setUndefined();
			}
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("IntersectionOfAandB",
				"Intersection of %0 and %1", g.getLabel(tpl),
				h.getLabel(tpl));

	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {
		if ((g instanceof GeoSegment) || (h instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (g != null && h != null) {
			g.getFreeVariables(variables);
			h.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {
		if ((g instanceof GeoSegment) || (h instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (g != null && h != null) {
			int[] degree1 = g.getDegrees(a);
			int[] degree2 = h.getDegrees(a);
			return SymbolicParameters.crossDegree(degree1, degree2);
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			final HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {
		if ((g instanceof GeoSegment) || (h instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (g != null && h != null) {
			BigInteger[] coords1 = g.getExactCoordinates(values);
			BigInteger[] coords2 = h.getExactCoordinates(values);
			return SymbolicParameters.crossProduct(coords1, coords2);
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if ((g instanceof GeoSegment) || (h instanceof GeoSegment)) {
			throw new NoSymbolicParametersException();
		}
		if (g != null && h != null) {
			PPolynomial[] coords1 = g.getPolynomials();
			PPolynomial[] coords2 = h.getPolynomials();
			polynomials = PPolynomial.crossProduct(coords1, coords2);
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
		/*
		 * In fact we cannot decide a statement properly if any of the inputs is
		 * a segment, at least not algebraically (without using cylindrical
		 * algebraic decomposition or such). But since we are doing constructive
		 * geometry, it's better to assume that segment intersection is not a
		 * real problem. TODO: Consider adding an NDG somehow in this case (but
		 * maybe not really important and useful).
		 * 
		 * See also AlgoIntersectLineConic.
		 */
		if (g != null
				&& h != null /* && !g.isGeoSegment() && !h.isGeoSegment() */) {
			if (botanaVars == null) {
				botanaVars = new PVariable[2];
				botanaVars[0] = new PVariable(kernel);
				botanaVars[1] = new PVariable(kernel);
			}
			PVariable[] fv = g.getBotanaVars(g);
			botanaPolynomials = new PPolynomial[2];
			botanaPolynomials[0] = PPolynomial.collinear(fv[0], fv[1], fv[2],
					fv[3], botanaVars[0], botanaVars[1]);
			fv = h.getBotanaVars(h);
			botanaPolynomials[1] = PPolynomial.collinear(fv[0], fv[1], fv[2],
					fv[3], botanaVars[0], botanaVars[1]);

			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}

}
