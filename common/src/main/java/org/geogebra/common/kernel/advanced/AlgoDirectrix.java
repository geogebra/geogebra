/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDirectrix.java
 *
 * Created on 30. August 2001, 21:37
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
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;

/**
 *
 * @author Markus
 */
public class AlgoDirectrix extends AlgoElement {

	private GeoConic c; // input
	private GeoLine directrix; // output
	private GeoLine directrix2; // output

	private GeoVec2D[] eigenvec;
	private GeoVec2D b;
	private GeoPoint P;
	private GeoPoint P2;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param c
	 *            conic
	 */
	public AlgoDirectrix(Construction cons, String label, GeoConic c) {
		super(cons);
		this.c = c;

		eigenvec = c.eigenvec;
		b = c.getB();

		directrix = new GeoLine(cons);
		directrix2 = new GeoLine(cons);
		P = new GeoPoint(cons);
		P2 = new GeoPoint(cons);
		directrix.setStartPoint(P);
		directrix2.setStartPoint(P2);

		setInputOutput(); // for AlgoElement
		compute();
		directrix2.setLabel(label);
		directrix.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Directrix;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = c;

		super.setOutputLength(2);
		super.setOutput(0, directrix);
		super.setOutput(1, directrix2);
		setDependencies(); // done by AlgoElement
	}

	public GeoLine getDirectrix() {
		return directrix;
	}

	public GeoLine getDirectrix2() {
		return directrix2;
	}

	GeoConic getConic() {
		return c;
	}

	// calc axes
	@Override
	public final void compute() {

		if (!c.isDefined()) {
			directrix.setUndefined();
			directrix2.setUndefined();
			return;
		}
		// only parabola has directrix
		if (c.type == GeoConicNDConstants.CONIC_PARABOLA) {
			// directrix has direction of second eigenvector
			// through point (b - p/2* eigenvec1)
			directrix.x = -eigenvec[1].getY();
			directrix.y = eigenvec[1].getX();
			double px = b.getX() - c.p / 2.0 * eigenvec[0].getX();
			double py = b.getY() - c.p / 2.0 * eigenvec[0].getY();
			directrix.z = -(directrix.x * px + directrix.y * py);

			directrix2.setUndefined();

		} else if (c.type == GeoConicNDConstants.CONIC_ELLIPSE
				|| c.type == GeoConicNDConstants.CONIC_HYPERBOLA) {
			// https://jira.geogebra.org/browse/GGB-183
			// parallel to the minor axis, at a distance a/e away from the
			// origin.

			double e = c.eccentricity;
			double a = c.getHalfAxis(0);

			double py = b.getY() + eigenvec[0].getY() * a / e;
			double px = b.getX() + eigenvec[0].getX() * a / e;
			double py2 = b.getY() - eigenvec[0].getY() * a / e;
			double px2 = b.getX() - eigenvec[0].getX() * a / e;

			directrix.x = -eigenvec[1].getY();
			directrix.y = eigenvec[1].getX();
			directrix.z = -(directrix.x * px + directrix.y * py);

			P.setCoords(px, py, 1.0);

			directrix2.x = -eigenvec[1].getY();
			directrix2.y = eigenvec[1].getX();
			directrix2.z = -(directrix.x * px2 + directrix.y * py2);

			P2.setCoords(px2, py2, 1.0);

		} else {
			directrix.setUndefined();
			directrix2.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("DirectrixOfA", "Directrix of %0",
				c.getLabel(tpl));
	}

}
