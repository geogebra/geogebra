package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.kernel.Kernel;

/*
 * GeometricMean[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
class CmdGeometricMean extends CmdOneListFunction {

	public CmdGeometricMean(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.GeometricMean(a, b);
	}


}
