package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdOneListFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * FitExp[&lt;List of Points&gt;]
 * 
 * @author Hans-Petter Ulven
 * @version 12.04.08
 */
public class CmdFitExp extends CmdOneListFunction {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFitExp(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b) {
		AlgoFitExp algo = new AlgoFitExp(cons, b);
		algo.getFitExp().setLabel(a);
		return algo.getFitExp();
	}

}
