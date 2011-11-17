package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

/*
 * HarmonicMean[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
class CmdHarmonicMean extends CmdOneListFunction {

	public CmdHarmonicMean(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.HarmonicMean(a, b);
	}


}
