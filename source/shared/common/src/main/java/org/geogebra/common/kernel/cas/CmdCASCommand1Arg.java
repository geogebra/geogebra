package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * used by TrigSimplify:, Expand, Factor, IFactor
 * 
 */
public class CmdCASCommand1Arg extends CommandProcessor implements UsesCAS {
	private Commands cmd;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 * @param cmd
	 *            command to be applied
	 */
	public CmdCASCommand1Arg(Kernel kernel, Commands cmd) {
		super(kernel);
		this.cmd = cmd;
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 1:
			if (arg[0] instanceof CasEvaluableFunction) {

				AlgoCasBaseSingleArgument algo = new AlgoCasBaseSingleArgument(
						cons, c.getLabel(), (CasEvaluableFunction) arg[0], cmd,
						info);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			throw argErr(c, arg[0]);

			// more than one argument
		default:
			throw argNumErr(c);
		}
	}
}
