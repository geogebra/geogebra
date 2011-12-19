package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.AbstractKernel;

/*
 * Shuffle[ <List> ]
 */
public class CmdRandomElement extends CmdOneListFunction {

	public CmdRandomElement(AbstractKernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.RandomElement(a, b);
	}


}
