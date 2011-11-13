package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;

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
