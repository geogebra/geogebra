package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoDoubleListSXY;
import geogebra.common.kernel.statistics.AlgoListSXY;
/**
 * SXY[list of points]
 * SXY[list of numbers,list of numbers]
 *
 */

public class CmdSXY extends CmdOneOrTwoListsFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdSXY(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoListSXY algo = new AlgoListSXY(cons, a, b);
		return algo.getResult();
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		AlgoDoubleListSXY algo = new AlgoDoubleListSXY(cons, a, b,
				c);
		return algo.getResult();
	}

}
