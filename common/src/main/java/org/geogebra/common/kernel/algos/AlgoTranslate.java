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
		SymbolicParametersAlgo {

	private Translateable out;
	protected GeoElement inGeo;
	protected GeoElement outGeo;
	protected GeoElement v; // input
	private Polynomial[] polynomials;

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

	// TODO Consider locusequability
}
