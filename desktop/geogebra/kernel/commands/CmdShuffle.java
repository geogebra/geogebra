package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;

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
