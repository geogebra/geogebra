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

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdOneListFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Mean absolute deviation MAD[ list ] adapted from CmdVariance by Michael
 * Borcherds 2008-02-18
 */
public class CmdMAD extends CmdOneListFunction {

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMAD(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b) {
		AlgoMeanAbsoluteDeviation algo = new AlgoMeanAbsoluteDeviation(cons, b);
		algo.getResult().setLabel(a);
		return algo.getResult();
	}

	@Override
	protected GeoElement doCommand(String a, Command c, GeoList list,
			GeoList freq) {
		AlgoMeanAbsoluteDeviation algo = new AlgoMeanAbsoluteDeviation(cons, list, freq);
		algo.getResult().setLabel(a);
		return algo.getResult();
	}
}
