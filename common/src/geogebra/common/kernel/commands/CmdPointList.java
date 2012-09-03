package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.advanced.AlgoPointList;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * PointList[ <List> ]
 */
public class CmdPointList extends CmdOneListFunction {

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdPointList(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoPointList algo = new AlgoPointList(cons, a, b);
		return algo.getResult();
	}


}
