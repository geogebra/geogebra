package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDefined;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * Defined[ Object ] Michael Borcherds 2008-03-06
 */
public class CmdDefined extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDefined(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);

		if (n == 1) {
			AlgoDefined algo = new AlgoDefined(cons, arg[0]);
			algo.getResult().setLabel(c.getLabel());
			return new GeoElement[]{algo.getResult()};
		}
		throw argNumErr(c);
	}

}
