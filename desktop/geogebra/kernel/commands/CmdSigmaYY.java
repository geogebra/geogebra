package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;

/*
 * SigmaYY[ list ]
 * adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
class CmdSigmaYY extends CmdOneListFunction {

	public CmdSigmaYY(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SigmaYY(a, b);
	}

}
