/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAngleConic.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoAngleConic extends AlgoAngle {

	private GeoConic c; // input
	private GeoAngle angle; // output

	public AlgoAngleConic(Construction cons, String label, GeoConic c) {
		super(cons);
		this.c = c;
		angle = new GeoAngle(cons);
		setInputOutput(); // for AlgoElement
		compute();
		angle.setDrawable(true);
		angle.setLabel(label);
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = c;

		setOutputLength(1);
		setOutput(0, angle);
		setDependencies(); // done by AlgoElement
	}

	public GeoAngle getAngle() {
		return angle;
	}

	GeoConic getConic() {
		return c;
	}

	// compute conic's angle
	@Override
	public final void compute() {
		// take a look at first eigenvector
		angle.setValue(Math.atan2(c.eigenvec[0].getY(), c.eigenvec[0].getX()));
	}

	@Override
	public final String toString(StringTemplate tpl) {
		return getLoc().getPlain("AngleOfA", c.getLabel(tpl));
	}

	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec,
			DrawAngle drawable) {

		m[0] = c.b.getX();
		m[1] = c.b.getY();
		firstVec[0] = 1;
		firstVec[1] = 0;

		return true;
	}

	public boolean getCoordsInD3(Coords[] drawCoords) {
		drawCoords[0] = c.getMidpoint3D();
		drawCoords[1] = Coords.VX;
		drawCoords[2] = c.getEigenvec3D(0);

		return true;
	}

	// TODO Consider locusequability
}
