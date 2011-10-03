package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;

class CmdTravelingSalesman extends CmdOneListFunction {

	public CmdTravelingSalesman(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.TravelingSalesman(a, b);
	}

}
