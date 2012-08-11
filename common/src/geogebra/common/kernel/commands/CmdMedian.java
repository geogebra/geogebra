package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * Median[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
public class CmdMedian extends CmdOneListFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMedian(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.Median(a, b);
	}
	
	@Override
	final protected GeoElement doCommand(String a, Command c, GeoList list, GeoList freq)
	{
		return kernelA.Median(a, list, freq);
	}


}
