package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdOneListFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * HarmonicMean[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
public class CmdHarmonicMean extends CmdOneListFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdHarmonicMean(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoHarmonicMean algo = new AlgoHarmonicMean(cons, a, b);
		return algo.getResult();
	}


}
