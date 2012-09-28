package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**
 * NSolveODE
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
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);
		if (!arg[0].isGeoList()) {
			throw argErr(app, c.getName(), arg[0]);
		}
		if (!arg[2].isGeoList()) {
			throw argErr(app, c.getName(), arg[2]);
		}
		int dim1 = ((GeoList)arg[0]).size();
		int dim2 = ((GeoList)arg[2]).size();
		for(int i = 0; i < dim1; i++) {
			if (!(((GeoList)arg[0]).get(i) instanceof FunctionalNVar)) {
				throw argErr(app, c.getName(), arg[0]);
			}
		}
		for(int i = 0; i < dim2; i++) {
			if (!(((GeoList)arg[2]).get(i)).isGeoNumeric()) {
				throw argErr(app, c.getName(), arg[2]);
			}
		}
		if (dim1 != dim2) {
			throw argErr(app, c.getName(), arg[0]);
		}
		if (dim1 == 0) {
			throw argErr(app, c.getName(), arg[0]);
		}
		
		if (n == 4) {
			if ((ok[0] = true)			// already checked before
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = true)	// already checked before
					&& (ok[3] = arg[3].isGeoNumeric())) {
				GeoElement[] ret = getAlgoDispatcher().NSolveODE(c.getLabels(),
						(GeoList) arg[0], (GeoNumeric) arg[1],
						(GeoList) arg[2], (GeoNumeric) arg[3]);
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));
		}
		throw argNumErr(app, c.getName(), n);
	}
}