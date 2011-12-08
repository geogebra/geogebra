package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.kernel.Kernel;

/*
 * Invert[ <List> ]
 * Michael Borcherds 
 */
class CmdInvert extends CmdOneListFunction {

	public CmdInvert(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Invert(a, b);
	}


}
