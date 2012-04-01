package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * ReducedRowEchelonForm[ <List> ]
 * Michael Borcherds 
 */
public class CmdReducedRowEchelonForm extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdReducedRowEchelonForm(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.ReducedRowEchelonForm(a, b);
	}

}
