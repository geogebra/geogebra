package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoTableText;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;

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

				if (list.size() == 0)
					throw argErr(app, c.getName(), arg[0]);

				if (list.get(0).isGeoList()) { // list of lists: no need to wrap
					GeoElement[] ret = { TableText(c.getLabel(),
							(GeoList) arg[0], null) };
					return ret;
				}
				list = wrapInList(kernelA, arg, arg.length, GeoClass.DEFAULT);
				if (list != null) {
					GeoElement[] ret = { TableText(c.getLabel(),
							list, null) };
					return ret;
				}
				throw argErr(app, c.getName(), arg[0]);
			}
			throw argErr(app, c.getName(), arg[0]);

		case 2:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoText())) {
				GeoList list = (GeoList) arg[0];

				if (list.size() == 0)
					throw argErr(app, c.getName(), arg[0]);

				if (list.get(0).isGeoList()) { // list of lists: no need to wrap
					GeoElement[] ret = { TableText(c.getLabel(),
							(GeoList) arg[0], (GeoText) arg[1]) };
					return ret;
				}
				list = wrapInList(kernelA, arg, arg.length - 1, GeoClass.DEFAULT);
				if (list != null) {
					GeoElement[] ret = { TableText(c.getLabel(),
							list, (GeoText) arg[1]) };
					return ret;
				}
				throw argErr(app, c.getName(), arg[0]);
			}
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoList())) {
				// two lists, no alignment
				GeoList list = wrapInList(kernelA, arg, arg.length, GeoClass.DEFAULT);
				if (list != null) {
					GeoElement[] ret = { TableText(c.getLabel(), list,
							null) };
					return ret;
				}

			} 
			throw argErr(app, c.getName(), getBadArg(ok,arg));
			

		case 0:
			throw argNumErr(app, c.getName(), n);

		default:
			// try to create list of numbers
			GeoList list;
			if (arg[arg.length - 1].isGeoText()) {
				list = wrapInList(kernelA, arg, arg.length - 1, GeoClass.DEFAULT);
				if (list != null) {
					GeoElement[] ret = { TableText(c.getLabel(), list,
							(GeoText) arg[arg.length - 1]) };
					return ret;
				}
			} else {
				list = wrapInList(kernelA, arg, arg.length, GeoClass.DEFAULT);
				if (list != null) {
					GeoElement[] ret = { TableText(c.getLabel(), list,
							null) };
					return ret;
				}
			}
			throw argErr(app, c.getName(), arg[0]);
		}
	}
	


	/**
	 * Table[list] Michael Borcherds
	 */
	final public GeoText TableText(String label, GeoList list, GeoText args) {
		AlgoTableText algo = new AlgoTableText(cons, label, list, args);
		GeoText text = algo.getResult();
		return text;
	}
}
