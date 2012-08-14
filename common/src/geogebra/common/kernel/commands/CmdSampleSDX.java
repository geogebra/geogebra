package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoListSampleSDX;
/**
 * SampleSDX[List of points]
 * SampleSDX[List of numbers, list of numbers]
 */
public class CmdSampleSDX extends CmdOneOrTwoListsFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSampleSDX(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoListSampleSDX algo = new AlgoListSampleSDX(cons, a, b);
		return algo.getResult();
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return null;
	}


}
