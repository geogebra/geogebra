package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoOrthoVectorLine;
import org.geogebra.common.kernel.algos.AlgoOrthoVectorVector;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.main.MyError;

/**
 * OrthogonalVector[ <GeoLine> ] OrthogonalVector[ <GeoVector> ]
 */
public class CmdOrthogonalVector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdOrthogonalVector(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoLine()) {

				AlgoOrthoVectorLine algo = new AlgoOrthoVectorLine(cons,
						c.getLabel(), (GeoLine) arg[0]);

				GeoElement[] ret = { algo.getVector() };
				return ret;
			} else if (arg[0].isGeoVector()) {
				AlgoOrthoVectorVector algo = new AlgoOrthoVectorVector(cons,
						c.getLabel(), (GeoVector) arg[0]);

				GeoElement[] ret = { algo.getVector() };
				return ret;
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
