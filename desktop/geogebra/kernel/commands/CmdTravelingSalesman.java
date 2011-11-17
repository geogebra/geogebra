package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

class CmdTravelingSalesman extends CmdOneListFunction {

	public CmdTravelingSalesman(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.TravelingSalesman(a, b);
	}

}
