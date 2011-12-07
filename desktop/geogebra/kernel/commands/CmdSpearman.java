package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;

class CmdSpearman extends CmdOneOrTwoListsFunction {

	public CmdSpearman(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Spearman(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernel.Spearman(a, b, c);
	}


}
