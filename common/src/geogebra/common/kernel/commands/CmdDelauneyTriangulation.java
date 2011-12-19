package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.AbstractKernel;

public class CmdDelauneyTriangulation extends CmdOneListFunction {

	public CmdDelauneyTriangulation(AbstractKernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.DelauneyTriangulation(a, b);
	}

}
