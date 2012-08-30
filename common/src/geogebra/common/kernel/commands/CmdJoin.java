package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoJoin;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;

/**
 *Join
 */
public class CmdJoin extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdJoin(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			ok[0] = arg[0].isGeoList();

			if (ok[0]) {
				GeoElement[] ret = { Join(c.getLabel(), (GeoList) arg[0]) };
				return ret;
			} else

			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			// try to create list of numbers
			GeoList list = wrapInList(kernelA, arg, arg.length,
					GeoClass.LIST);
			if (list != null) {
				GeoElement[] ret = { Join(c.getLabel(), list) };
				return ret;
			}
			throw argNumErr(app, c.getName(), n);
		}
	}
	
	/**
	 * Join[list,list] Michael Borcherds
	 */
	final private GeoList Join(String label, GeoList list) {
		AlgoJoin algo = new AlgoJoin(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}


}
