package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;

/*
 * SigmaXX[ list ]
 * adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
class CmdSigmaXX extends CmdOneListFunction {

	public CmdSigmaXX(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SigmaXX(a, b);
	}
}
