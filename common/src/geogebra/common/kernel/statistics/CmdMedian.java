package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdOneListFunction;
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
		AlgoMedian algo = new AlgoMedian(cons, a, b);
		return algo.getMedian();
	}
	
	@Override
	final protected GeoElement doCommand(String a, Command c, GeoList list, GeoList freq)
	{
		AlgoMedian algo = new AlgoMedian(cons, a, list, freq);
		return algo.getMedian();
	}


}
