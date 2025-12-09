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

package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoRemovableDiscontinuity;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.MyError;

public class CmdRemovableDiscontinuity extends CommandProcessor implements UsesCAS {

	/**
	 * @param kernel kernel
	 */
	public CmdRemovableDiscontinuity(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		switch (n) {
		case 1:
			GeoElement[] arg = resArgs(c, info);
			GeoElement element = arg[0];
			if (element.isGeoFunction()) {
				return removableDiscontinuity((GeoFunction) element, c);
			}
			throw argErr(c, element);
		default:
			throw argNumErr(c);
		}
	}

	private GeoElement[] removableDiscontinuity(GeoFunction function, Command cmd) {
		AlgoRemovableDiscontinuity algo
				= new AlgoRemovableDiscontinuity(cons, function, cmd.getLabels());
		return algo.getOutput();
	}
}
