package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * Limit
 */
public class CmdLimit extends CommandProcessor implements UsesCAS {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLimit(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[c.getArgumentNumber()];
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 2:
			if ((ok[0] = arg[0].isGeoFunction())
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {

				return process(c.getLabel(), arg[0], arg[1]);
			}
			throw argErr(c, getBadArg(ok, arg));
		case 3:
			/* Same as for 2 args but second arg is ignored. The 3 args syntax used in Classic CAS
				eg: Limit(If(x<-1,x+2,-1<=x<=1, 1,1<x<2,x^2,4),x,2)
				We need to keep it for backwards compatibility, see APPS-2791 */
			if ((ok[0] = arg[0].isGeoFunction())
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {

				return process(c.getLabel(), arg[0], arg[2]);
			}
			throw argErr(c, getBadArg(ok, arg));
			// more than one argument
		default:
			throw argNumErr(c);
		}
	}

	private GeoElement[] process(String label, GeoElement arg1, GeoElement arg2) {
		AlgoLimit algo = new AlgoLimit(cons, label,
				(GeoFunction) arg1, (GeoNumberValue) arg2);
		GeoElement[] ret = {algo.getResult()};
		return ret;
	}
}
