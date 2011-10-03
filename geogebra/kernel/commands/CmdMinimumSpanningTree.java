package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;

class CmdMinimumSpanningTree extends CmdOneListFunction {

	public CmdMinimumSpanningTree(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.MinimumSpanningTree(a, b);
	}

}
