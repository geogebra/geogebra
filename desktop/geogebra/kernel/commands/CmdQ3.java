package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;

/*
 * Sum[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
class CmdQ3 extends CmdOneListFunction {

	public CmdQ3(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Q3(a, b);
	}


}
