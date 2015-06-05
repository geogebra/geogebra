/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoTranslatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoTranslate extends AlgoTransformation implements
		SymbolicParametersAlgo, SymbolicParametersBotanaAlgo {

	private Translateable out;
	protected GeoElement inGeo;
	protected GeoElement outGeo;
	protected GeoElement v; // input
	private Polynomial[] polynomials;

	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;

	/**
	 * Creates labeled translation algo
	 * 
	 * @param cons
	 * @param label
	 * @param in
	 * @param v
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
	 * @param in
	 * @param v
	 */
	public AlgoTranslate(Construction cons, GeoElement in, GeoElement v) {
		super(cons);
		this.v = v;

		inGeo = in;

		// create output object
		outGeo = getResultTemplate(inGeo);
		if (outGeo instanceof Translateable)
			out = (Translateable) outGeo;

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

		setOutputLength(1);
		setOutput(0, outGeo);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public GeoElement getResult() {
		return outGeo;
	}

	// calc translated point
	@Override
	public final void compute() {
		if (inGeo.isGeoList()) {
			transformList((GeoList) inGeo, (GeoList) outGeo);
			return;
		}

		if (!v.isDefined()) {
			out.setUndefined();
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

	/**
	 * set inGeo to outGeo
	 */
	protected void setOutGeo() {
		outGeo.set(inGeo);
	}

	protected Coords getVectorCoords() {
		GeoVec3D vec = (GeoVec3D) v;
		return new Coords(vec.x, vec.y, vec.z);
	}

	@Override
	final public String toString(StringTemplate tpl) {

		// Michael Borcherds 2008-03-24 simplified code!
		return getLoc().getPlain("TranslationOfAbyB", inGeo.getLabel(tpl),
				v.getLabel(tpl));
	}

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if (!(outGeo instanceof GeoList)) {
			out = (Translateable) outGeo;
		}
	}

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (inGeo instanceof GeoPoint && v instanceof GeoVector) {
			((SymbolicParametersAlgo) inGeo).getFreeVariables(variables);
			((SymbolicParametersAlgo) v).getFreeVariables(variables);

			return;
		}
		throw new NoSymbolicParametersException();
	}

	public int[] getDegrees() throws NoSymbolicParametersException {
		if (inGeo instanceof GeoPoint && v instanceof GeoVector) {
			int[] degree1 = ((SymbolicParametersAlgo) inGeo).getDegrees();
			int[] degree2 = ((SymbolicParametersAlgo) v).getDegrees();
			int[] result = new int[3];

			result[0] = Math.max(degree1[0] + degree2[2], degree2[0]
					+ degree1[2]);
			result[1] = Math.max(degree1[1] + degree2[2], degree2[1]
					+ degree1[2]);
			result[2] = degree2[2] + degree1[2];

			return result;
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(
			final HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (inGeo instanceof GeoPoint && v instanceof GeoVector) {
			BigInteger[] coords1 = ((SymbolicParametersAlgo) inGeo)
					.getExactCoordinates(values);
			BigInteger[] coords2 = ((SymbolicParametersAlgo) v)
					.getExactCoordinates(values);
			BigInteger[] result = new BigInteger[3];
			result[0] = coords1[0].multiply(coords2[2]).add(
					coords2[0].multiply(coords1[2]));
			result[1] = coords1[1].multiply(coords2[2]).add(
					coords2[1].multiply(coords1[2]));
			result[2] = coords1[2].multiply(coords2[2]);
			return SymbolicParameters.reduce(result);
		}
		return null;
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if (inGeo instanceof GeoPoint && v instanceof GeoVector) {
			Polynomial[] coords1 = ((SymbolicParametersAlgo) inGeo)
					.getPolynomials();
			Polynomial[] coords2 = ((SymbolicParametersAlgo) v)
					.getPolynomials();
			polynomials = new Polynomial[3];
			polynomials[0] = coords1[0].multiply(coords2[2]).add(
					coords2[0].multiply(coords1[2]));
			polynomials[1] = coords1[1].multiply(coords2[2]).add(
					coords2[1].multiply(coords1[2]));
			polynomials[2] = coords1[2].multiply(coords2[2]);
			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public double getAreaScaleFactor() {
		return 1;
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {
		
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		GeoPoint P = (GeoPoint) inGeo;
		GeoVector u = (GeoVector) v;
		
		if (P != null && v != null) {

			Variable[] vP = P.getBotanaVars(P);
			Variable[] vv = u.getBotanaVars(u);

			if (botanaVars == null) {
			botanaVars = new Variable[6];
			// A'
			botanaVars[0] = new Variable();
			botanaVars[1] = new Variable();
			// P
			botanaVars[2] = vP[0];
			botanaVars[3] = vP[1];
			// v
			botanaVars[4] = vv[0];
			botanaVars[5] = vv[1];
			}

			botanaPolynomials = new Polynomial[2];

			Polynomial p1 = new Polynomial(vP[0]);
			Polynomial p2 = new Polynomial(vP[1]);
			Polynomial u1 = new Polynomial(vv[0]);
			Polynomial u2 = new Polynomial(vv[1]);
			Polynomial p_1 = new Polynomial(botanaVars[0]);
			Polynomial p_2 = new Polynomial(botanaVars[1]);

			botanaPolynomials[0] = p1.add(u1).subtract(p_1);
			botanaPolynomials[1] = p2.add(u2).subtract(p_2);

			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}

	// TODO Consider locusequability
}
