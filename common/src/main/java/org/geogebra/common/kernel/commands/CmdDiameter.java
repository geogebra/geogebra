package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/**
 * Diameter[ <GeoVector>, <GeoConic> ] Diameter[ <GeoLine>, <GeoConic> ]
 */
public class CmdDiameter extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDiameter(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// diameter line conjugate to vector relative to conic
			if ((ok[0] = (arg[0].isGeoVector()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = { diameter(c.getLabel(),
						(GeoVectorND) arg[0], (GeoConicND) arg[1]) };
				return ret;
			}

			// diameter line conjugate to line relative to conic
			if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = { diameter(c.getLabel(), (GeoLineND) arg[0],
						(GeoConicND) arg[1]) };
				return ret;
			}
			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param l
	 *            line
	 * @param c
	 *            conic
	 * @return diameter line
	 */
	protected GeoElement diameter(String label, GeoLineND l, GeoConicND c) {
		return getAlgoDispatcher().DiameterLine(label, l, c);
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param v
	 *            vector
	 * @param c
	 *            conic
	 * @return diameter line
	 */
	protected GeoElement diameter(String label, GeoVectorND v, GeoConicND c) {
		return getAlgoDispatcher().DiameterLine(label, v, c);
	}
}
