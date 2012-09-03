package geogebra.common.kernel.commands;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;


/**
 * PointIn[ <Region> ] 
 * @version 2010-05-17
 */
public class CmdPointIn extends CommandProcessor {

	/**
	 * Initiates command processor for PointIn command
	 * @param kernel kernel used for computations
	 */
	public CmdPointIn (Kernel kernel) {
		super(kernel);
	}

	/**
	 * Checks correctness of inputs and runs the command.
	 * Last change: correct error messages (2010-05-17), Zbynek Konecny 
	 */
	@Override
	public  GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;

		if (n==1) {
			arg = resArgs(c);
			if (arg[0].isRegion()) {
				GeoElement[] ret =
				{ getAlgoDispatcher().PointIn(c.getLabel(), (Region) arg[0], 0, 0, true, false)};
				return ret;
			} 
			throw argErr(app, c.getName(), arg[0]);
		}
		throw argNumErr(app, c.getName(), n);

	}
}