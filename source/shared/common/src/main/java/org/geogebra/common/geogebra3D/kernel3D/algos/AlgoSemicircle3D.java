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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicPart3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoMidpoint;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.AlgoMidpointND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Semicircle defined by two points A and B (start and end point) with
 * orientation
 */
public class AlgoSemicircle3D extends AlgoElement {

	private GeoPointND A; // input
	private GeoPointND B; // input
	private GeoDirectionND orientation; // input
	private GeoConicPart3D conicPart; // output

	private PathParameter param;

	private GeoPointND M; // midpoint of AB
	private GeoConic3D conic;
	private Coords p2d;

	/**
	 * Creates new semicircle algorithm
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
	 * Creates new unlabeled semicircle algorithm
	 * 
	 * @param cons
	 *            construction
	 * @param A
	 *            first endpoint
	 * @param B
	 *            second endpoint
	 * @param orientation
	 *            orientation
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

		conicPart = new GeoConicPart3D(cons,
				GeoConicNDConstants.CONIC_PART_ARC);
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

		setOnlyOutput(conicPart);

		setDependencies();
	}

	/**
	 * Returns the semicircle.
	 * 
	 * @return the semicircle
	 */
	public GeoConicPart3D getSemicircle() {
		return conicPart;
	}

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
		return getLoc().getPlain("SemicircleThroughAandB",
				A.getLabel(tpl), B.getLabel(tpl));
	}

}
