package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoListSampleSDY;
/**
 * SampleSDY[List of points]
 * SampleSDY[List of numbers, list of numbers]
 */
public class CmdSampleSDY extends CmdOneOrTwoListsFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSampleSDY(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoListSampleSDY algo = new AlgoListSampleSDY(cons, a, b);
		return algo.getResult();
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return null;
	}

}
