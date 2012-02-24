package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;

/**
 * abstract class for Commands with one list argument eg Mean[ <List> ]
 * 
 * if more than one argument, then they are put into a list
 * 
 * Michael Borcherds 2008-04-12
 */
public abstract class CmdOneListFunction extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdOneListFunction(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 0:
			throw argNumErr(app, c.getName(), n);
		case 1:
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						doCommand(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
		
		     // more than one argument
        default :
        	if (arg[0].isNumberValue()) {
	            // try to create list of numbers
	       	 GeoList list = wrapInList(kernelA, arg, arg.length, GeoClass.NUMERIC);
	            if (list != null) {
	           	 GeoElement[] ret = { doCommand(c.getLabel(), list)};
	                return ret;             	     	 
	            } 
        	} else if (arg[0].isVectorValue()) {
                // try to create list of points (eg FitExp[])
              	 GeoList list = wrapInList(kernelA, arg, arg.length, GeoClass.POINT);
                   if (list != null) {
                  	 GeoElement[] ret = { doCommand(c.getLabel(), list)};
                       return ret;             	     	 
                   } 
        		
        	}
			throw argNumErr(app, c.getName(), n);
		}
	}
	
    /**
     * Perform the actual command
     * @param label label for output
     * @param list input list
     * @return resulting element
     */
    abstract protected GeoElement doCommand(String label, GeoList list);     
}
