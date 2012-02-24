package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/**
 * ComplexRoot[ <GeoFunction> ]
 */
public class CmdComplexRoot extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdComplexRoot(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		// roots of polynomial
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoFunctionable()) {
				return kernelA.ComplexRoot(c.getLabels(),
						((GeoFunctionable) arg[0]).getGeoFunction());
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
