package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;

class CmdMinimumSpanningTree extends CmdOneListFunction {

	public CmdMinimumSpanningTree(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.MinimumSpanningTree(a, b);
	}

}
