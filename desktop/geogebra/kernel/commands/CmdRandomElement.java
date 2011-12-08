package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.kernel.Kernel;

/*
 * Shuffle[ <List> ]
 */
class CmdRandomElement extends CmdOneListFunction {

	public CmdRandomElement(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.RandomElement(a, b);
	}


}
