package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

/*
 * SampleSD[ list ]
 * adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
class CmdSampleSD extends CmdOneListFunction {

	public CmdSampleSD(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SampleStandardDeviation(a, b);
	}


}
