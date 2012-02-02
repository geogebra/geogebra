package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/*
 * Shuffle[ <List> ]
 */
public class CmdShuffle extends CmdOneListFunction {

	public CmdShuffle(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.Shuffle(a, b);
	}


}
