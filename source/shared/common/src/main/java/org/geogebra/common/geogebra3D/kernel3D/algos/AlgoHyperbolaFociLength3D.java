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

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Ellipse for given foci and first semi-axis length
 * 
 * @author Markus
 */
public class AlgoHyperbolaFociLength3D extends AlgoConicFociLength3D {

	public AlgoHyperbolaFociLength3D(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoNumberValue a,
			GeoDirectionND orientation) {
		super(cons, label, A, B, a, orientation);
	}

	@Override
	public Commands getClassName() {
		return Commands.Hyperbola;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// direction is plane
		if (orientation instanceof GeoCoordSys2D) {
			return getLoc().getPlain(
					conic.isEllipse() || conic.isCircle()
							? "EllipseWithFociABandFirstAxisLengthCParallelToD"
							: "HyperbolaWithFociABandFirstAxisLengthCParallelToD",
					A.getLabel(tpl), B.getLabel(tpl),
					a.toGeoElement().getLabel(tpl), orientation.getLabel(tpl));
		}

		// direction is line
		return getLoc().getPlain(
				conic.isEllipse() || conic.isCircle()
						? "EllipseWithFociABandFirstAxisLengthCPerpendicularToD"
						: "HyperbolaWithFociABandFirstAxisLengthCPerpendicularToD",
				A.getLabel(tpl), B.getLabel(tpl),
				a.toGeoElement().getLabel(tpl), orientation.getLabel(tpl));
	}

}
