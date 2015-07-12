package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoNormalize;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * Normalize[ <List> ]
 */
public class CmdNormalize extends CommandProcessor {

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdNormalize(Kernel kernel) {
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

				AlgoNormalize algo = new AlgoNormalize(cons, c.getLabel(),
						(GeoList) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;

			} else if (!(arg[0] instanceof VectorValue))
				throw argErr(app, c.getName(), arg[0]);

		default:

			// try to create list of points (eg FitExp[])
			GeoList list = wrapInList(kernelA, arg, arg.length, GeoClass.POINT);
			if (list != null) {
				GeoElement[] ret = { Normalize(c.getLabel(), list) };
				return ret;
			}

			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * Normalize[list] Oana Niculaescu
	 */
	final private GeoList Normalize(String label, GeoList list) {
		AlgoNormalize algo = new AlgoNormalize(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}

}
