package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.AbstractKernel;

/*
 * Mode[ <List> ]
 */
public class CmdMode extends CmdOneListFunction {

	public CmdMode(AbstractKernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.Mode(a, b);
	}

}
