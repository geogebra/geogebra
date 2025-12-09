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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Algo for infinite cone (point, direction, angle)
 * 
 * @author matthieu
 *
 */
public class AlgoConeInfinitePointVectorNumber
		extends AlgoQuadricPointVectorNumber {

	/**
	 * @param c
	 *            constructor
	 * @param label
	 *            label
	 * @param origin
	 *            origin
	 * @param direction
	 *            direction
	 * @param angle
	 *            angle
	 */
	public AlgoConeInfinitePointVectorNumber(Construction c, String label,
			GeoPointND origin, GeoVectorND direction, GeoNumberValue angle) {
		super(c, label, origin, direction, angle,
				new AlgoQuadricComputerCone());
	}

	@Override
	final protected String getPlainName() {
		return "InfiniteConePointAVectorBNumberC";
	}

	@Override
	public Commands getClassName() {
		return Commands.ConeInfinite;
	}

}
