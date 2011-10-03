package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;

/*
 * Rank[ <List> ]
 */
class CmdTiedRank extends CmdOneListFunction {

	public CmdTiedRank(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.TiedRank(a, b);
	}


}
