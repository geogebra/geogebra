/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoCircleTwoPoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.adapters.BotanaCircle;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * 
 * @author Markus
 */
public class AlgoCircleTwoPoints extends AlgoSphereNDTwoPoints
		implements SymbolicParametersBotanaAlgo {

	private BotanaCircle botanaParams;

	/**
	 * @param cons
	 *            construction
	 * @param M
	 *            center
	 * @param P
	 *            point on circle
	 */
	public AlgoCircleTwoPoints(Construction cons, GeoPoint M, GeoPoint P) {
		super(cons, M, P);
		setIncidence();
	}

	private void setIncidence() {
		((GeoPoint) getP()).addIncidence(getCircle(), false);
	}

	@Override
	protected GeoQuadricND createSphereND(Construction cons1) {
		GeoConic circle = new GeoConic(cons1);
		circle.addPointOnConic(getP()); // TODO do this in AlgoSphereNDTwoPoints
		return circle;
	}

	@Override
	public Commands getClassName() {
		return Commands.Circle;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_CIRCLE_TWO_POINTS;
	}

	public GeoConic getCircle() {
		return (GeoConic) getSphereND();
	}

	/*
	 * GeoPoint getM() { return M; } GeoPoint getP() { return P; }
	 */

	// compute circle with midpoint M and radius r
	/*
	 * public final void compute() { circle.setCircle(M, P); }
	 */

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (botanaParams == null) {
			botanaParams = new BotanaCircle();
		}
		return botanaParams.getBotanaVars(getP(), getM());
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		// It's OK to return null here since no constraint must be set:
		return null;
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
	final public String toString(StringTemplate tpl) {

		return getLoc().getPlainDefault("CircleThroughAwithCenterB",
				"Circle through %0 with center %1",
				getP().getLabel(tpl), getM().getLabel(tpl));
	}
}
