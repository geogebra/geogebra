package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoIteration;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.MyError;

/**
 * Iteration[ <function>, <start>, <n> ]
 */
public class CmdIteration extends CommandProcessor {
	
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIteration(Kernel kernel) {
		super(kernel);
	}

	
@Override
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
            	
        		AlgoIteration algo = new AlgoIteration(cons, c.getLabel(),
                        (GeoFunction) arg[0],
                        (NumberValue) arg[1],
                        (NumberValue) arg[2]);
 
            	GeoElement[] ret = {  algo.getResult() };
                   return ret; 
               }
		throw argErr(app, c.getName(), getBadArg(ok,arg));                   		    		     

        default :
            throw argNumErr(app, c.getName(), n);
    }
}
}