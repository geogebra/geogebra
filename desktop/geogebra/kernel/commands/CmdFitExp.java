package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;

/** 
 * FitExp[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 12.04.08
 */
class CmdFitExp extends CmdOneListFunction{

    public CmdFitExp(Kernel kernel) {super(kernel);}
    
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.FitExp(a, b);
	}

}// class CmdFitExp
