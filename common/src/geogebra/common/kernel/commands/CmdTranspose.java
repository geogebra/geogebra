package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/*
 * Transpose[ <List> ]
 * Michael Borcherds 
 */
public class CmdTranspose extends CmdOneListFunction {

	public CmdTranspose(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.Transpose(a, b);
	}

}
