package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;

/** 
 * FitSin[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 15.11.08
 */
class CmdFitSin extends CmdOneListFunction{

    public CmdFitSin(Kernel kernel) {super(kernel);}
    
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.FitSin(a, b);
	}

}// class CmdFitSin
