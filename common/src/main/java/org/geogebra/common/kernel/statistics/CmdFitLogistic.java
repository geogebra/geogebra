package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdOneListFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * FitLogistic[&lt;List of Points&gt;]
 * 
 * @author Hans-Petter Ulven
 * @version 15.11.08
 */
public class CmdFitLogistic extends CmdOneListFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFitLogistic(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b) {
		AlgoFitLogistic algo = new AlgoFitLogistic(cons, b);
		algo.getFitLogistic().setLabel(a);
		return algo.getFitLogistic();
	}

}
