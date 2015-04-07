/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoFoci.java
 *
 * Created on 11. November 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoFocus extends AlgoElement {

	protected GeoConicND c; // input
	protected GeoPointND[] focus; // output

	transient private double temp1, temp2;
	GeoVec2D b;
	GeoVec2D[] eigenvec;

	AlgoFocus(Construction cons, String label, GeoConicND c) {
		this(cons, c);
		GeoElement.setLabels(label, (GeoElement[]) focus);
	}

	public AlgoFocus(Construction cons, String[] labels, GeoConicND c) {
		this(cons, c);
		GeoElement.setLabels(labels, (GeoElement[]) focus);
	}

	AlgoFocus(Construction cons, GeoConicND c) {
		super(cons);
		this.c = c;

		createFocus(cons);

		// only first undefined point should be shown in algebra window
		focus[1].showUndefinedInAlgebraView(false);

		setInputOutput(); // for AlgoElement

		b = c.b;
		eigenvec = c.eigenvec;

		compute();
	}

	/**
	 * creates the focus
	 * 
	 * @param cons
	 */
	protected void createFocus(Construction cons) {
		focus = new GeoPoint[2];
		for (int i = 0; i < focus.length; i++) {
			focus[i] = new GeoPoint(cons);
		}

	}

	@Override
	public Commands getClassName() {
		return Commands.Focus;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = c;

		super.setOutput((GeoElement[]) focus);
		setDependencies(); // done by AlgoElement
	}

	GeoConicND getConic() {
		return c;
	}

	public GeoPointND[] getFocus() {
		return focus;
	}

	@Override
	public final void compute() {
		switch (c.type) {
		case GeoConicNDConstants.CONIC_CIRCLE:
			setCoords(0, b.getX(), b.getY());
			setCoords(1, b.getX(), b.getY());
			break;

		case GeoConicNDConstants.CONIC_ELLIPSE:
		case GeoConicNDConstants.CONIC_HYPERBOLA:
			temp1 = c.linearEccentricity * eigenvec[0].getX();
			temp2 = c.linearEccentricity * eigenvec[0].getY();
			setCoords(0, b.getX() - temp1, b.getY() - temp2);
			setCoords(1, b.getX() + temp1, b.getY() + temp2);
			break;

		case GeoConicNDConstants.CONIC_PARABOLA:
			temp1 = c.p / 2;
			setCoords(0, b.getX() + temp1 * eigenvec[0].getX(), b.getY()
					+ temp1 * eigenvec[0].getY());
			// second focus undefined
			focus[1].setUndefined();
			break;

		default:
			// both focus undefined
			focus[0].setUndefined();
			focus[1].setUndefined();
		}
	}

	/**
	 * set the coords of the i-th focus
	 * 
	 * @param i
	 * @param x
	 * @param y
	 */
	protected void setCoords(int i, double x, double y) {
		focus[i].setCoords(x, y, 1.0);
	}

	@Override
	public final String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("FocusOfA", c.getLabel(tpl));

	}

	// TODO Consider locusequability
}
