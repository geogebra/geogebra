package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
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
