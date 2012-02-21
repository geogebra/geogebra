package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;

/** 
 * FitLogistic[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 15.11.08
 */
public class CmdFitLogistic extends CmdOneListFunction{

    public CmdFitLogistic(Kernel kernel) {super(kernel);}
    
	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.FitLogistic(a, b);
	}

}// class CmdFitLogistic
