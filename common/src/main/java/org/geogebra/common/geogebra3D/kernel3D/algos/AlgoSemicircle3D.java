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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoMidpoint;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.AlgoMidpointND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Semicircle defined by two points A and B (start and end point) with
 * orientation
 */
public class AlgoSemicircle3D extends AlgoElement {

	private GeoPointND A, B; // input
	private GeoDirectionND orientation; // input
	private GeoConicPart3D conicPart; // output

	private PathParameter param;

	private GeoPointND M; // midpoint of AB
	private GeoConic3D conic;

	/**
	 * Creates new semicircle algoritm
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for the semicircle
	 * @param A
	 *            first endpoint
	 * @param B
	 *            second endpoint
	 */
	public AlgoSemicircle3D(Construction cons, String label, GeoPointND A,
			GeoPointND B, GeoDirectionND orientation) {
		this(cons, A, B, orientation);
		conicPart.setLabel(label);
	}

	/**
	 * Creates new unlabeled semicircle algoritm
	 * 
	 * @param cons
	 *            construction
	 * @param A
	 *            first endpoint
	 * @param B
	 *            second endpoint
	 */
	public AlgoSemicircle3D(Construction cons, GeoPointND A, GeoPointND B,
			GeoDirectionND orientation) {
		super(cons);

		p2d = new Coords(4);

		this.A = A;
		this.B = B;
		this.orientation = orientation;

		// helper algo to get midpoint
		AlgoMidpointND algom;
		if (A.isGeoElement3D() || B.isGeoElement3D()) {
			algom = new AlgoMidpoint3D(cons, A, B);
		} else {
			algom = new AlgoMidpoint(cons, (GeoPoint) A, (GeoPoint) B);
		}
		cons.removeFromConstructionList(algom);
		M = algom.getPoint();

		// helper algo to get circle
		AlgoCircle3DPointPointDirection algo = new AlgoCircle3DPointPointDirection(
				cons, M, B, orientation);
		cons.removeFromConstructionList(algo);
		conic = algo.getCircle();

		conicPart = new GeoConicPart3D(cons, GeoConicNDConstants.CONIC_PART_ARC);
		conicPart.addPointOnConic(A);
		conicPart.addPointOnConic(B);

		param = new PathParameter();

		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Semicircle;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_SEMICIRCLE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = (GeoElement) A;
		input[1] = (GeoElement) B;
		input[2] = (GeoElement) orientation;

		setOutputLength(1);
		setOutput(0, conicPart);

		setDependencies();
	}

	/**
	 * Returns the semicercle
	 * 
	 * @return the semicircle
	 */
	public GeoConicPart3D getSemicircle() {
		return conicPart;
	}

	private Coords p2d;

	@Override
	public void compute() {
		if (!conic.isDefined()) {
			conicPart.setUndefined();
			return;
		}

		B.getInhomCoordsInD3().projectPlaneInPlaneCoords(
				conic.getCoordSys().getMatrixOrthonormal(), p2d);
		p2d.setZ(1);
		conic.pointChanged(p2d, param);

		conicPart.set(conic);
		conicPart.setParameters(param.t, param.t + Math.PI, true);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("SemicircleThroughAandBOrientedbyC",
				((GeoElement) A).getLabel(tpl), ((GeoElement) B).getLabel(tpl),
				((GeoElement) orientation).getLabel(tpl));
	}

}
