package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoSpline;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;

/**
 * 
 * Spline [<list of points>]
 * 
 * @author Giuliano Bellucci
 * 
 */
public class CmdSpline extends CommandProcessor {

	/**
	 * @param kernel kernel
	 */
	public CmdSpline(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		switch (n) {
		case 0:
			throw argNumErr(app, c.getName(), n);
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoList() 
					&& arePoint((GeoList) arg[0])) {				
				GeoElement[] ret = { Spline(c.getLabel(), (GeoList) arg[0]) };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
		case 2:
			arg = resArgs(c);
			if (arg[0].isGeoList() 
					&& arePoint((GeoList) arg[0])) {
				int degree = (int) c.getArgument(1).evaluateDouble();
				if (Double.isNaN(degree) || degree > ((GeoList) arg[0]).size() || degree < 3) {
					throw argErr(app, c.getName(), c.getArgument(1));
				}
				GeoNumberValue degreeNum=(GeoNumberValue) arg[1]; 
				AlgoSpline algo = new AlgoSpline(cons, c.getLabel(),
						(GeoList) arg[0],degreeNum);
				GeoCurveCartesian list = algo.getSpline();
				GeoElement[] ret = { list };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
		default:
			GeoList list = wrapInList(kernelA, arg, arg.length,
					GeoClass.POINT);
			if (list != null) {
				GeoElement[] ret = { Spline(c.getLabel(), list) };
				return ret;
			}

			throw argNumErr(app, c.getName(), n);
		}
	}

	private GeoCurveCartesian Spline(String label, GeoList list) {
		AlgoSpline algo = new AlgoSpline(cons, label,
				list,new GeoNumeric(cons,3));
		return algo.getSpline();
	}

	private static boolean arePoint(GeoList geoList) {
		for (int i = 0; i < geoList.size()-1; i++) {
			if (!geoList.get(i).isGeoPoint() || geoList.get(i).isEqual(geoList.get(i+1))) {
				return false;
			}
		}
		return true;
	}

}
