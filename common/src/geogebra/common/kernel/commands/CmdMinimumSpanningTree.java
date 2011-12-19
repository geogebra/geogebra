package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.AbstractKernel;

public class CmdMinimumSpanningTree extends CmdOneListFunction {

	public CmdMinimumSpanningTree(AbstractKernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.MinimumSpanningTree(a, b);
	}

}
