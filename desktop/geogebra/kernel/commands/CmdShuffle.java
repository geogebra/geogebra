package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

/*
 * Shuffle[ <List> ]
 */
class CmdShuffle extends CmdOneListFunction {

	public CmdShuffle(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Shuffle(a, b);
	}


}
