package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * AxisStepX
 */
public class CmdAxisStepX extends CommandProcessor {

	private int axis;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAxisStepX(Kernel kernel, int axis) {
		super(kernel);
		this.axis = axis;
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:

			AlgoAxisStep algo = new AlgoAxisStep(cons, c.getLabel(), axis);

			GeoElement[] ret = { algo.getResult() };
			return ret;

		default:
			throw argNumErr(c);
		}
	}
}
