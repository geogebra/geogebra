package geogebra.kernel.commands;

import geogebra.kernel.CasEvaluableFunction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

class CmdFactor extends CommandProcessor {
	
	public CmdFactor (Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
     int n = c.getArgumentNumber();
     boolean[] ok = new boolean[n];
     GeoElement[] arg;
     arg = resArgs(c);
     
     switch (n) {
         case 1 :             
        	 if (ok[0] = (arg[0] instanceof CasEvaluableFunction)) {
	                 GeoElement[] ret =
	                 { kernel.Factor(c.getLabel(), (CasEvaluableFunction) arg[0] )};
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
