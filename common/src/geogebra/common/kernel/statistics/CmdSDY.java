package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdOneOrTwoListsFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
/**
 * SDY[List of points]
 * SDY[List of numbers, list of numbers]
 */
public class CmdSDY extends CmdOneOrTwoListsFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdSDY(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		
		AlgoListSDY algo = new AlgoListSDY(cons, a, b);
		return algo.getResult();
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return null;
	}

}
