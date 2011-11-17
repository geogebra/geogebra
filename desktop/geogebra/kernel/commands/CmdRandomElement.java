package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

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
