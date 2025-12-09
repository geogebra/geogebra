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

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoFocus3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoFocus;
import org.geogebra.common.kernel.commands.CmdFocus;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * 3D version of Focus
 *
 */
public class CmdFocus3D extends CmdFocus {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdFocus3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected AlgoFocus newAlgoFocus(Construction cons1, String[] labels,
			GeoConicND c) {

		if (c.isGeoElement3D()) {
			return new AlgoFocus3D(cons1, labels, c);
		}

		return super.newAlgoFocus(cons1, labels, c);
	}

}
