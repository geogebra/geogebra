package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;

/**
 * Sort[ <List> ]
 */
public class CmdSort extends CommandProcessor {

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdSort(Kernel kernel) {
		super(kernel);
	}

	
	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		switch (n) {
		case 0:throw argNumErr(app, c.getName(), n);
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						Sort(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
		
		case 2:			
			arg = resArgs(c);
			if ((arg[0].isGeoList()) &&
				(arg[1].isGeoList())) 
			{
				
				AlgoSort algo = new AlgoSort(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
				
			}  else if(!(arg[0].isVectorValue() && arg[1].isVectorValue()))
				throw argErr(app, c.getName(), arg[0]);
			
		
        default :
        	 
        	
                // try to create list of points (eg FitExp[])
              	 GeoList list = wrapInList(kernelA, arg, arg.length, GeoClass.POINT);
                   if (list != null) {
                  	 GeoElement[] ret = { Sort(c.getLabel(), list)};
                       return ret;             	     	 
                   } 		
        	
			throw argNumErr(app, c.getName(), n);
		}
	}
	
	
	
	/**
	 * Sort[list] Michael Borcherds
	 */
	final private GeoList Sort(String label, GeoList list) {
		AlgoSort algo = new AlgoSort(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}

}
