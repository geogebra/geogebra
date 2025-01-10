package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdOneListFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Mean[ list ] or Mean[ list, frequency ] Michael Borcherds 2008-04-12
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
	final protected GeoElement doCommand(String a, GeoList b) {
		AlgoMean algo = new AlgoMean(cons, b);
		algo.getResult().setLabel(a);
		return algo.getResult();
	}

	@Override
	protected GeoElement doCommand(String label, Command c, GeoList list,
			GeoList freq) {
		AlgoMean algo = new AlgoMean(cons, list, freq);
		algo.getResult().setLabel(label);
		return algo.getResult();
	}

}
