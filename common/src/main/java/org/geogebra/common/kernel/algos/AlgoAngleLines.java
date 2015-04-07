/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoAngleLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoAngleLines extends AlgoAngleLinesND {

	/**
	 * Creates new unlabeled angle between lines algo
	 * 
	 * @param cons
	 *            construction
	 * @param g
	 *            first line
	 * @param h
	 *            second line
	 * @param orientation
	 *            orientation (for 3D)
	 */
	AlgoAngleLines(Construction cons, GeoLineND g, GeoLineND h,
			GeoDirectionND orientation) {
		super(cons);
		setInput(g, h, orientation);
		angle = newGeoAngle(cons);
		setInputOutput(); // for AlgoElement

		// compute angle
		compute();

	}

	private AlgoAngleLines(GeoLineND g, GeoLineND h) {
		super(g, h);
	}

	/**
	 * Creates new labeled angle between lines algo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            angle label
	 * @param g
	 *            first line
	 * @param h
	 *            second line
	 */

	public AlgoAngleLines(Construction cons, String label, GeoLineND g,
			GeoLineND h) {
		this(cons, label, g, h, null);
	}

	/**
	 * Creates new labeled angle between lines algo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            angle label
	 * @param g
	 *            first line
	 * @param h
	 *            second line
	 * @param orientation
	 *            orientation (for 3D)
	 */

	public AlgoAngleLines(Construction cons, String label, GeoLineND g,
			GeoLineND h, GeoDirectionND orientation) {
		this(cons, g, h, orientation);
		angle.setLabel(label);
	}

	public AlgoAngleLines copy() {
		return new AlgoAngleLines(g.copy(), h.copy());
	}

	// calc angle between lines g and h
	// use normalvectors (gx, gy), (hx, hy)
	@Override
	public void compute() {
		// |v| * |w| * sin(alpha) = det(v, w)
		// cos(alpha) = v . w / (|v| * |w|)
		// tan(alpha) = sin(alpha) / cos(alpha)
		// => tan(alpha) = det(v, w) / v . w
		double det = ((GeoLine) g).x * ((GeoLine) h).y - ((GeoLine) g).y
				* ((GeoLine) h).x;
		double prod = ((GeoLine) g).x * ((GeoLine) h).x + ((GeoLine) g).y
				* ((GeoLine) h).y;
		double value = Math.atan2(det, prod);

		angle.setValue(value);
	}

	// TODO Consider locusequability

	// ///////////////////////////////
	// TRICKS FOR XOY PLANE
	// ///////////////////////////////

	@Override
	protected int getInputLengthForXML() {
		return getInputLengthForXMLMayNeedXOYPlane();
	}

	@Override
	protected int getInputLengthForCommandDescription() {
		return getInputLengthForCommandDescriptionMayNeedXOYPlane();
	}

	@Override
	public GeoElement getInput(int i) {
		return getInputMaybeXOYPlane(i);
	}

}
