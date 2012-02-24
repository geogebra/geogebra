package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;

/**
 * Reverse[ <List> ]
 * Michael Borcherds 2008-02-16
 */
public class CmdReverse extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdReverse(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.Reverse(a, b);
	}


}
