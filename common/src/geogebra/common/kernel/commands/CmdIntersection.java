package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;
import geogebra.common.kernel.AbstractKernel;


/*
 * Intersection[ <GeoList>, <GeoList> ]
 */
public class CmdIntersection extends CommandProcessor {
	
	public CmdIntersection(AbstractKernel kernel) {
		super(kernel);
	}
	
public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);
            if (arg[0].isGeoList() && arg[1].isGeoList() ) {
				GeoElement[] ret = { 
						kernelA.Intersection(c.getLabel(),
						(GeoList) arg[0], (GeoList)arg[1] ) };
				return ret;
			} 
            
			else {
                if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
                else
                    throw argErr(app, c.getName(), arg[1]);
            }


        default :
            throw argNumErr(app, c.getName(), n);
    }
}
}