/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoEllipseFociLength.java
 *
 * Created on 15. November 2001, 21:37
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Ellipse for given foci and first semi-axis length
 * 
 * @author Markus
 * @version
 */
public class AlgoEllipseFociLength3D extends AlgoConicFociLength3D {

	public AlgoEllipseFociLength3D(Construction cons, String label,
			GeoPointND A, GeoPointND B, NumberValue a,
			GeoDirectionND orientation) {
		super(cons, label, A, B, a, orientation);
	}

	@Override
	public Commands getClassName() {
		return Commands.Ellipse;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_ELLIPSE_THREE_POINTS;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// direction is plane
		if (orientation instanceof GeoCoordSys2D) {
			return getLoc().getPlain(
							conic.isHyperbola() ? "HyperbolaWithFociABandFirstAxisLengthCParallelToD"
									: "EllipseWithFociABandFirstAxisLengthCParallelToD",
					A.getLabel(tpl), B.getLabel(tpl),
					a.toGeoElement().getLabel(tpl), orientation.getLabel(tpl));
		}

		// direction is line
		return getLoc().getPlain(
						conic.isHyperbola() ? "HyperbolaWithFociABandFirstAxisLengthCPerpendicularToD"
								: "EllipseWithFociABandFirstAxisLengthCPerpendicularToD",
				A.getLabel(tpl), B.getLabel(tpl),
				a.toGeoElement().getLabel(tpl), orientation.getLabel(tpl));
	}

}
