package geogebra.common.kernel.advanced;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoUnitOrthoVectorLine;
import geogebra.common.kernel.algos.AlgoUnitOrthoVectorVector;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.main.MyError;


/**
 * UnitOrthogonalVector[ <GeoLine> ] UnitOrthogonalVector[ <GeoVector> ]
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
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoLine()) {
				AlgoUnitOrthoVectorLine algo = new AlgoUnitOrthoVectorLine(cons, c.getLabel(),
						(GeoLine) arg[0]);

				GeoElement[] ret = { algo.getVector() };
				return ret;
			} else if (arg[0].isGeoVector()) {
				
				AlgoUnitOrthoVectorVector algo = new AlgoUnitOrthoVectorVector(cons,
						c.getLabel(),
						(GeoVector) arg[0]);

				GeoElement[] ret = { algo.getVector() };
				return ret;
			} 
			throw argErr(app, c.getName(), arg[0]);
			

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}


