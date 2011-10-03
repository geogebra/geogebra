package geogebra.kernel.commands;


import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Region;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;


/**
 * PointIn[ <Region> ] 
 * @version 2010-05-17
 */
public class CmdPointIn extends CommandProcessor {

	/**
	 * Initiates command processor for PointIn command
	 * @param kernel Kernel used for computations
	 */
	public CmdPointIn (Kernel kernel) {
		super(kernel);
	}

	/**
	 * Checks correctness of inputs and runs the command.
	 * Last change: correct error messages (2010-05-17), Zbynek Konecny 
	 */
	public  GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		if (n==1) {
			arg = resArgs(c);
			if (ok[0] = (arg[0].isRegion())) {
				GeoElement[] ret =
				{ kernel.PointIn(c.getLabel(), (Region) arg[0])};
				return ret;
			} else
				throw argErr(app, "PointIn", arg[0]);
		}else
			throw argNumErr(app, "PointIn", n);

	}
}