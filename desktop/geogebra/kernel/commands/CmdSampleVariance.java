package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

/*
 * SampleVariance[ list ]
 * adapted from CmdSum by Michael Borcherds 2008-02-16
 */
class CmdSampleVariance extends CmdOneListFunction {

	public CmdSampleVariance(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SampleVariance(a, b);
	}
	

}
