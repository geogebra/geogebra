package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

/*
 * PointList[ <List> ]
 */
class CmdRootList extends CmdOneListFunction {

	public CmdRootList(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.RootList(a, b);
	}


}
