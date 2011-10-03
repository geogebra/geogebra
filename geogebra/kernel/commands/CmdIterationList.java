package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.MyError;

/*
 * IterationList[ <function>, <start>, <n> ]
 */
public class CmdIterationList extends CommandProcessor {
	
	public CmdIterationList(Kernel kernel) {
		super(kernel);
	}

	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n]; 
    GeoElement[] arg;
    
    switch (n) {    	
    	case 3 :
    		arg = resArgs(c);
            if ((ok[0] = arg[0].isGeoFunction())
               	 && (ok[1] = arg[1].isNumberValue())
               	 && (ok[2] = arg[2].isNumberValue()))
               {
            	GeoElement[] ret = {  kernel.IterationList(
                                c.getLabel(),
                                (GeoFunction) arg[0],
                                (NumberValue) arg[1],
                                (NumberValue) arg[2]) };
                   return ret; 
               } else {          
               	for (int i=0; i < n; i++) {
               		if (!ok[i]) throw argErr(app, c.getName(), arg[i]);	
               	}            	
               }                   		    		     

        default :
            throw argNumErr(app, c.getName(), n);
    }
}
}