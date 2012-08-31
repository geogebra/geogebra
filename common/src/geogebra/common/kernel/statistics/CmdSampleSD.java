package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdOneListFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * SampleSD[ list ]
 * adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
public class CmdSampleSD extends CmdOneListFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSampleSD(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoSampleStandardDeviation algo = new AlgoSampleStandardDeviation(
				cons, a, b);
		return algo.getResult();
	}

	@Override
	protected GeoElement doCommand(String a, Command c, GeoList list, GeoList freq) {
		AlgoSampleStandardDeviation algo = new AlgoSampleStandardDeviation(
				cons, a, list, freq);
		return algo.getResult();
	}

}
