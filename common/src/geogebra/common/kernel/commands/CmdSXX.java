package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.statistics.AlgoDoubleListSXX;
import geogebra.common.kernel.statistics.AlgoListSXX;
import geogebra.common.kernel.statistics.AlgoSXX;
/**
 * SXX[list of points]
 * SXX[list of numbers,list of numbers]
 *
 */

public class CmdSXX extends CmdOneOrTwoListsFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdSXX(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		GeoNumeric num;
		GeoElement geo = b.get(0);
		if (geo.isNumberValue()) { // list of numbers
			AlgoSXX algo = new AlgoSXX(cons, a, b);
			num = algo.getResult();
		} else { // (probably) list of points
			AlgoListSXX algo = new AlgoListSXX(cons, a, b);
			num = algo.getResult();
		}
		return num;
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		AlgoDoubleListSXX algo = new AlgoDoubleListSXX(cons, a, b,
				c);
		return algo.getResult();
	}


}
