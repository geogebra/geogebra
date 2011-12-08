package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.kernel.Kernel;

/*
 * ReducedRowEchelonForm[ <List> ]
 * Michael Borcherds 
 */
class CmdReducedRowEchelonForm extends CmdOneListFunction {

	public CmdReducedRowEchelonForm(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.ReducedRowEchelonForm(a, b);
	}

}
