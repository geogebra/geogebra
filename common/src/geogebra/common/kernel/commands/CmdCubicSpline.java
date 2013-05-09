package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoCubicSpline;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

/**
 * 
 * CubicSpline [<list of points>]
 * 
 * @author Giuliano Bellucci
 * 
 */
public class CmdCubicSpline extends CommandProcessor {

	/**
	 * @param kernel 
	 */
	public CmdCubicSpline(Kernel kernel) {
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
			if (arg[0].isGeoList() && ((GeoList) arg[0]).size() >= 2
					&& arePoint((GeoList) arg[0])) {
				AlgoCubicSpline algo = new AlgoCubicSpline(cons, c.getLabel(),
						(GeoList) arg[0]);
				GeoList list = algo.getList();
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
