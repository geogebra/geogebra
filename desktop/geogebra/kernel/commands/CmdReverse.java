package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

/*
 * Reverse[ <List> ]
 * Michael Borcherds 2008-02-16
 */
class CmdReverse extends CmdOneListFunction {

	public CmdReverse(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Reverse(a, b);
	}


}
