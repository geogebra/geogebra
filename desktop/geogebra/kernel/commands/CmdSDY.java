package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

class CmdSDY extends CmdOneOrTwoListsFunction {

	public CmdSDY(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SDY(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return null;
	}

}
