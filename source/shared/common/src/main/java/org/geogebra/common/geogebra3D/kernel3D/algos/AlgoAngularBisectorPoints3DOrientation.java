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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * @author mathieu
 *
 */
public class AlgoAngularBisectorPoints3DOrientation
		extends AlgoAngularBisectorPoints3D {

	private GeoDirectionND orientation;

	/**
	 * Creates new AlgoLineBisector
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            leg
	 * @param B
	 *            vertex
	 * @param C
	 *            leg
	 * @param orientation
	 *            direction
	 */
	public AlgoAngularBisectorPoints3DOrientation(Construction cons,
			String label, GeoPointND A, GeoPointND B, GeoPointND C,
			GeoDirectionND orientation) {
		super(cons, label, A, B, C, orientation);
	}

	@Override
	protected void setInput(GeoDirectionND orientation) {

		this.orientation = orientation;

		input = new GeoElement[4];
		input[0] = (GeoElement) getA();
		input[1] = (GeoElement) getB();
		input[2] = (GeoElement) getC();
		input[3] = (GeoElement) orientation;
	}

	@Override
	final public String toString(StringTemplate tpl) {

		// orientation is space
		if (orientation == kernel.getSpace()) {
			return getLoc().getPlain("AngleBisectorOfABCInSpace",
					getA().getLabel(tpl), getB().getLabel(tpl),
					getC().getLabel(tpl),
					orientation.getLabel(tpl));
		}

		// orientation is plane
		if (orientation instanceof GeoCoordSys2D) {
			return getLoc().getPlain("AngleBisectorOfABCParallelToD",
					getA().getLabel(tpl), getB().getLabel(tpl),
					getC().getLabel(tpl),
					orientation.getLabel(tpl));
		}

		// orientation is line/vector
		return getLoc().getPlain("AngleBisectorOfABCPerpendicularToD",
				getA().getLabel(tpl), getB().getLabel(tpl),
				getC().getLabel(tpl),
				orientation.getLabel(tpl));

	}

	@Override
	protected void setCoordFromFiniteB(Coords o, Coords d, Coords v1) {

		Coords vn = orientation.getDirectionInD3();
		if (vn == null) { // e.g. for space
			super.setCoordFromFiniteB(o, d, v1);
			return;
		}

		if (d.isZero()) { // use orientation to compute line through B
							// orthogonal to AC
			Coords d1 = vn.crossProduct4(v1);
			super.setCoordFromFiniteB(o, d1, v1);

		} else { // check if d is orthogonal to orientation
			if (DoubleUtil.isZero(vn.dotproduct(d))) {
				super.setCoordFromFiniteB(o, d, v1);
			} else {
				getLine().setUndefined();
			}
		}

	}
}
