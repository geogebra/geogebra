package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;

/*
 * Shuffle[ <List> ]
 */
public class CmdRandomElement extends CmdOneListFunction {

	public CmdRandomElement(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.RandomElement(a, b);
	}


}
