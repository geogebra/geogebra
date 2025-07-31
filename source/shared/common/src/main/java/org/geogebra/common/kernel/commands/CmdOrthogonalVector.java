package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoOrthoVectorLine;
import org.geogebra.common.kernel.algos.AlgoOrthoVectorVector;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.main.MyError;

/**
 * OrthogonalVector[ &lt;GeoLine&gt; ]
 * 
 * OrthogonalVector[ &lt;GeoVector&gt; ]
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
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);
			if (arg[0].isGeoLine()) {

				AlgoOrthoVectorLine algo = new AlgoOrthoVectorLine(cons,
						c.getLabel(), (GeoLine) arg[0]);

				GeoElement[] ret = { algo.getVector() };
				return ret;
			} else if (arg[0] instanceof GeoVec3D) {
				AlgoOrthoVectorVector algo = new AlgoOrthoVectorVector(cons,
						c.getLabel(), (GeoVec3D) arg[0]);

				GeoElement[] ret = { algo.getVector() };
				return ret;
			} else {
				throw argErr(c, arg[0]);
			}

		default:
			throw argNumErr(c);
		}
	}
}
