package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * GeometricMean[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
public class CmdGeometricMean extends CmdOneListFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdGeometricMean(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.GeometricMean(a, b);
	}


}
