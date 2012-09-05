package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoFitLineY;
import geogebra.common.kernel.commands.CmdOneListFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * 
 * FitLineY[list of points]
 * adapted from CmdLcm by Michael Borcherds 2008-01-14
 */
public class CmdFitLineY extends CmdOneListFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFitLineY(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoFitLineY algo = new AlgoFitLineY(cons, a, b);
		return algo.getFitLineY();
	}

}
