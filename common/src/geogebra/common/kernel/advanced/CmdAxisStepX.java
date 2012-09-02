package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**
 *AxisStepX
 */
public class CmdAxisStepX extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAxisStepX(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:

			AlgoAxisStepX algo = new AlgoAxisStepX(cons, c.getLabel());

			GeoElement[] ret = { algo.getResult() };
			return ret;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
