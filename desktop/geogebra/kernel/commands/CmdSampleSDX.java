package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;

class CmdSampleSDX extends CmdOneOrTwoListsFunction {

	public CmdSampleSDX(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SampleSDX(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return null;
	}


}
