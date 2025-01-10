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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.debug.Log;

/**
 * Circle arc or sector defined by three points.
 */
public abstract class AlgoConicPartCircumcircleND extends AlgoConicPart {

	protected GeoPointND A;
	protected GeoPointND B;
	protected GeoPointND C;

	private GeoLine line; // for degenerate case

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param A
	 *            start point
	 * @param B
	 *            point on arc
	 * @param C
	 *            end point
	 * @param type
	 *            conic type
	 */
	public AlgoConicPartCircumcircleND(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoPointND C, int type) {
		this(cons, A, B, C, type);
		conicPart.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            start point
	 * @param B
	 *            point on arc
	 * @param C
	 *            end point
	 * @param type
	 *            conic type
	 */
	public AlgoConicPartCircumcircleND(Construction cons, GeoPointND A,
			GeoPointND B, GeoPointND C, int type) {
		super(cons, type);
		this.A = A;
		this.B = B;
		this.C = C;

		// helper algo to get circle
		AlgoCircleThreePoints algo = getAlgo();
		cons.removeFromConstructionList(algo);
		conic = algo.getCircle();

		conicPart = createConicPart(cons, type);
		conicPart.addPointOnConic(A);
		conicPart.addPointOnConic(B);
		conicPart.addPointOnConic(C);

		setInputOutput(); // for AlgoElement
		compute();
		setIncidence();
	}

	/**
	 * 
	 * @param cons1
	 *            construction
	 * @param type1
	 *            part type (arc or sector)
	 * @return output conic part
	 */
	abstract protected GeoConicND createConicPart(Construction cons1,
			int type1);

	/**
	 * 
	 * @return circle algo
	 */
	abstract protected AlgoCircleThreePoints getAlgo();

	private void setIncidence() {
		A.addIncidence(conicPart, false);
		B.addIncidence(conicPart, false);
		C.addIncidence(conicPart, false);
	}

	@Override
	public Commands getClassName() {
		switch (type) {
		case GeoConicNDConstants.CONIC_PART_ARC:
			return Commands.CircumcircleArc;
		default:
			return Commands.CircumcircleSector;
		}
	}

	@Override
	public int getRelatedModeID() {
		switch (type) {
		case GeoConicNDConstants.CONIC_PART_ARC:
			return EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS;
		default:
			return EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS;
		}
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = (GeoElement) A;
		input[1] = (GeoElement) B;
		input[2] = (GeoElement) C;

		setOnlyOutput(conicPart);

		setDependencies();
	}

	@Override
	public void compute() {
		if (!conic.isDefined()) {
			conicPart.setUndefined();
			return;
		}

		conicPart.set(conic);
		switch (conicPart.getType()) {
		case GeoConicNDConstants.CONIC_PARALLEL_LINES:
			computeDegenerate();
			break;

		case GeoConicNDConstants.CONIC_CIRCLE:
			computeCircle();
			break;

		case GeoConicNDConstants.CONIC_SINGLE_POINT:
			computeSinglePoint();
			break;

		default:
			// this should not happen
			Log.debug("AlgoCirclePartPoints: unexpected conic type: "
					+ conicPart.getType());
			conicPart.setUndefined();
		}
	}

	// arc degenerated to segment or two rays
	private void computeDegenerate() {
		if (line == null) { // init lines
			line = conicPart.getLines()[0];
			conicPart.getLines()[1].setStartPoint(getC());
		}
		line.setStartPoint(getA());
		line.setEndPoint(getC());

		// make sure the line goes through A and C
		GeoVec3D.lineThroughPoints(getA(), getC(), line);

		// check if B is between A and C => (1) segment AC
		// otherwise we got (2) two rays starting at A and C in opposite
		// directions
		// case (1): use parameters 0, 1 and positive orientation to tell
		// conicPart how to behave
		// case (2): use parameters 0, 1 and negative orientation
		double lambda = GeoPoint.affineRatio(getA(), getC(), getB());
		if (lambda < 0 || lambda > 1) {
			// two rays
			// second ray with start point C and direction of AC
			conicPart.getLines()[1].setCoords(line);
			conicPart.getLines()[1].setStartPoint(getC());
			// first ray with start point A and opposite direction
			line.changeSign();

			// tell conicPart about this case: two rays
			((GeoConicPartND) conicPart).setParameters(0, 1, false);
		} else {
			// segment
			// tell conicPart about this case: one segment
			((GeoConicPartND) conicPart).setParameters(0, 1, true);
		}
	}

	// circle through A, B, C
	private void computeCircle() {
		// start angle from vector MA
		double alpha = Math.atan2(
				getAy() - conicPart.getTranslationVector().getY(),
				getAx() - conicPart.getTranslationVector().getX());
		// end angle from vector MC
		double beta = Math.atan2(
				getCy() - conicPart.getTranslationVector().getY(),
				getCx() - conicPart.getTranslationVector().getX());

		// check orientation of triangle A, B, C to see
		// whether we have to swap start and end angle
		double det = (getBx() - getAx()) * (getCy() - getAy())
				- (getBy() - getAy()) * (getCx() - getAx());

		((GeoConicPartND) conicPart).setParameters(alpha, beta, det > 0);
	}

	/**
	 * compute as single point (A)
	 */
	protected void computeSinglePoint() {
		((GeoConicPartND) conicPart).setParametersToSinglePoint();
	}

	/**
	 * Method for LocusEqu.
	 * 
	 * @return first point.
	 */
	abstract public GeoPoint getA();

	/**
	 * Method for LocusEqu.
	 * 
	 * @return second point.
	 */
	abstract public GeoPoint getB();

	/**
	 * Method for LocusEqu.
	 * 
	 * @return third point.
	 */
	abstract public GeoPoint getC();

	final private double getAx() {
		return getA().inhomX;
	}

	final private double getAy() {
		return getA().inhomY;
	}

	final private double getBx() {
		return getB().inhomX;
	}

	final private double getBy() {
		return getB().inhomY;
	}

	final private double getCx() {
		return getC().inhomX;
	}

	final private double getCy() {
		return getC().inhomY;
	}

}
