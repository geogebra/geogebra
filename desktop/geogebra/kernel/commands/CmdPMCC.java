package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

class CmdPMCC extends CmdOneOrTwoListsFunction {

	public CmdPMCC(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.PMCC(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernel.PMCC(a, b, c);
	}


}
