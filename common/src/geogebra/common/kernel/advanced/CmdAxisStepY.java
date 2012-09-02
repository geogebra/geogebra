package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
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

			AlgoAxisStepY algo = new AlgoAxisStepY(cons, c.getLabel());

			GeoElement[] ret = { algo.getResult() };
			return ret;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
