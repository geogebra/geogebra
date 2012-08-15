package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoStandardDeviation;

/**
 * SD[ list ]
 * adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
public class CmdSD extends CmdOneListFunction {

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdSD(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoStandardDeviation algo = new AlgoStandardDeviation(cons, a,
				b);
		return algo.getResult();
	}

	@Override
	protected GeoElement doCommand(String a, Command c, GeoList list, GeoList freq) {
		AlgoStandardDeviation algo = new AlgoStandardDeviation(cons, a,
				list, freq);
		return algo.getResult();
	}
}
