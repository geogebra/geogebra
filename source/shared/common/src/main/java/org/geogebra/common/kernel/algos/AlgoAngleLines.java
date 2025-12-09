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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 *
 * @author Markus
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

	@Override
	public AlgoAngleLines copy() {
		return new AlgoAngleLines(g.copy(), h.copy());
	}

	// calc angle between lines g and h
	// use normal vectors (gx, gy), (hx, hy)
	@Override
	public void compute() {
		// |v| * |w| * sin(alpha) = det(v, w)
		// cos(alpha) = v . w / (|v| * |w|)
		// tan(alpha) = sin(alpha) / cos(alpha)
		// => tan(alpha) = det(v, w) / v . w
		double det = ((GeoLine) g).x * ((GeoLine) h).y
				- ((GeoLine) g).y * ((GeoLine) h).x;
		double prod = ((GeoLine) g).x * ((GeoLine) h).x
				+ ((GeoLine) g).y * ((GeoLine) h).y;
		double value = Math.atan2(det, prod);

		angle.setValue(value);
	}

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
	public GeoElementND getInput(int i) {
		return getInputMaybeXOYPlane(i);
	}

}
