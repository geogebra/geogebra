package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;

/** 
 * FitGrowth[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 2010-02-25
 */
public class CmdFitGrowth extends CmdOneListFunction{

    public CmdFitGrowth(Kernel kernel) {super(kernel);}
    
	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.FitGrowth(a, b);
	}
}//class CmdFitGrowth
