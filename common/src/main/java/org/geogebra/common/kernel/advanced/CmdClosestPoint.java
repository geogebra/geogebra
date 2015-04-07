package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.algos.AlgoClosestPoint;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * ClosestPoint[Point,Path] ClosestPoint[Path,Point]
 */
public class CmdClosestPoint extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdClosestPoint(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// distance between two points
			if ((ok[0] = (arg[0] instanceof Path))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { ClosestPoint(c.getLabel(), (Path) arg[0],
						(GeoPointND) arg[1]) };
				return ret;
			}

			// distance between point and line
			else if ((ok[1] = (arg[1] instanceof Path))
					&& (ok[0] = (arg[0].isGeoPoint()))) {
				GeoElement[] ret = { ClosestPoint(c.getLabel(), (Path) arg[1],
						(GeoPointND) arg[0]) };
				return ret;
			}

			else if ((ok[1] = (arg[1] instanceof GeoLine))
					&& (ok[0] = (arg[0] instanceof GeoLine))) {
				GeoElement[] ret = { new AlgoClosestPointLines(
						kernelA.getConstruction(), c.getLabel(),
						(GeoLine) arg[1], (GeoLine) arg[0]).getOutput(0) };
				return ret;
			}

			// syntax error
			else {
				if (ok[0] && !ok[1])
					throw argErr(app, c.getName(), arg[1]);
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/** Point anywhere on path with */
	final private GeoPoint ClosestPoint(String label, Path path, GeoPointND p) {
		AlgoClosestPoint algo = new AlgoClosestPoint(cons, label, path, p);
		return (GeoPoint) algo.getP();
	}
}
