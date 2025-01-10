/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.FixedPathRegionAlgo;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.adapters.PointOnPathAdapter;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

public class AlgoPointOnPath extends AlgoElement
		implements FixedPathRegionAlgo, SymbolicParametersAlgo,
		SymbolicParametersBotanaAlgo {

	private Path path; // input
	/** output */
	protected GeoPointND P;
	private GeoNumberValue param;
	private PPolynomial[] polynomials;
	private PVariable variable;
	private PointOnPathAdapter proverAdapter;

	/**
	 * @param cons
	 *            construction
	 * @param path
	 *            path
	 * @param param
	 *            path parameter
	 */
	public AlgoPointOnPath(Construction cons, Path path,
			GeoNumberValue param) {
		this(cons, path, 0, 0, 0, param);
	}

	/**
	 * @param cons
	 *            construction
	 * @param path
	 *            path
	 * @param x
	 *            estimate of x-coord
	 * @param y
	 *            estimate of y-coord
	 * @param z
	 *            estimate of z-coord
	 * @param param
	 *            path parameter
	 */
	public AlgoPointOnPath(Construction cons, Path path, double x, double y,
			double z, GeoNumberValue param) {
		super(cons);
		this.path = path;

		// create point on path and compute current location
		createPoint(path, x, y, z);

		this.param = param;

		setInputOutput(); // for AlgoElement
		compute();
		addIncidence();
	}

	/**
	 * @param cons
	 *            construction
	 * @param path
	 *            path
	 * @param x
	 *            estimate of x-coord
	 * @param y
	 *            estimate of y-coord
	 */
	public AlgoPointOnPath(Construction cons, Path path, double x, double y) {
		this(cons, path, x, y, 0, true);
	}

	/**
	 * @param cons
	 *            construction
	 * @param path
	 *            path
	 * @param x
	 *            estimate of x-coord
	 * @param y
	 *            estimate of y-coord
	 * @param z
	 *            estimate of z-coord
	 * @param addIncidence
	 *            whether to add to incidence list
	 */
	public AlgoPointOnPath(Construction cons, Path path, double x, double y,
			double z, boolean addIncidence) {
		super(cons, addIncidence);
		this.path = path;

		// create point on path and compute current location
		createPoint(path, x, y, z);

		setInputOutput(); // for AlgoElement
		if (addIncidence) {
			addIncidence();
		} else {
			P.setEuclidianVisible(false);
		}
	}

	/**
	 * @author Tam
	 * 
	 *         for special cases of e.g. AlgoIntersectLineConic
	 */
	private void addIncidence() {
		P.addIncidence((GeoElement) path, false);

	}

	/**
	 * @param z
	 *            point z-coord; ignored in 2D
	 */
	protected void createPoint(Path path1, double x, double y, double z) {
		P = new GeoPoint(cons);
		P.setPath(path1);
		P.setCoords(x, y, 1.0);
	}

	@Override
	public Commands getClassName() {
		return Commands.Point;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_POINT;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		if (param == null) {
			input = new GeoElement[1];
			input[0] = path.toGeoElement();
		} else {
			input = new GeoElement[2];
			input[0] = path.toGeoElement();
			input[1] = param.toGeoElement();
		}
		setOnlyOutput(P);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting point
	 */
	public GeoPointND getP() {
		return P;
	}

	/**
	 * @return path
	 */
	public Path getPath() {
		return path;
	}

	@Override
	public final void compute() {
		if (param != null) {
			PathParameter pp = P.getPathParameter();
			pp.setT(PathNormalizer.toParentPathParameter(param.getDouble(),
					path.getMinParameter(), path.getMaxParameter()));
		}
		if (input[0].isDefined()) {
			path.pathChanged(P);
			P.updateCoords();
		} else {
			P.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("PointOnA", "Point on %0",
				input[0].getLabel(tpl));
	}

	@Override
	public boolean isChangeable(GeoElementND out) {
		return param == null;
	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {
		if (input[0] instanceof GeoSegment) {
			throw new NoSymbolicParametersException();
		}
		if (input[0] instanceof GeoLine) {
			((SymbolicParametersAlgo) input[0]).getFreeVariables(variables);
			if (variable == null) {
				variable = new PVariable((GeoElement) P);
			}
			variables.add(variable);
			return;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {
		if (input[0] instanceof GeoSegment) {
			throw new NoSymbolicParametersException();
		}
		if (input[0] instanceof GeoLine) {
			int[] degreesLine = ((SymbolicParametersAlgo) input[0])
					.getDegrees(a);

			int[] result = new int[3];
			result[0] = degreesLine[2] + 1;
			result[1] = degreesLine[2] + 1;
			result[2] = Math.max(degreesLine[0] + 1, degreesLine[1] + 1);
			return result;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (input[0] instanceof GeoSegment) {
			throw new NoSymbolicParametersException();
		}
		if (input[0] instanceof GeoLine && variable != null) {
			BigInteger[] exactCoordinates = new BigInteger[3];
			BigInteger[] line = ((SymbolicParametersAlgo) input[0])
					.getExactCoordinates(values);

			if (line[2].equals(BigInteger.ZERO)) {
				/*
				 * this line is going through the origin, we simply substitute
				 */
				exactCoordinates[0] = line[1].multiply(values.get(variable));
				exactCoordinates[1] = line[0].multiply(values.get(variable));
				exactCoordinates[2] = BigInteger.ONE;
			} else {
				/*
				 * using Simon's original code otherwise, it doesn't seem to
				 * handle the previous case properly
				 */
				exactCoordinates[0] = line[2].multiply(values.get(variable));
				exactCoordinates[1] = line[2].multiply(
						BigInteger.ONE.subtract(values.get(variable)));
				exactCoordinates[2] = line[0]
						.multiply(values.get(variable).negate())
						.add(line[1].multiply(
								values.get(variable).subtract(BigInteger.ONE)));
				/* maybe there is a way to unify the two cases, TODO */
			}

			return exactCoordinates;
		}
		return null;
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if (input[0] instanceof GeoSegment) {
			throw new NoSymbolicParametersException();
		}
		if (path instanceof GeoLine) {
			if (variable == null) {
				variable = new PVariable((GeoElement) P);
			}
			polynomials = new PPolynomial[3];
			PPolynomial[] line = ((SymbolicParametersAlgo) input[0])
					.getPolynomials();
			polynomials[0] = line[2].multiply(new PPolynomial(variable));
			polynomials[1] = line[2].multiply(
					(new PPolynomial(1)).subtract(new PPolynomial(variable)));
			polynomials[2] = line[0]
					.multiply((new PPolynomial(variable)).negate())
					.add(line[1].multiply((new PPolynomial(variable))
							.subtract(new PPolynomial(1))));
			return polynomials;

		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (this.proverAdapter == null) {
			this.proverAdapter = new PointOnPathAdapter();
		}
		return proverAdapter.getBotanaPolynomials(path.toGeoElement());
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		if (this.proverAdapter == null) {
			this.proverAdapter = new PointOnPathAdapter();
		}
		return proverAdapter.getBotanaVars();
	}

}
