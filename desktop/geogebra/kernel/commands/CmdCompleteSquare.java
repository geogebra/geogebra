package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunction;


class CmdCompleteSquare extends CommandProcessor {
	
	public CmdCompleteSquare (Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
     int n = c.getArgumentNumber();
     boolean[] ok = new boolean[n];
     GeoElement[] arg;
     arg = resArgs(c);
     
     switch (n) {
         case 1 :             
        	 if (ok[0] = (arg[0].isGeoFunction())) {
	                 GeoElement[] ret =
	                 { kernel.CompleteSquare(c.getLabel(), (GeoFunction) arg[0] )};
	             return ret;                
	         }                        
              else
            	 throw argErr(app, c.getName(), arg[0]);         
			 
	     // more than one argument
         default :
            	 throw argNumErr(app, c.getName(), n);
     }
 }    
}
