package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 *  Mean[ list ] or  Mean[ list, frequency ]
 *  Michael Borcherds 2008-04-12
 */
public class CmdMean extends CmdOneListFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMean(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.Mean(a, b);
	}

	@Override
	protected GeoElement doCommand(String a, Command c, GeoList list, GeoList freq) {
		return kernelA.Mean(a, list, freq);
	}

}
