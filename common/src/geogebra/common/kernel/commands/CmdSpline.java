package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoSpline;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoSpline;
import geogebra.common.main.MyError;

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
			if (arg[0].isGeoList() && ((GeoList) arg[0]).size() > 2
					&& arePoint((GeoList) arg[0])) {
				AlgoSpline algo = new AlgoSpline(cons, c.getLabel(),
						(GeoList) arg[0],4);
				GeoSpline list = algo.getSpline();
				GeoElement[] ret = { list };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
		case 2:
			arg = resArgs(c);
			if (arg[0].isGeoList() && ((GeoList) arg[0]).size() > 2
					&& arePoint((GeoList) arg[0])) {
				int grade = (int) c.getArgument(1).evaluateNum().getNumber()
						.getDouble();
				if (Double.isNaN(grade) || grade > ((GeoList) arg[0]).size() ) {
					throw argErr(app, c.getName(), c.getArgument(1));
				}
				AlgoSpline algo = new AlgoSpline(cons, c.getLabel(),
						(GeoList) arg[0],grade+1);
				GeoSpline list = algo.getSpline();
				GeoElement[] ret = { list };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
		default:
			throw argNumErr(app, c.getName(), n);
		}
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
