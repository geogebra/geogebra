package geogebra.common.kernel.commands;


import geogebra.common.kernel.Region;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.common.kernel.AbstractKernel;


/**
 * PointIn[ <Region> ] 
 * @version 2010-05-17
 */
public class CmdPointIn extends CommandProcessor {

	/**
	 * Initiates command processor for PointIn command
	 * @param AbstractKernel kernel used for computations
	 */
	public CmdPointIn (AbstractKernel kernel) {
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
				{ kernelA.PointIn(c.getLabel(), (Region) arg[0])};
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		}else
			throw argNumErr(app, c.getName(), n);

	}
}