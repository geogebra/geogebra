package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * abstract class for Commands with one list argument eg Mean[ <List> ]
 * 
 * if more than one argument, then they are put into a list
 * 
 * Michael Borcherds 2008-04-12
 */
public abstract class CmdOneOrTwoListsFunction extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdOneOrTwoListsFunction(Kernel kernel) {
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
				GeoElement[] ret = { doCommand(c.getLabel(), (GeoList) arg[0]) };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);

		case 2:
			arg = resArgs(c);
			if ((arg[0].isGeoList()) && (arg[1].isGeoList())) {
				GeoElement[] ret = { doCommand(c.getLabel(), (GeoList) arg[0],
						(GeoList) arg[1]) };
				return ret;

			} else if (!(arg[0] instanceof VectorValue && arg[1] instanceof VectorValue))
				throw argErr(app, c.getName(), arg[0]);

		default:

			if (arg[0] instanceof VectorValue) {
				// try to create list of points (eg FitExp[])
				GeoList list = wrapInList(kernelA, arg, arg.length,
						GeoClass.POINT);
				if (list != null) {
					GeoElement[] ret = { doCommand(c.getLabel(), list) };
					return ret;
				}
			}
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * Perform the actual command
	 * 
	 * @param label
	 *            label for output
	 * @param list
	 *            input list
	 * @return resulting element
	 */
	abstract protected GeoElement doCommand(String label, GeoList list);

	/**
	 * Perform the actual command
	 * 
	 * @param label
	 *            label for output
	 * @param listX
	 *            first input list
	 * @param listY
	 *            second input list
	 * @return resulting element
	 */
	abstract protected GeoElement doCommand(String label, GeoList listX,
			GeoList listY);
}
