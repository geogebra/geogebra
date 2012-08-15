package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoRootMeanSquare;

/**
 * RootMeanSquare[ list ]
 */
public class CmdRootMeanSquare extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdRootMeanSquare(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoRootMeanSquare algo = new AlgoRootMeanSquare(cons, a, b);
		return algo.getResult();
	}


}
