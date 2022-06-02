package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoType;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.main.MyError;

/**
 * Type[ &lt;GeoConic or Quadric> ]
 */
public class CmdType extends CommandProcessor {

	/**
	 * Create new command processor
	 *
	 * @param kernel
	 *            kernel
	 */
	public CmdType(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		if (n == 1) {
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0] instanceof GeoQuadricND) {
				AlgoType algo = new AlgoType(cons, (GeoQuadricND) arg[0]);
				algo.getResult().setLabel(c.getLabel());
				return new GeoElement[]{algo.getResult()};
			}
			throw argErr(c, arg[0]);
		}
		throw argNumErr(c);
	}
}
