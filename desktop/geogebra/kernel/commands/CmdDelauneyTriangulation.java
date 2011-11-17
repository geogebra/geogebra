package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

class CmdDelauneyTriangulation extends CmdOneListFunction {

	public CmdDelauneyTriangulation(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.DelauneyTriangulation(a, b);
	}

}
