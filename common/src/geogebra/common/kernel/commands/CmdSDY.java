package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoListSDY;
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
