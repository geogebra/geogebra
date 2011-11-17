package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

/*
 * SD[ list ]
 * adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
class CmdSD extends CmdOneListFunction {

	public CmdSD(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.StandardDeviation(a, b);
	}


}
