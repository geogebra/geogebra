package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoUnitOrthoVectorLine;
import org.geogebra.common.kernel.algos.AlgoUnitOrthoVectorVector;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.main.MyError;

/**
 * UnitOrthogonalVector[ &lt;GeoLine&gt; ] UnitOrthogonalVector[
 * &lt;GeoVector&gt; ]
 */
public class CmdUnitOrthogonalVector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUnitOrthogonalVector(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0] instanceof GeoLine) {
				AlgoUnitOrthoVectorLine algo = new AlgoUnitOrthoVectorLine(cons,
						c.getLabel(), (GeoLine) arg[0]);

				GeoElement[] ret = { algo.getVector() };
				return ret;
			} else if (arg[0] instanceof GeoVec3D) {

				AlgoUnitOrthoVectorVector algo = new AlgoUnitOrthoVectorVector(
						cons, c.getLabel(), (GeoVec3D) arg[0]);

				GeoElement[] ret = { algo.getVector() };
				return ret;
			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
