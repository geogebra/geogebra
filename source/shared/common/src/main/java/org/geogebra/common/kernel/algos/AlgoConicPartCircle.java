/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.adapters.BotanaCircle;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;

/**
 * Circular arc or sector defined by the circle's center, one point on the
 * circle (start point) and another point (angle for end-point).
 */
public class AlgoConicPartCircle extends AlgoConicPart implements
		SymbolicParametersBotanaAlgo {

	private GeoPoint center;
	private GeoPoint startPoint;
	private GeoPoint endPoint;

	private GeoPoint P;
	private GeoPoint Q;

	private BotanaCircle botanaParams;

	/**
	 * @param cons
	 *            construction
	 * @param center
	 *            center
	 * @param startPoint
	 *            start point
	 * @param endPoint
	 *            point on end ray
	 * @param type
	 *            GeoConicPart.CONIC_PART_ARC or GeoConicPart.CONIC_PART_ARC
	 */
	public AlgoConicPartCircle(Construction cons, GeoPoint center,
			GeoPoint startPoint, GeoPoint endPoint, int type) {
		super(cons, type);
		this.center = center;
		this.startPoint = startPoint;
		this.endPoint = endPoint;

		// create circle with center through startPoint
		AlgoCircleTwoPoints algo = new AlgoCircleTwoPoints(cons, center,
				startPoint);
		cons.removeFromConstructionList(algo);
		conic = algo.getCircle();

		// temp Points
		P = new GeoPoint(cons);
		Q = new GeoPoint(cons);

		conicPart = new GeoConicPart(cons, type);
		conicPart.addPointOnConic(startPoint);

		setInputOutput(); // for AlgoElement
		compute();
		setIncidence();
	}

	private void setIncidence() {
		startPoint.addIncidence(conicPart, false);
		// endPoint.addIncidence(conicPart);

	}

	public GeoPoint getStartPoint() {
		return startPoint;
	}

	public GeoPoint getEndPoint() {
		return endPoint;
	}

	public GeoPoint getCenter() {
		return center;
	}

	@Override
	public Commands getClassName() {
		if (type == GeoConicNDConstants.CONIC_PART_ARC) {
			return Commands.CircleArc;
		}
		return Commands.CircleSector;
	}

	@Override
	public int getRelatedModeID() {
		if (type == GeoConicNDConstants.CONIC_PART_ARC) {
			return EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS;
		}
		return EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = center;
		input[1] = startPoint;
		input[2] = endPoint;

		setOnlyOutput(conicPart);

		setDependencies();
	}

	@Override
	public final void compute() {

		// needed for eg http://www.geogebra.org/m/mfafi40w
		if (!startPoint.isDefined() || !endPoint.isDefined()
				|| !center.isDefined()) {
			conicPart.setUndefined();
			return;
		}

		// the temp points P and Q should lie on the conic
		P.setCoords(startPoint);
		conic.pointChanged(P);

		Q.setCoords(endPoint);
		conic.pointChanged(Q);

		// now take the parameters from the temp points
		conicPart.set(conic);
		((GeoConicPartND) conicPart).setParameters(P.getPathParameter().t,
				Q.getPathParameter().t, true);
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (botanaParams == null) {
			botanaParams = new BotanaCircle();
		}
		return botanaParams.getBotanaVars(startPoint, center);
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		// It's OK to return null here since no constraint must be set:
		return null;
	}

	@Override
	public GeoConicPart getConicPart() {
		return (GeoConicPart) super.getConicPart();
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

}
