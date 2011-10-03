package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.MyError;

/**
 * Sequence[ <expression>, <number-var>, <from>, <to> ]
 * Sequence[ <expression>, <number-var>, <from>, <to>, <step> ]  
 * Sequence[ <number-var>]
 */
public class CmdSequence extends CommandProcessor {
	/**
	 * Creates new sequence command
	 * @param kernel
	 */
	public CmdSequence(Kernel kernel) {
		super(kernel);
	}

	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    
    // avoid "Command Sequence not known eg Sequence[If[Element[list1,i]=="b",0,1]]
    if (n != 4 && n != 5 && n!=1)
    	throw argNumErr(app, c.getName(), n);

    boolean[] ok = new boolean[n];
 
    // create local variable at position 1 and resolve arguments
    GeoElement[] arg;
    if(n>1)
    	 arg = resArgsLocalNumVar(c, 1, 2);
    else
    	 arg = resArgs(c);
    switch (n) {
    	case 1:if (ok[0] = arg[0].isGeoNumeric()){
    		return  kernel.Sequence(c.getLabel(),(GeoNumeric) arg[0]);    		
    	}    	
    	throw argErr(app, c.getName(), arg[0]);
    	case 4 :
            if ((ok[0] = arg[0].isGeoElement())
               	 && (ok[1] = arg[1].isGeoNumeric())
               	 && (ok[2] = arg[2].isNumberValue())
               	 && (ok[3] = arg[3].isNumberValue()))
               {
                   return  kernel.Sequence(
                                c.getLabel(),
                                arg[0],
                                (GeoNumeric) arg[1],
                                (NumberValue) arg[2],
                                (NumberValue) arg[3],
                                null);
               } else {          
               	for (int i=0; i < n; i++) {
               		if (!ok[i]) throw argErr(app, c.getName(), arg[i]);	
               	}            	
               }               
    		    		
        case 5 :        	        	                           
            if ((ok[0] = arg[0].isGeoElement())
            	 && (ok[1] = arg[1].isGeoNumeric())
            	 && (ok[2] = arg[2].isNumberValue())
            	 && (ok[3] = arg[3].isNumberValue())
            	 && (ok[4] = arg[4].isNumberValue()) )
            {
                return kernel.Sequence(
                             c.getLabel(),
                             arg[0],
                             (GeoNumeric) arg[1],
                             (NumberValue) arg[2],
                             (NumberValue) arg[3],
                             (NumberValue) arg[4]);
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