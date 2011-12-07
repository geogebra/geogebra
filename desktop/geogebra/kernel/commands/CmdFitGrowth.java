package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;

/** 
 * FitGrowth[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 2010-02-25
 */
class CmdFitGrowth extends CmdOneListFunction{

    public CmdFitGrowth(Kernel kernel) {super(kernel);}
    
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.FitGrowth(a, b);
	}
}//class CmdFitGrowth
