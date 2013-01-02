package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoIntersectPathLinePolygon;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.main.MyError;


/**
 * IntersectPath[ <GeoLine>, <GeoPolygon> ]
 * IntersectPath[ <GeoLine>, <GeoConic> ]
 */
public class CmdIntersectPath extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIntersectPath(Kernel kernel) {
		super(kernel);
	}

	@Override
	public  GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2 :
			arg = resArgs(c);

			// Line - Polygon(as region) in 2D
			if ((ok[0] = (arg[0] .isGeoLine()))
					&& (ok[1] = (arg[1] .isGeoPolygon()))) {
				GeoElement[] ret =
						intersectPathLinePolygon(
								c.getLabels(),
								(GeoLine) arg[0],
								(GeoPolygon) arg[1]);
				return ret;
			} else if ((ok[0] = (arg[0] .isGeoPolygon()))
					&& (ok[1] = (arg[1] .isGeoLine()))) {
				GeoElement[] ret =
						intersectPathLinePolygon(
								c.getLabels(),
								(GeoLine) arg[1],
								(GeoPolygon) arg[0]);
				return ret;
			}
			
			
			// Line - Conic
			/*
			else if (
					(ok[0] = (arg[0] .isGeoLine()))
					&& (ok[1] = (arg[1] .isGeoConic())))
				return intersectPathLineConic(
						c.getLabels(),
						(GeoLine) arg[0],
						(GeoConic) arg[1]);
			else if (
					(ok[0] = (arg[0] .isGeoConic()))
					&& (ok[1] = (arg[1] .isGeoLine())))
				return intersectPathLineConic(
						c.getLabels(),
						(GeoLine) arg[1],
						(GeoConic) arg[0]);

			 */


			throw argErr(app, c.getName(), getBadArg(ok,arg));

		default :
			throw argNumErr(app, c.getName(), n);
		}
	}

	/*
	 * IntersectLineConic yields intersection points named label1, label2 of
	 * line g and conic c and intersection lines named in lowcase of the label
	 *
	final private GeoLine[] intersectPathLineConic(String[] labels, GeoLine g,
			GeoConic c) {
		AlgoIntersectLineConicRegion algo = new AlgoIntersectLineConicRegion(
				cons, labels, g, c);

		GeoLine[] lines = algo.getIntersectionLines();

		return lines;
	}
	*/
	

	/**
	 * yields intersection segments named label of line g and polygon p (as
	 * region)
	 */
	final private GeoElement[] intersectPathLinePolygon(String[] labels,
			GeoLine g, GeoPolygon p) {
		AlgoIntersectPathLinePolygon algo = new AlgoIntersectPathLinePolygon(
				cons, labels, g, p);
		return algo.getOutput();
	}
}