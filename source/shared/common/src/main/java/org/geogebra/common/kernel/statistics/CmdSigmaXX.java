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
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdOneListFunction;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * SigmaXX[ list ] adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
public class CmdSigmaXX extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSigmaXX(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String label, GeoList list) {
		GeoNumeric num;
		GeoElement geo = list.get(0);
		if (geo instanceof NumberValue) { // list of numbers
			AlgoSigmaXX algo = new AlgoSigmaXX(cons, list);
			num = algo.getResult();
		} else { // (probably) list of points
			AlgoListSigmaXX algo = new AlgoListSigmaXX(cons, list);
			num = algo.getResult();
		}
		num.setLabel(label);
		return num;
	}

	@Override
	protected GeoElement doCommand(String a, Command c, GeoList list,
			GeoList freq) {
		AlgoSigmaXX algo = new AlgoSigmaXX(cons, list, freq);
		algo.getResult().setLabel(a);
		return algo.getResult();
	}

	@Override
	protected GeoElement doCommand(String a, Command c, GeoList list,
			GeoList freq, GeoBoolean isGrouped) {
		throw argNumErr(c);
	}

}
