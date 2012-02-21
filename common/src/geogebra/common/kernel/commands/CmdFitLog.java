package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;

/** 
 * FitLog[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 12.04.08
 */

public class CmdFitLog extends CmdOneListFunction{

    public CmdFitLog(Kernel kernel) {super(kernel);}
    
	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.FitLog(a, b);
	}

}// class CmdFitLog
