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
import org.geogebra.common.kernel.LocusEquation;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;

/**
 * Circular arc or sector defined by the circle's center, one point on the
 * circle (start point) and another point (angle for end-point).
 */
public class AlgoConicPartCircle extends AlgoConicPart {

	private GeoPoint center, startPoint, endPoint;

	private GeoPoint P, Q;

	/**
	 * Creates a new arc or sector algorithm. The type is either
	 * GeoConicPart.CONIC_PART_ARC or GeoConicPart.CONIC_PART_ARC
	 */
	public AlgoConicPartCircle(Construction cons, String label,
			GeoPoint center, GeoPoint startPoint, GeoPoint endPoint, int type) {
		this(cons, center, startPoint, endPoint, type);
		conicPart.setLabel(label);
	}

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
		switch (type) {
		case GeoConicPart.CONIC_PART_ARC:
			return Commands.CircleArc;
		default:
			return Commands.CircleSector;
		}
	}

	@Override
	public int getRelatedModeID() {
		switch (type) {
		case GeoConicPart.CONIC_PART_ARC:
			return EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS;
		default:
			return EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS;
		}
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = center;
		input[1] = startPoint;
		input[2] = endPoint;

		super.setOutputLength(1);
		super.setOutput(0, conicPart);

		setDependencies();
	}

	@Override
	public final void compute() {

		// needed for eg http://tube.geogebra.org/m/mfafi40w
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
	public boolean isLocusEquable() {
		return true;
	}

	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {
		return LocusEquation.eqnCircleArc(geo, this, scope);
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
	public GeoElement getInput(int i) {
		return getInputMaybeXOYPlane(i);
	}

}
