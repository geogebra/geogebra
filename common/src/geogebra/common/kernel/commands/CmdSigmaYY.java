package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/*
 * SigmaYY[ list ]
 * adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
public class CmdSigmaYY extends CmdOneListFunction {

	public CmdSigmaYY(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.SigmaYY(a, b);
	}

}
