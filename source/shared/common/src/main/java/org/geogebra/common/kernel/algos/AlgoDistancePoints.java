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

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 *
 * @author Markus
 */
public class AlgoDistancePoints extends AlgoElement
		implements DistanceAlgo, SymbolicParametersBotanaAlgo {

	private GeoPointND P; // input
	private GeoPointND Q; // input
	private GeoNumeric dist; // output

	private PVariable[] botanaVars;

	/**
	 * @param cons
	 *            construction
	 * @param P
	 *            point
	 * @param Q
	 *            point
	 */
	public AlgoDistancePoints(Construction cons, GeoPointND P, GeoPointND Q) {
		super(cons);
		this.P = P;
		this.Q = Q;
		dist = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		// compute length
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Distance;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_DISTANCE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) P;
		input[1] = (GeoElement) Q;

		setOnlyOutput(dist);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public GeoNumeric getDistance() {
		return dist;
	}

	/*
	 * GeoPoint getP() { return P; } GeoPoint getQ() { return Q; }
	 */

	// calc length of vector v
	@Override
	public final void compute() {
		dist.setValue(P.distance(Q));
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("DistanceOfAandB",
				"Distance between %0 and %1", P.getLabel(tpl),
				Q.getLabel(tpl));

	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		return botanaVars;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {

		GeoPoint P1 = (GeoPoint) P;
		GeoPoint P2 = (GeoPoint) Q;

		if (P1 != null && P2 != null) {

			PVariable[] vP1 = P1.getBotanaVars(P1);
			PVariable[] vP2 = P2.getBotanaVars(P2);

			if (botanaVars == null) {
				botanaVars = new PVariable[4];
				botanaVars[0] = vP1[0];
				botanaVars[1] = vP1[1];
				botanaVars[2] = vP2[0];
				botanaVars[3] = vP2[1];

			}

			return null;
		}
		throw new NoSymbolicParametersException();

	}

}
