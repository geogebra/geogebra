package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.kernel.Kernel;

/*
 * 
 * FitLineX[list of points]
 * adapted from CmdLcm by Michael Borcherds 2008-01-14
 */
class CmdFitLineX extends CmdOneListFunction {

	public CmdFitLineX(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.FitLineX(a, b);
	}


}
