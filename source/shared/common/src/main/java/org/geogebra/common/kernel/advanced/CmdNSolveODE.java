package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoNSolveODE;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * NSolveODE
 * 
 * @author Bencze Balazs
 */
public class CmdNSolveODE extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdNSolveODE(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);
		if (n < 3) {
			throw argNumErr(c);
		}
		if (!arg[0].isGeoList()) {
			throw argErr(c, arg[0]);
		}
		if (!arg[2].isGeoList()) {
			throw argErr(c, arg[2]);
		}
		int dim1 = ((GeoList) arg[0]).size();
		int dim2 = ((GeoList) arg[2]).size();
		for (int i = 0; i < dim1; i++) {
			if (!(((GeoList) arg[0]).get(i) instanceof FunctionalNVar)) {
				throw argErr(c, arg[0]);
			}
		}
		for (int i = 0; i < dim2; i++) {
			if (!(((GeoList) arg[2]).get(i)).isGeoNumeric()) {
				throw argErr(c, arg[2]);
			}
		}
		if (dim1 != dim2) {
			throw argErr(c, arg[0]);
		}
		if (dim1 == 0) {
			throw argErr(c, arg[0]);
		}
		boolean[] ok = new boolean[n];
		if (n == 4) {
			if ((ok[0] = true) // already checked before
					&& (ok[1] = arg[1].isGeoNumeric()) && (ok[2] = true) // already
																			// checked
																			// before
					&& (ok[3] = arg[3].isGeoNumeric())) {
				AlgoNSolveODE algo = new AlgoNSolveODE(cons, c.getLabels(),
						(GeoList) arg[0], (GeoNumeric) arg[1], (GeoList) arg[2],
						(GeoNumeric) arg[3]);
				return algo.getResult();
			}
			throw argErr(c, getBadArg(ok, arg));
		}
		throw argNumErr(c);
	}
}