package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/** 
 * FitSin[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 15.11.08
 */
public class CmdFitSin extends CmdOneListFunction{
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
    public CmdFitSin(Kernel kernel) {super(kernel);}
    
	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.FitSin(a, b);
	}

}// class CmdFitSin
