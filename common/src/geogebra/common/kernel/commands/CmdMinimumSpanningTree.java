package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;

public class CmdMinimumSpanningTree extends CmdOneListFunction {

	public CmdMinimumSpanningTree(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.MinimumSpanningTree(a, b);
	}

}
