package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * SampleVariance[ list ]
 * adapted from CmdSum by Michael Borcherds 2008-02-16
 */
public class CmdSampleVariance extends CmdOneListFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSampleVariance(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.SampleVariance(a, b);
	}
	
	@Override
	protected GeoElement doCommand(String a, Command c, GeoList list, GeoList freq) {
		return kernelA.SampleVariance(a, list, freq);
	}
}
