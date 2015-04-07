package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoConicFromCoeffList;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * Conic[ <List> ] Conic[ five GeoPoints ]
 */
public class CmdConic extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdConic(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);
		switch (n) {
		case 1:
			if (arg[0].isGeoList())
				return Conic(c.getLabel(), (GeoList) arg[0]);
		case 5:
			for (int i = 0; i < 5; i++) {
				if (!arg[i].isGeoPoint()) {
					throw argErr(app, c.getName(), arg[i]);
				}
			}
			GeoElement[] ret = { Conic(c.getLabel(), arg) };
			return ret;
		default:
			if (arg[0] instanceof GeoNumberValue) {
				// try to create list of numbers
				GeoList list = wrapInList(kernelA, arg, arg.length,
						GeoClass.NUMERIC);
				if (list != null) {
					ret = Conic(c.getLabel(), list);
					return ret;
				}
			}
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * conic from coefficients
	 * 
	 * @param coeffList
	 * @return
	 */
	final private GeoElement[] Conic(String label, GeoList coeffList) {
		AlgoConicFromCoeffList algo = new AlgoConicFromCoeffList(cons, label,
				coeffList);

		return new GeoElement[] { algo.getConic() };

	}

	/**
	 * @param label
	 *            label
	 * @param arg
	 *            points
	 * @return conic 5 points
	 */
	protected GeoElement Conic(String label, GeoElement[] arg) {
		GeoPoint[] points = { (GeoPoint) arg[0], (GeoPoint) arg[1],
				(GeoPoint) arg[2], (GeoPoint) arg[3], (GeoPoint) arg[4] };
		return getAlgoDispatcher().Conic(label, points);
	}
}
