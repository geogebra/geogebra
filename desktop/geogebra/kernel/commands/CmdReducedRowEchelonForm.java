package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;

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
