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
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdOneOrTwoListsFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * SXX[list of points] SXX[list of numbers,list of numbers]
 *
 */

public class CmdSXX extends CmdOneOrTwoListsFunction {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSXX(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b) {
		GeoNumeric num;
		GeoElement geo = b.get(0);
		if (geo instanceof NumberValue) { // list of numbers
			AlgoSXX algo = new AlgoSXX(cons, b);
			num = algo.getResult();
		} else { // (probably) list of points
			AlgoListSXX algo = new AlgoListSXX(cons, b);
			num = algo.getResult();
		}
		num.setLabel(a);
		return num;
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c) {
		AlgoDoubleListSXX algo = new AlgoDoubleListSXX(cons, a, b, c);
		return algo.getResult();
	}

}
