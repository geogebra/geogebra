package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
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
