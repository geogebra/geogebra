package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;

/** 
 * FitLogistic[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 15.11.08
 */
class CmdFitLogistic extends CmdOneListFunction{

    public CmdFitLogistic(Kernel kernel) {super(kernel);}
    
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.FitLogistic(a, b);
	}

}// class CmdFitLogistic
