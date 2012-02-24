package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/** 
 * FitPow[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 07.04.08
 */
public class CmdFitPow extends CmdOneListFunction{
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
    public CmdFitPow(Kernel kernel) {super(kernel);}
    
	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.FitPow(a, b);
	}

}// class CmdFitPow
