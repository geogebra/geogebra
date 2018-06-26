package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdOneListFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Mean absolute deviation MAD[ list ] adapted from CmdVariance by Michael
 * Borcherds 2008-02-18
 */
public class CmdMAD extends CmdOneListFunction {

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMAD(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b) {
		AlgoMeanAbsoluteDeviation algo = new AlgoMeanAbsoluteDeviation(cons, b);
		algo.getResult().setLabel(a);
		return algo.getResult();
	}

	@Override
	protected GeoElement doCommand(String a, Command c, GeoList list,
			GeoList freq) {
		AlgoMeanAbsoluteDeviation algo = new AlgoMeanAbsoluteDeviation(cons, list, freq);
		algo.getResult().setLabel(a);
		return algo.getResult();
	}
}
