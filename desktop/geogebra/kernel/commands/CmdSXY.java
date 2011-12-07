package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;

class CmdSXY extends CmdOneOrTwoListsFunction {

	public CmdSXY(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SXY(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernel.SXY(a, b, c);
	}

}
