/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicPart3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.algos.AlgoConicPart;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Circular arc or sector defined by the circle's center, one point on the
 * circle (start point) and another point (angle for end-point).
 */
public class AlgoConicPartCircle3D extends AlgoConicPart {

	private GeoPointND center;
	private GeoPointND startPoint;
	private GeoPointND endPoint;

	private PathParameter paramP;
	private PathParameter paramQ;
	private Coords p2d;

	/**
	 * Creates a new arc or sector algorithm. The type is either
	 * GeoConicPart.CONIC_PART_ARC or GeoConicPart.CONIC_PART_ARC
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param center
	 *            center
	 * @param startPoint
	 *            arc start point
	 * @param endPoint
	 *            arc end point
	 * @param type
	 *            arc type
	 */
	public AlgoConicPartCircle3D(Construction cons, String label,
			GeoPointND center, GeoPointND startPoint, GeoPointND endPoint,
			int type) {
		this(cons, label, center, startPoint, endPoint, null, type);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param center
	 *            center
	 * @param startPoint
	 *            arc start point
	 * @param endPoint
	 *            arc end point
	 * @param orientation
	 *            orientation
	 * @param type
	 *            arc type
	 */
	public AlgoConicPartCircle3D(Construction cons, String label,
			GeoPointND center, GeoPointND startPoint, GeoPointND endPoint,
			GeoDirectionND orientation, int type) {
		this(cons, center, startPoint, endPoint, orientation, type);
		conicPart.setLabel(label);
	}

	private AlgoConicPartCircle3D(Construction cons, GeoPointND center,
			GeoPointND startPoint, GeoPointND endPoint,
			GeoDirectionND orientation, int type) {
		super(cons, type);
		this.center = center;
		this.startPoint = startPoint;
		this.endPoint = endPoint;

		// create circle with center through startPoint
		AlgoCircle3DCenterPointPoint algo = new AlgoCircle3DCenterPointPoint(
				cons, center, startPoint, endPoint);
		cons.removeFromConstructionList(algo);
		conic = algo.getCircle();

		// orientation
		setOrientation(orientation);

		// temp Points
		paramP = new PathParameter();
		paramQ = new PathParameter();

		conicPart = new GeoConicPart3D(cons, type);
		conicPart.addPointOnConic(startPoint);

		setInputOutput(); // for AlgoElement
		initCoords();
		compute();
		setIncidence();
	}

	/**
	 * init Coords values
	 */
	protected void initCoords() {
		p2d = new Coords(4);
	}

	/**
	 * set orientation for the arc/sector
	 * 
	 * @param orientation
	 *            orientation
	 */
	protected void setOrientation(GeoDirectionND orientation) {
		// not used here
	}

	private void setIncidence() {
		startPoint.addIncidence(conicPart, false);
		// endPoint.addIncidence(conicPart);
	}

	public GeoPointND getStartPoint() {
		return startPoint;
	}

	public GeoPointND getEndPoint() {
		return endPoint;
	}

	public GeoPointND getCenter() {
		return center;
	}

	@Override
	public Commands getClassName() {
		switch (type) {
		case GeoConicNDConstants.CONIC_PART_ARC:
			return Commands.CircleArc;
		default:
			return Commands.CircleSector;
		}
	}

	@Override
	public int getRelatedModeID() {
		switch (type) {
		case GeoConicNDConstants.CONIC_PART_ARC:
			return EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS;
		default:
			return EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS;
		}
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		setInput();
		setOnlyOutput(conicPart);
		setDependencies();
	}

	/**
	 * set input
	 */
	protected void setInput() {
		setInput(3);
	}

	/**
	 * set input
	 * 
	 * @param n
	 *            input length
	 */
	protected void setInput(int n) {
		input = new GeoElement[n];
		input[0] = (GeoElement) center;
		input[1] = (GeoElement) startPoint;
		input[2] = (GeoElement) endPoint;
	}

	/**
	 * set arc/sector when center is aligned between start and end points
	 * 
	 * @param center1
	 *            center coords
	 * @param v1
	 *            radius vector
	 */
	protected void semiCircle(Coords center1, Coords v1) {
		// points aligned, undefined
		conicPart.setUndefined();
	}

	@Override
	public final void compute() {

		CoordSys cs = conic.getCoordSys();

		if (!cs.isDefined()) {
			Coords centerCoords = center.getInhomCoordsInD3();
			Coords startCoords = startPoint.getInhomCoordsInD3();
			Coords v1 = startCoords.sub(centerCoords);
			Coords v2 = endPoint.getInhomCoordsInD3().sub(centerCoords);
			if (DoubleUtil.isGreater(0, v1.dotproduct(v2))) {
				semiCircle(centerCoords, v1);
				return;
			}

			// points aligned, single point
			GeoConic3D.setSinglePoint((GeoConic3D) conicPart, startCoords);
			((GeoConicPart3D) conicPart).setParametersToSinglePoint();
			conicPart.setType(GeoConicNDConstants.CONIC_SINGLE_POINT);
			return;
		}

		// the temp points P and Q should lie on the conic
		startPoint.getInhomCoordsInD3()
				.projectPlaneInPlaneCoords(cs.getMatrixOrthonormal(), p2d);
		p2d.setZ(1);
		conic.pointChanged(p2d, paramP);

		endPoint.getInhomCoordsInD3()
				.projectPlaneInPlaneCoords(cs.getMatrixOrthonormal(), p2d);
		p2d.setZ(1);
		conic.pointChanged(p2d, paramQ);

		// now take the parameters from the temp points
		setConicPart(paramP.t, paramQ.t);
	}

	/**
	 * set conic part coord sys and parameters
	 * 
	 * @param start
	 *            start parameter
	 * @param end
	 *            end parameter
	 */
	protected void setConicPart(double start, double end) {
		conicPart.set(conic);
		((GeoConicPartND) conicPart).setParameters(start, end,
				getPositiveOrientation());
	}

	/**
	 * 
	 * @return positive orientation
	 */
	protected boolean getPositiveOrientation() {
		return true;
	}

	@Override
	public GeoConicPart3D getConicPart() {
		return (GeoConicPart3D) super.getConicPart();
	}

}
