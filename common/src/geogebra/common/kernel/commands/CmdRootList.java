package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoRootList;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * RootList[ <List> ]
 */
public class CmdRootList extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdRootList(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoRootList algo = new AlgoRootList(cons, a, b);
		return algo.getResult();
	}


}
