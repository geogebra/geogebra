package geogebra.common.kernel.algos;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdOneListFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * Reverse[ <List> ]
 * Michael Borcherds 2008-02-16
 */
public class CmdReverse extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdReverse(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoReverse algo = new AlgoReverse(cons, a, b);
		return algo.getResult();
	}


}
