package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;

/*
 * Transpose[ <List> ]
 * Michael Borcherds 
 */
class CmdTranspose extends CmdOneListFunction {

	public CmdTranspose(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Transpose(a, b);
	}

}
