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

package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable.LevelOfDetail;
import org.geogebra.common.main.MyError;

/**
 * Level of detail (for 3D surfaces)
 */
public class CmdSetLevelOfDetail extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetLevelOfDetail(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 2:
			GeoElement[] arg = resArgs(c);
			if (arg[1].isNumberValue()) {
				if (arg[0] instanceof SurfaceEvaluable) {
					int lod = (int) arg[1].evaluateDouble();
					SurfaceEvaluable se = (SurfaceEvaluable) arg[0];
					if (lod >= 1) {
						se.setLevelOfDetail(LevelOfDetail.QUALITY);
					} else {
						se.setLevelOfDetail(LevelOfDetail.SPEED);
					}
				}
				return arg;
			}
			throw argErr(c, arg[1]);

		default:
			throw argNumErr(c);
		}
	}
}
