package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdOneOrTwoListsFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
/**
 * SDX[List of points]
 * SDX[List of numbers, list of numbers]
 */
public class CmdSDX extends CmdOneOrTwoListsFunction {

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdSDX(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoListSDX algo = new AlgoListSDX(cons, a, b);
		return algo.getResult();
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return null;
	}

}
