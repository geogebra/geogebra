package geogebra.common.kernel.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/*
 * Variance[ list ]
 * adapted from CmdSum by Michael Borcherds 2008-02-16
 */
public class CmdVariance extends CmdOneListFunction {

	public CmdVariance(AbstractKernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.Variance(a, b);
	}
	

}
