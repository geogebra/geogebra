package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoTableText;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * TableText[<Matrix>],TableText[<Matrix>,<Point>]
 */

public class CmdTableText extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTableText(Kernel kernel) {
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
			if ((arg[0].isGeoList())) {
				GeoList list = (GeoList) arg[0];

				if (list.size() == 0 || list.get(0).isGeoList()) { // list of
																	// lists: no
																	// need to
																	// wrap
					GeoElement[] ret = { tableText(c.getLabel(),
							(GeoList) arg[0], null) };
					return ret;
				}
				list = wrapInList(kernelA, arg, arg.length, GeoClass.DEFAULT);
				if (list != null) {
					GeoElement[] ret = { tableText(c.getLabel(), list, null) };
					return ret;
				}
				throw argErr(app, c.getName(), arg[0]);
			}
			throw argErr(app, c.getName(), arg[0]);

		case 2:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoText())) {
				GeoList list = (GeoList) arg[0];

				if (list.size() == 0 || list.get(0).isGeoList()) { // list of
																	// lists: no
																	// need to
																	// wrap
					GeoElement[] ret = { tableText(c.getLabel(),
							(GeoList) arg[0], (GeoText) arg[1]) };
					return ret;
				}
				list = wrapInList(kernelA, arg, arg.length - 1,
						GeoClass.DEFAULT);
				if (list != null) {
					GeoElement[] ret = { tableText(c.getLabel(), list,
							(GeoText) arg[1]) };
					return ret;
				}
				throw argErr(app, c.getName(), arg[0]);
			}
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoList())) {
				// two lists, no alignment
				GeoList list = wrapInList(kernelA, arg, arg.length,
						GeoClass.DEFAULT);
				if (list != null) {
					GeoElement[] ret = { tableText(c.getLabel(), list, null) };
					return ret;
				}

			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		case 0:
			throw argNumErr(app, c.getName(), n);

		default:
			// try to create list of numbers
			GeoList list;
			if (arg[arg.length - 1].isGeoText()) {
				list = wrapInList(kernelA, arg, arg.length - 1,
						GeoClass.DEFAULT);
				if (list != null) {
					GeoElement[] ret = { tableText(c.getLabel(), list,
							(GeoText) arg[arg.length - 1]) };
					return ret;
				}
			} else {
				list = wrapInList(kernelA, arg, arg.length, GeoClass.DEFAULT);
				if (list != null) {
					GeoElement[] ret = { tableText(c.getLabel(), list, null) };
					return ret;
				}
			}
			throw argErr(app, c.getName(), arg[0]);
		}
	}

	/**
	 * Table[list] Michael Borcherds
	 * 
	 * @param label
	 *            label for output
	 * @param list
	 *            input matrix
	 * @param args
	 *            matrix parameters
	 * @return table text
	 */
	final public GeoText tableText(String label, GeoList list, GeoText args) {
		AlgoTableText algo = new AlgoTableText(cons, label, list, args);
		GeoText text = algo.getResult();
		return text;
	}
}
