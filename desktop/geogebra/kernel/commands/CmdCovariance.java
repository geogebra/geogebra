package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;

class CmdCovariance extends CmdOneOrTwoListsFunction {

	public CmdCovariance(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Covariance(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernel.Covariance(a, b, c);
	}


}
