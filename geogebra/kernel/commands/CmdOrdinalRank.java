package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;

/*
 * Rank[ <List> ]
 */
class CmdOrdinalRank extends CmdOneListFunction {

	public CmdOrdinalRank(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.OrdinalRank(a, b);
	}


}
