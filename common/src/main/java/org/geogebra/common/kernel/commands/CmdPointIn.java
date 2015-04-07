package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * PointIn[ <Region> ]
 * 
 * @version 2010-05-17
 */
public class CmdPointIn extends CommandProcessor {

	/**
	 * Initiates command processor for PointIn command
	 * 
	 * @param kernel
	 *            kernel used for computations
	 */
	public CmdPointIn(Kernel kernel) {
		super(kernel);
	}

	/**
	 * Checks correctness of inputs and runs the command. Last change: correct
	 * error messages (2010-05-17), Zbynek Konecny
	 */
	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;

		if (n == 1) {
			arg = resArgs(c);
			if (arg[0].isRegion()) {
				return pointIn(c.getLabel(), (Region) arg[0]);
			}
			throw argErr(app, c.getName(), arg[0]);
		}

		throw argNumErr(app, c.getName(), n);

	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param region
	 *            region
	 * @return output
	 */
	protected GeoElement[] pointIn(String label, Region region) {
		GeoElement[] ret = { getAlgoDispatcher().PointIn(label, region, 0, 0,
				true, false, true) };
		return ret;

	}
}