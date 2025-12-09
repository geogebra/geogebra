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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.adapters.BotanaCircleThreePoints;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * Circle arc or sector defined by three points.
 */
public class AlgoConicPartCircumcircle extends AlgoConicPartCircumcircleND
		implements SymbolicParametersBotanaAlgo {

	private BotanaCircleThreePoints botanaParams;

	public AlgoConicPartCircumcircle(Construction cons, String label,
			GeoPoint A, GeoPoint B, GeoPoint C, int type) {
		super(cons, label, A, B, C, type);
	}

	public AlgoConicPartCircumcircle(Construction cons, GeoPoint A, GeoPoint B,
			GeoPoint C, int type) {
		super(cons, A, B, C, type);
	}

	@Override
	protected AlgoCircleThreePoints getAlgo() {
		return new AlgoCircleThreePoints(cons, getA(), getB(), getC());
	}

	@Override
	protected GeoConicPart createConicPart(Construction cons1, int partType) {
		return new GeoConicPart(cons1, partType);
	}

	/**
	 * Method for LocusEqu.
	 * 
	 * @return first point.
	 */
	@Override
	final public GeoPoint getA() {
		return (GeoPoint) A;
	}

	/**
	 * Method for LocusEqu.
	 * 
	 * @return second point.
	 */
	@Override
	final public GeoPoint getB() {
		return (GeoPoint) B;
	}

	/**
	 * Method for LocusEqu.
	 * 
	 * @return third point.
	 */
	@Override
	final public GeoPoint getC() {
		return (GeoPoint) C;
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo) {
		if (botanaParams == null) {
			botanaParams = new BotanaCircleThreePoints();
		}
		return botanaParams.getBotanaVars();
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {

		if (botanaParams == null) {
			botanaParams = new BotanaCircleThreePoints();
		}
		return botanaParams.getPolynomials(getInput());
	}

	@Override
	public GeoConicPart getConicPart() {
		return (GeoConicPart) super.getConicPart();
	}

	@Override
	protected void computeSinglePoint() {
		GeoPoint midpoint = getA();
		GeoConic.setSinglePoint((GeoConic) conicPart, midpoint.inhomX,
				midpoint.inhomY);
		super.computeSinglePoint();
	}

}
