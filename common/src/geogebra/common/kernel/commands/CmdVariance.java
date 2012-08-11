package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * Variance[ list ] or Variance[ list, frequency ] 
 * adapted from CmdSum by Michael Borcherds 2008-02-16
 */
public class CmdVariance extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdVariance(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.Variance(a, b);
	}
	
	@Override
	protected GeoElement doCommand(String a, Command c, GeoList list, GeoList freq) {
		return kernelA.Variance(a, list, freq);
	}

}
