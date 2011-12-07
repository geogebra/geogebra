package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;

class CmdSDX extends CmdOneOrTwoListsFunction {

	public CmdSDX(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SDX(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return null;
	}

}
