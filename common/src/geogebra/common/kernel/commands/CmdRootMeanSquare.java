package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;

/*
 * HarmonicMean[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
public class CmdRootMeanSquare extends CmdOneListFunction {

	public CmdRootMeanSquare(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.RootMeanSquare(a, b);
	}


}
