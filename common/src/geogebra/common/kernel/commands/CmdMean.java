package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.AbstractKernel;

/*
 * Mean[ list ]
 *  Michael Borcherds 2008-04-12
 */
public class CmdMean extends CmdOneListFunction {

	public CmdMean(AbstractKernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.Mean(a, b);
	}

}
