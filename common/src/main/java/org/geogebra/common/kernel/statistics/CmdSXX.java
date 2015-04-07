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
			AlgoSXX algo = new AlgoSXX(cons, a, b);
			num = algo.getResult();
		} else { // (probably) list of points
			AlgoListSXX algo = new AlgoListSXX(cons, a, b);
			num = algo.getResult();
		}
		return num;
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c) {
		AlgoDoubleListSXX algo = new AlgoDoubleListSXX(cons, a, b, c);
		return algo.getResult();
	}

}
