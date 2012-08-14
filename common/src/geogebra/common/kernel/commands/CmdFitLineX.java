package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoFitLineX;

/**
 * 
 * FitLineX[list of points]
 * adapted from CmdLcm by Michael Borcherds 2008-01-14
 */
public class CmdFitLineX extends CmdOneListFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFitLineX(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoFitLineX algo = new AlgoFitLineX(cons, a, b);
		return algo.getFitLineX();
	}


}
