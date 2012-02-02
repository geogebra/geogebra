package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;

public class CmdDelauneyTriangulation extends CmdOneListFunction {

	public CmdDelauneyTriangulation(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.DelauneyTriangulation(a, b);
	}

}
