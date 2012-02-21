package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;


/*
 * Invert[ <List> ]
 * Michael Borcherds 
 */
public class CmdInvert extends CmdOneListFunction {

	public CmdInvert(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.Invert(a, b);
	}


}
