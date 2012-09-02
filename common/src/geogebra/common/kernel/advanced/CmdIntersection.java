package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoIntersection;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;


/**
 * Intersection[ <GeoList>, <GeoList> ]
 */
public class CmdIntersection extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIntersection(Kernel kernel) {
		super(kernel);
	}
	
@Override
public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);
            if (arg[0].isGeoList() && arg[1].isGeoList() ) {
            	
        		AlgoIntersection algo = new AlgoIntersection(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList)arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} 
            
			throw argErr(app, c.getName(), getBadArg(ok,arg));


        default :
            throw argNumErr(app, c.getName(), n);
    }
}
}