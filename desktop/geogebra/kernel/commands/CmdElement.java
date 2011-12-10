package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/*
 * Element[ <list>, <n> ]
 * Element[ <point>, <n> ]
 */
public class CmdElement extends CommandProcessorDesktop {

	public CmdElement(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 0:
		case 1:
			throw argNumErr(app, c.getName(), n);
		case 2:
			arg = resArgs(c);
			// list
			if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.Element(c.getLabel(),
						(GeoList) arg[0], (NumberValue) arg[1]) };
				return ret;
			}

			// error
			else {
				for (int i = 0; i < n; i++) {
					if (!ok[i])
						throw argErr(app, c.getName(), arg[i]);
				}
			}
		default:
			arg = resArgs(c);
			// list
			NumberValue[] nvs = new NumberValue[n-1];
			if (!arg[0].isGeoList())
				throw argErr(app, c.getName(), arg[0]);
			for (int i = 1; i < n; i++) {
				if (arg[i].isNumberValue())
					nvs[i - 1] = (NumberValue) arg[i];
				else
					throw argErr(app, c.getName(), arg[i]);
			}

			GeoElement[] ret = { kernel.Element(c.getLabel(), (GeoList) arg[0],
					nvs) };
			return ret;
		}

	}
}
