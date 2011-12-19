package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.AbstractKernel;

/*
 * Reverse[ <List> ]
 * Michael Borcherds 2008-02-16
 */
public class CmdReverse extends CmdOneListFunction {

	public CmdReverse(AbstractKernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.Reverse(a, b);
	}


}
