package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

/*
 * Mean[ list ]
 *  Michael Borcherds 2008-04-12
 */
class CmdMean extends CmdOneListFunction {

	public CmdMean(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Mean(a, b);
	}

}
