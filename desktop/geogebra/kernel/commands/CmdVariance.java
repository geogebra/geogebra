package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;

/*
 * Variance[ list ]
 * adapted from CmdSum by Michael Borcherds 2008-02-16
 */
class CmdVariance extends CmdOneListFunction {

	public CmdVariance(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Variance(a, b);
	}
	

}
