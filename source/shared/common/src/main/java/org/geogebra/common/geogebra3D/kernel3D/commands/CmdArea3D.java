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

package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoAreaPoints3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoAreaPoints;
import org.geogebra.common.kernel.commands.CmdArea;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Area command
 *
 */
public class CmdArea3D extends CmdArea {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdArea3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected AlgoAreaPoints getAlgoAreaPoints(Construction cons1,
			GeoPointND[] points, boolean is3D) {
		if (is3D) {
			return new AlgoAreaPoints3D(cons1, points);
		}
		return new AlgoAreaPoints(cons1, points);
	}

}
