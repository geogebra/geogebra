package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;

/*
 * 
 * FitLineY[list of points]
 * adapted from CmdLcm by Michael Borcherds 2008-01-14
 */
class CmdFitLineY extends CmdOneListFunction {

	public CmdFitLineY(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.FitLineY(a, b);
	}

}
