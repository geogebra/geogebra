package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 *AxisStepY
 */
public class CmdAxisStepY extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAxisStepY(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:

			GeoElement[] ret = { kernelA.AxisStepY(c.getLabel()) };
			return ret;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
