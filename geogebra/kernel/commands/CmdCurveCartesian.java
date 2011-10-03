package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.MyError;

/*
 * Curve[ <x-coord expression>,  <y-coord expression>, <number-var>, <from>, <to> ]  
 */
public class CmdCurveCartesian extends CommandProcessor {
	
	public CmdCurveCartesian(Kernel kernel) {
		super(kernel);
	}

	
public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
 
    switch (n) {
    	// Curve[ <x-coord expression>,  <y-coord expression>, <number-var>, <from>, <to> ] 
    	// Note: x and y coords are numbers dependent on number-var
    	case 5 :
    	    // create local variable at position 2 and resolve arguments
    	    GeoElement[] arg = resArgsLocalNumVar(c, 2, 3);      

    	    if ((ok[0] = arg[0].isNumberValue())
            	 && (ok[1] = arg[1].isNumberValue())
               	 && (ok[2] = arg[2].isGeoNumeric())
               	 && (ok[3] = arg[3].isNumberValue())
               	 && (ok[4] = arg[4].isNumberValue()))
               {
            	   GeoElement [] ret = new GeoElement[1];
                   ret[0] = kernel.CurveCartesian(
                                c.getLabel(),
                                (NumberValue) arg[0],
                                (NumberValue) arg[1],
                                (GeoNumeric) arg[2],
                                (NumberValue) arg[3],
                                (NumberValue) arg[4]);
                   return ret;
               } else {          
               	for (int i=0; i < n; i++) {
               		if (!ok[i]) throw argErr(app, "CurveCartesian", arg[i]);	
               	}            	
               }                   	  

        default :
            throw argNumErr(app, "CurveCartesian", n);
    }
}
}