/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoIncircle.java, dsun48 [6/26/2011]
 *
 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoIncircle extends AlgoElement {

	private GeoPointND A, B, C; // input
	protected GeoConicND circle; // output

	// angle bisector calculations
	private GeoLine bisectorC, bisectorB, sideBC, heightBC;
	private GeoPoint heightFoot, incenter;
	private GeoPoint A1, B1, C1;

	public AlgoIncircle(Construction cons, String label, GeoPointND A,
			GeoPointND B, GeoPointND C) {
		this(cons, A, B, C);
		circle.setLabel(label);
	}

	public AlgoIncircle(Construction cons, GeoPointND A, GeoPointND B,
			GeoPointND C) {

		super(cons);

		this.A = A;
		this.B = B;
		this.C = C;

		circle = new GeoConic(cons); // output

		bisectorC = new GeoLine(cons);
		bisectorB = new GeoLine(cons);
		heightFoot = new GeoPoint(cons);
		heightBC = new GeoLine(cons);
		sideBC = new GeoLine(cons);
		incenter = new GeoPoint(cons);
		A1 = new GeoPoint(cons);
		B1 = new GeoPoint(cons);
		C1 = new GeoPoint(cons);

		setInputOutput();

		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Incircle;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = (GeoElement) A;
		input[1] = (GeoElement) B;
		input[2] = (GeoElement) C;

		super.setOutputLength(1);
		super.setOutput(0, circle);
		setDependencies(); // done by AlgoElement
	}

	public GeoConicND getCircle() {
		return circle;
	}

	public GeoPoint getA() {
		return (GeoPoint) A;
	}

	public GeoPoint getB() {
		return (GeoPoint) B;
	}

	public GeoPoint getC() {
		return (GeoPoint) C;
	}

	// compute incircle of triangle A, B, C
	@Override
	public void compute() {
		// bisector of angle ABC
		double dAB = A.distance(B);
		double dAC = A.distance(C);
		double dBC = B.distance(C);		
		double s = (dAB + dAC + dBC) / 2;
		double[] Ac = A.getPointAsDouble();
		double[] Bc = B.getPointAsDouble();
		double[] Cc = C.getPointAsDouble();
		double wA = dBC / s / 2;
		double wB = dAC / s / 2;
		double wC = dAB / s / 2;

		incenter.setCoords(Ac[0] * wA + Bc[0] * wB + Cc[0] * wC, Ac[1] * wA
				+ Bc[1] * wB + Cc[1] * wC, 1);
		double radius = Math.sqrt((s - dBC) * (s - dAC) / s * (s - dAB));
		circle.setCircle(incenter, radius);
	}

	@Override
	public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("IncircleOfTriangleABC", A.getLabel(tpl),
				B.getLabel(tpl), C.getLabel(tpl));
	}

	// TODO Consider locusequability
}

// Local Variables:
// indent-tabs-mode: nil
// c-basic-offset: 4
// tab-width: 4
// End:
// vim: set expandtab shiftwidth=4 softtabstop=4 tabstop=4

