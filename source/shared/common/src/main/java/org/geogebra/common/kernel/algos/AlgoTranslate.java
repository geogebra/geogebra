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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * 
 * @author Markus
 */
public class AlgoTranslate extends AlgoTransformation
		implements SymbolicParametersAlgo, SymbolicParametersBotanaAlgo {

	private Translateable out;
	protected GeoElement v; // input
	private PPolynomial[] polynomials;

	private PPolynomial[] botanaPolynomials;
	private PVariable[] botanaVars;

	/**
	 * Creates labeled translation algo.
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param in
	 *            input geo
	 * @param v
	 *            translation vector
	 */
	public AlgoTranslate(Construction cons, String label, GeoElement in,
			GeoVec3D v) {
		this(cons, in, v);
		outGeo.setLabel(label);
	}

	/**
	 * Creates unlabeled translation algo
	 * 
	 * @param cons
	 *            construction
	 * @param in
	 *            input geo
	 * @param v
	 *            translation vector
	 */
	public AlgoTranslate(Construction cons, GeoElement in, GeoElement v) {
		super(cons);
		this.v = v;

		inGeo = in;

		// create output object
		outGeo = getResultTemplate(inGeo);
		if (outGeo instanceof Translateable) {
			out = (Translateable) outGeo;
		}

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Translate;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TRANSLATE_BY_VECTOR;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = inGeo;
		input[1] = v;

		setOnlyOutput(outGeo);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public GeoElement getResult() {
		return outGeo;
	}

	// calc translated point
	@Override
	public final void compute() {
		if (inGeo instanceof GeoList && outGeo instanceof GeoList) {
			transformList((GeoList) inGeo, (GeoList) outGeo);
			return;
		}

		// out might be null if out is not Translateable
		if (!v.isDefined() || out == null) {
			outGeo.setUndefined();
			return;
		}

		setOutGeo();
		if (!out.isDefined()) {
			return;
		}

		out.translate(getVectorCoords());
		if (inGeo.isLimitedPath()) {
			this.transformLimitedPath(inGeo, outGeo);
		}
	}

	protected Coords getVectorCoords() {
		GeoVec3D vec = (GeoVec3D) v;
		return new Coords(vec.x, vec.y, vec.z);
	}

	@Override
	final public String toString(StringTemplate tpl) {

		// Michael Borcherds 2008-03-24 simplified code!
		return getLoc().getPlainDefault("TranslationOfAbyB",
				"Translation of %0 by %1",
				inGeo.getLabel(tpl), v.getLabel(tpl));
	}

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if (!(outGeo instanceof GeoList) && (outGeo instanceof Translateable)) {
			out = (Translateable) outGeo;
		}
	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {
		if (inGeo instanceof GeoPoint && v instanceof GeoVector) {
			((SymbolicParametersAlgo) inGeo).getFreeVariables(variables);
			((SymbolicParametersAlgo) v).getFreeVariables(variables);

			return;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {
		if (inGeo instanceof GeoPoint && v instanceof GeoVector) {
			int[] degree1 = ((SymbolicParametersAlgo) inGeo).getDegrees(a);
			int[] degree2 = ((SymbolicParametersAlgo) v).getDegrees(a);
			int[] result = new int[3];

			result[0] = Math.max(degree1[0] + degree2[2],
					degree2[0] + degree1[2]);
			result[1] = Math.max(degree1[1] + degree2[2],
					degree2[1] + degree1[2]);
			result[2] = degree2[2] + degree1[2];

			return result;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			final HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (inGeo instanceof GeoPoint && v instanceof GeoVector) {
			BigInteger[] coords1 = ((SymbolicParametersAlgo) inGeo)
					.getExactCoordinates(values);
			BigInteger[] coords2 = ((SymbolicParametersAlgo) v)
					.getExactCoordinates(values);
			BigInteger[] result = new BigInteger[3];
			result[0] = coords1[0].multiply(coords2[2])
					.add(coords2[0].multiply(coords1[2]));
			result[1] = coords1[1].multiply(coords2[2])
					.add(coords2[1].multiply(coords1[2]));
			result[2] = coords1[2].multiply(coords2[2]);
			return SymbolicParameters.reduce(result);
		}
		return null;
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if (inGeo instanceof GeoPoint && v instanceof GeoVector) {
			PPolynomial[] coords1 = ((SymbolicParametersAlgo) inGeo)
					.getPolynomials();
			PPolynomial[] coords2 = ((SymbolicParametersAlgo) v)
					.getPolynomials();
			polynomials = new PPolynomial[3];
			polynomials[0] = coords1[0].multiply(coords2[2])
					.add(coords2[0].multiply(coords1[2]));
			polynomials[1] = coords1[1].multiply(coords2[2])
					.add(coords2[1].multiply(coords1[2]));
			polynomials[2] = coords1[2].multiply(coords2[2]);
			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public double getAreaScaleFactor() {
		return 1;
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

		GeoPoint P = (GeoPoint) inGeo;
		GeoVector u = (GeoVector) v;

		if (P != null && v != null) {

			PVariable[] vP = P.getBotanaVars(P);
			PVariable[] vv = u.getBotanaVars(u);

			if (botanaVars == null) {
				botanaVars = new PVariable[6];
				// A'
				botanaVars[0] = new PVariable(kernel);
				botanaVars[1] = new PVariable(kernel);
				// P
				botanaVars[2] = vP[0];
				botanaVars[3] = vP[1];
				// v
				botanaVars[4] = vv[0];
				botanaVars[5] = vv[1];
			}

			botanaPolynomials = new PPolynomial[2];

			PPolynomial p1 = new PPolynomial(vP[0]);
			PPolynomial p2 = new PPolynomial(vP[1]);
			PPolynomial u1 = new PPolynomial(vv[0]);
			PPolynomial u2 = new PPolynomial(vv[1]);
			PPolynomial p_1 = new PPolynomial(botanaVars[0]);
			PPolynomial p_2 = new PPolynomial(botanaVars[1]);

			botanaPolynomials[0] = p1.add(u1).subtract(p_1);
			botanaPolynomials[1] = p2.add(u2).subtract(p_2);

			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}

}
