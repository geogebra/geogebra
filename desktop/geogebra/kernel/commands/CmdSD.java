package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.kernel.Kernel;

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
