package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoListSYY;
/**
 * SYY[list of points]
 * SYY[list of numbers,list of numbers]
 *
 */

public class CmdSYY extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdSYY(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoListSYY algo = new AlgoListSYY(cons, a, b);
		return algo.getResult();
	}
}
