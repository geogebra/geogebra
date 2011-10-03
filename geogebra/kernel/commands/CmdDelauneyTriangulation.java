package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;

class CmdDelauneyTriangulation extends CmdOneListFunction {

	public CmdDelauneyTriangulation(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.DelauneyTriangulation(a, b);
	}

}
