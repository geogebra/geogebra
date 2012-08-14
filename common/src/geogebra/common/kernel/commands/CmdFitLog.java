package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.statistics.AlgoFitLog;

/** 
 * FitLog[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 12.04.08
 */

public class CmdFitLog extends CmdOneListFunction{
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
    public CmdFitLog(Kernel kernel) {super(kernel);}
    
	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		AlgoFitLog algo = new AlgoFitLog(cons, a, b);
		return algo.getFitLog();
	}

}// class CmdFitLog
