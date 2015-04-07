/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoEllipseFociPoint.java
 * 
 * Ellipse with Foci A and B passing through point C
 *
 * Michael Borcherds
 * 2008-04-06
 * adapted from EllipseFociLength
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
 * @version
 */
public abstract class AlgoEllipseHyperbolaFociPointND extends AlgoElement {

	protected GeoPointND A, B, C; // input
	protected GeoConicND conic; // output

	final protected int type; // ellipse or hyperbola

	public AlgoEllipseHyperbolaFociPointND(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoPointND C,
			GeoDirectionND orientation, final int type) {
		this(cons, A, B, C, orientation, type);
		conic.setLabel(label);
	}

	public AlgoEllipseHyperbolaFociPointND(Construction cons, GeoPointND A,
			GeoPointND B, GeoPointND C, GeoDirectionND orientation,
			final int type) {
		super(cons);

		this.type = type;

		this.A = A;
		this.B = B;
		this.C = C;
		setOrientation(orientation);
		conic = newGeoConic(cons);
		setInputOutput(); // for AlgoElement

		initCoords();

		compute();
		addIncidence();
	}

	/**
	 * init coords values
	 */
	protected void initCoords() {
		// none used here
	}

	/**
	 * for 3D, set an orientation
	 * 
	 * @param orientation
	 *            orientation
	 */
	protected void setOrientation(GeoDirectionND orientation) {
		// not needed in 2D
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @return new conic
	 */
	abstract protected GeoConicND newGeoConic(Construction cons);

	/**
	 * @author Tam
	 * 
	 *         for special cases of e.g. AlgoIntersectLineConic
	 */
	private void addIncidence() {
		if (C != null)
			C.addIncidence(conic, false);

	}

	@Override
	public Commands getClassName() {

		if (type == GeoConicNDConstants.CONIC_HYPERBOLA) {
			return Commands.Hyperbola;
		}
		return Commands.Ellipse;
	}

	@Override
	public int getRelatedModeID() {

		if (type == GeoConicNDConstants.CONIC_HYPERBOLA) {
			return EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS;
		}
		return EuclidianConstants.MODE_ELLIPSE_THREE_POINTS;
	}

	/**
	 * set the input
	 */
	protected void setInput() {
		input = new GeoElement[3];
		input[0] = (GeoElement) A;
		input[1] = (GeoElement) B;
		input[2] = (GeoElement) C;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		setInput();

		super.setOutputLength(1);
		super.setOutput(0, conic);
		setDependencies(); // done by AlgoElement
	}

	public GeoConicND getConic() {
		return conic;
	}

	public GeoPointND getFocus1() {
		// Public for LocusEqu
		return A;
	}

	public GeoPointND getFocus2() {
		// Public for LocusEqu
		return B;
	}

	/**
	 * Method for LocusEqu
	 * 
	 * @return returns external point for ellipse.
	 */
	public GeoPointND getExternalPoint() {
		return C;
	}

	// compute ellipse with foci A, B passing through C
	@Override
	public void compute() {

		double xyA[] = new double[2];
		double xyB[] = new double[2];
		double xyC[] = new double[2];
		getA2d().getInhomCoords(xyA);
		getB2d().getInhomCoords(xyB);
		getC2d().getInhomCoords(xyC);

		double length;
		double length1 = Math.sqrt((xyA[0] - xyC[0]) * (xyA[0] - xyC[0])
				+ (xyA[1] - xyC[1]) * (xyA[1] - xyC[1]));
		double length2 = Math.sqrt((xyB[0] - xyC[0]) * (xyB[0] - xyC[0])
				+ (xyB[1] - xyC[1]) * (xyB[1] - xyC[1]));

		if (type == GeoConicNDConstants.CONIC_HYPERBOLA) {
			length = Math.abs(length1 - length2);
		} else {
			length = length1 + length2; // ellipse
		}

		conic.setEllipseHyperbola(getA2d(), getB2d(), length / 2);
	}

	/**
	 * 
	 * @return point A in 2D coords
	 */
	abstract protected GeoPoint getA2d();

	/**
	 * 
	 * @return point B in 2D coords
	 */
	abstract protected GeoPoint getB2d();

	/**
	 * 
	 * @return point C in 2D coords
	 */
	abstract protected GeoPoint getC2d();

	@Override
	public String toString(StringTemplate tpl) {
		if (type == GeoConicNDConstants.CONIC_HYPERBOLA) {
			return getLoc().getPlain("HyperbolaWithFociABPassingThroughC",
					A.getLabel(tpl), B.getLabel(tpl), C.getLabel(tpl));
		}

		return getLoc().getPlain("EllipseWithFociABPassingThroughC",
				A.getLabel(tpl), B.getLabel(tpl), C.getLabel(tpl));
	}

}
