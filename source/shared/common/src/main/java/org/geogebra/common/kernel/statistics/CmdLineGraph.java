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

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;

public class CmdLineGraph extends CommandProcessor {

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdLineGraph(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		if (c.getArgumentNumber() != 2) {
			throw argNumErr(c);
		}
		GeoElement[] args = resArgs(c, info);
		GeoList xValues = (GeoList) validate(args[0], args[0].isGeoList(), c);
		GeoList yValues = (GeoList) validate(args[1], args[1].isGeoList(), c);
		AlgoLineGraph algo = new AlgoLineGraph(cons, xValues, yValues);
		algo.getOutput(0).setLabel(c.getLabel());
		return algo.getOutput(0).asArray();
	}
}
