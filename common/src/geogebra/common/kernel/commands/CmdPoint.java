package geogebra.common.kernel.commands;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.algos.AlgoPointVector;
import geogebra.common.kernel.algos.AlgoPointsFromList;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.main.MyError;


/**
 * Point[ <Path> ] Point[ <Point>, <Vector> ]
 */
public class CmdPoint extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdPoint (Kernel kernel) {
		super(kernel);
	}
	
@Override
public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            // need to check isGeoList first as {1,2} can be a Path but we want Point[{1,2}] to create a point
            if ((arg[0].isGeoList() && 
            		((GeoList)arg[0]).getGeoElementForPropertiesDialog().isGeoNumeric())) {
            	
        		AlgoPointsFromList algo = new AlgoPointsFromList(cons, c.getLabels(), !cons.isSuppressLabelsActive(), (GeoList) arg[0]);

                GeoElement[] ret = algo.getPoints();
            
                return ret;
            } else if (arg[0].isPath()) {
                GeoElement[] ret =
                    { getAlgoDispatcher().Point(c.getLabel(), (Path) arg[0], null)};
                return ret;
            } else 
				throw argErr(app, c.getName(), arg[0]);

        case 2 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isPath()))
                    && (ok[1] = (arg[1].isNumberValue()))) {
                    GeoElement[] ret =
                        {
                    		getAlgoDispatcher().Point(
                                c.getLabel(),
                                (Path) arg[0],
                                (NumberValue) arg[1])};
                    return ret;
                }
            else if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoVector()))) {
            	
        		AlgoPointVector algo = new AlgoPointVector(cons, c.getLabel(),
                        (GeoPoint) arg[0],
                        (GeoVector) arg[1]);

                GeoElement[] ret =
                    {algo.getQ()};
                return ret;
            } else {                
                if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
            }

        default :
            throw argNumErr(app, c.getName(), n);
    }
}
}