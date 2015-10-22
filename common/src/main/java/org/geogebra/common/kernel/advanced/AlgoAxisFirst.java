/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAxes.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoAxisFirst extends AlgoElement {

	private GeoConicND c; // input
	private GeoLine axis; // output

	private GeoVec2D[] eigenvec;
	private GeoVec2D b;
	protected GeoPointND P;

	protected AlgoAxisFirst(Construction cons, GeoConicND c) {
		super(cons);
		this.c = c;

	}

	public AlgoAxisFirst(Construction cons, String label, GeoConicND c) {
		this(cons, c);

		eigenvec = c.eigenvec;
		b = c.b;

		axis = new GeoLine(cons);
		finishSetup(label);
	}

	protected void finishSetup(String label) {
		P = new GeoPoint(cons);
		getAxis().setStartPoint(P);

		setInputOutput(); // for AlgoElement
		compute();
		getAxis().setLabel(label);

	}

	@Override
	public Commands getClassName() {
		return Commands.FirstAxis;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = c;

		setOutputLength(1);
		setOutput(0, getAxis().toGeoElement());
		setDependencies(); // done by AlgoElement
	}

	public GeoLineND getAxis() {
		return axis;
	}

	protected GeoConicND getConic() {
		return c;
	}

	// calc axes
	@Override
	public void compute() {
		// axes are lines with directions of eigenvectors
		// through midpoint b

		axis.x = -eigenvec[0].getY();
		axis.y = eigenvec[0].getX();
		axis.z = -(axis.x * b.getX() + axis.y * b.getY());

		P.setCoords(b.getX(), b.getY(), 1.0);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("FirstAxisOfA", c.getLabel(tpl));
	}

	// TODO Consider locusequability
}
