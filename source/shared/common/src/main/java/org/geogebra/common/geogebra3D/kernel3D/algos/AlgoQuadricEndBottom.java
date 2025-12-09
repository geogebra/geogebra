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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute the bottom of a limited quadric
 * 
 * @author Mathieu
 *
 */
public class AlgoQuadricEndBottom extends AlgoQuadricEnd {
	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param quadric
	 *            quadric
	 */
	public AlgoQuadricEndBottom(Construction cons, String label,
			GeoQuadric3DLimited quadric) {
		super(cons, label, quadric);
	}

	@Override
	protected Coords getOrigin(Coords o1, Coords o2) {
		return o1;
	}

	@Override
	protected Coords getV1(Coords v1) {
		return v1.mul(-1);
	}

	@Override
	public Commands getClassName() {
		return Commands.Bottom;
	}

}
