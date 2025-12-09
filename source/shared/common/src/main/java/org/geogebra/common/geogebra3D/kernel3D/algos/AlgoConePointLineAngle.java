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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author ggb3D
 *
 */
public class AlgoConePointLineAngle extends AlgoQuadricPointNumber {

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            output label
	 * @param origin
	 *            vertex
	 * @param axis
	 *            axis
	 * @param angle
	 *            angle
	 */
	public AlgoConePointLineAngle(Construction c, String label,
			GeoPointND origin, GeoLineND axis, GeoNumberValue angle) {
		super(c, label, origin, axis, angle,
				new AlgoQuadricComputerCone());
	}

	@Override
	protected Coords getDirection() {
		GeoLineND axis = (GeoLineND) getSecondInput();
		return axis.getPointInD(3, 1).getInhomCoordsInSameDimension()
				.sub(axis.getPointInD(3, 0).getInhomCoordsInSameDimension());
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain(getPlainName(), getOrigin().getLabel(tpl),
				getSecondInput().getLabel(tpl), getNumber().getLabel(tpl));

	}

	@Override
	final protected String getPlainName() {
		return "ConeWithCenterAAxisParallelToBAngleC";
	}

	@Override
	public Commands getClassName() {
		return Commands.ConeInfinite;
	}

}
