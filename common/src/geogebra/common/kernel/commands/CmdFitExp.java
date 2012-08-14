package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoFitExp;

/** 
 * FitExp[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 12.04.08
 */
public class CmdFitExp extends CmdOneListFunction{

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFitExp(Kernel kernel) {super(kernel);}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoFitExp algo = new AlgoFitExp(cons, a, b);
		return algo.getFitExp();
	}

}// class CmdFitExp
