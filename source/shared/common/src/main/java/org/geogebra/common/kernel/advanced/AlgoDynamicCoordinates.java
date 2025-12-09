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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDynamicCoordinatesInterface;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 *
 * @author Michael
 */
public class AlgoDynamicCoordinates extends AlgoElement
		implements AlgoDynamicCoordinatesInterface,
		SymbolicParametersBotanaAlgo {

	protected GeoNumberValue x; // input
	protected GeoNumberValue y; // input
	protected GeoPointND P; // input
	protected GeoPointND M; // output
	private PVariable[] botanaVars;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param P
	 *            moving point
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	public AlgoDynamicCoordinates(Construction cons, String label, GeoPoint P,
			GeoNumberValue x, GeoNumberValue y) {
		super(cons);
		this.P = P;
		this.x = x;
		this.y = y;
		// create new Point
		M = new GeoPoint(cons);
		setInputOutput();

		compute();
		M.setLabel(label);
	}

	public AlgoDynamicCoordinates(Construction cons) {
		super(cons);
	}

	@Override
	public Commands getClassName() {
		return Commands.DynamicCoordinates;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = P.toGeoElement();
		input[1] = x.toGeoElement();
		input[2] = y.toGeoElement();

		setOnlyOutput(M);
		setDependencies(); // done by AlgoElement
	}

	public GeoPointND getPoint() {
		return M;
	}

	@Override
	public GeoPointND getParentPoint() {
		return P;
	}

	@Override
	public void compute() {
		double xCoord = x.getDouble();
		double yCoord = y.getDouble();

		if (Double.isNaN(xCoord) || Double.isInfinite(xCoord)
				|| Double.isNaN(yCoord) || Double.isInfinite(yCoord)) {
			M.setUndefined();
			return;
		}

		M.setCoords(xCoord, yCoord, 1.0);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("DynamicCoordinatesOfA",
				"Dynamic coordinates of %0", P.getLabel(tpl));
	}

	@Override
	public boolean isChangeable(GeoElementND out) {
		return true;
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		if (botanaVars != null) {
			return botanaVars;
		}
		// botanaVars = ((SymbolicParametersBotanaAlgo) P).getBotanaVars(P);
		botanaVars = new PVariable[2];
		botanaVars[0] = new PVariable(kernel); // ,true
		botanaVars[1] = new PVariable(kernel); // ,true
		return botanaVars;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		return null;
		/*
		 * if (botanaPolynomials != null) { return botanaPolynomials; }
		 * botanaPolynomials = ((SymbolicParametersBotanaAlgo) P)
		 * .getBotanaPolynomials(P); return botanaPolynomials;
		 */
	}
}
