package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * Cell
 */
public class CmdCell extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCell(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok = false;
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 2:
			if ((ok = arg[0] instanceof GeoNumberValue)
					&& arg[1] instanceof GeoNumberValue) {

				AlgoCell algo = new AlgoCell(cons, c.getLabel(),
						(GeoNumberValue) arg[0], (GeoNumberValue) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			throw argErr(c, ok ? arg[1] : arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
