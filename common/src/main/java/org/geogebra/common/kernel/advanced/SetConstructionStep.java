package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * ConstructionStep[] ConstructionStep[ &lt;Object> ]
 * 
 * @author Michael Borcherds
 * @version 2008-03-06
 */
public class SetConstructionStep extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public SetConstructionStep(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 0:

			AlgoConstructionStep algo = new AlgoConstructionStep(cons,
					c.getLabel());
			GeoElement[] ret = { algo.getResult() };
			return ret;

		case 1:
			AlgoStepObject algo2 = new AlgoStepObject(cons, c.getLabel(),
					arg[0]);
			GeoElement[] ret2 = { algo2.getResult() };
			return ret2;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
