package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoDotPlot;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * DotPlot[ <List of Numeric> ] G.Sturr 2010-8-10
 */
public class CmdDotPlot extends CmdOneListFunction {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDotPlot(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b) {
		AlgoDotPlot algo = new AlgoDotPlot(cons, a, b);
		return algo.getResult();
	}

}
