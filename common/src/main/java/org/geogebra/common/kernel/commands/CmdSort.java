package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoSort;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * Sort[ <List> ]
 */
public class CmdSort extends CommandProcessor {

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSort(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		switch (n) {
		case 0:
			throw argNumErr(app, c.getName(), n);
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { Sort(c.getLabel(), (GeoList) arg[0]) };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);

		case 2:
			arg = resArgs(c);
			if ((arg[0].isGeoList()) && (arg[1].isGeoList())) {

				AlgoSort algo = new AlgoSort(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;

			} else if (!(arg[0] instanceof VectorValue && arg[1] instanceof VectorValue))
				throw argErr(app, c.getName(), arg[0]);

		default:

			// try to create list of points (eg FitExp[])
			GeoList list = wrapInList(kernelA, arg, arg.length, GeoClass.POINT);
			if (list != null) {
				GeoElement[] ret = { Sort(c.getLabel(), list) };
				return ret;
			}

			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * Sort[list] Michael Borcherds
	 */
	final private GeoList Sort(String label, GeoList list) {
		AlgoSort algo = new AlgoSort(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}

}
