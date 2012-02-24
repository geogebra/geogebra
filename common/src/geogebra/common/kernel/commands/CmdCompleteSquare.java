package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/**
 * CompleteSquare[ &lt;Polynomial> ]
 * 
 * @author kondr
 * 
 */
public class CmdCompleteSquare extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCompleteSquare(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if (arg[0].isGeoFunction()) {
				GeoElement[] ret = { kernelA.CompleteSquare(c.getLabel(),
						(GeoFunction) arg[0]) };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
