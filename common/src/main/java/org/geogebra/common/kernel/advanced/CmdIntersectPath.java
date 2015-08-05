package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoIntersectPathLinePolygon;
import org.geogebra.common.kernel.algos.AlgoIntersectPolyLineConicRegion;
import org.geogebra.common.kernel.algos.AlgoIntersectSegmentConicRegion;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.main.MyError;

/**
 * IntersectPath[ <GeoLine>, <GeoPolygon> ] IntersectPath[ <GeoLine>, <GeoConic>
 * ] // removed IntersectPath[<GeoSegment>, <GeoConic>]
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
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// Line - Polygon(as region) in 2D
			if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoPolygon()))) {
				GeoElement[] ret = intersectPathLinePolygon(c.getLabels(),
						(GeoLine) arg[0], (GeoPolygon) arg[1]);
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPolygon()))
					&& (ok[1] = (arg[1].isGeoLine()))) {
				GeoElement[] ret = intersectPathLinePolygon(c.getLabels(),
						(GeoLine) arg[1], (GeoPolygon) arg[0]);
				return ret;
			}

			
			  // Line - Conic(as region) in 2D else if ((ok[0] =
			// if (((arg[0].isGeoLine())) && (ok[1] = (arg[1].isGeoConic())))
			// return
			// intersectLineConicRegion(c.getLabels(), (GeoLine) arg[0],
			// (GeoConic) arg[1]);
			//
			//
			// else if ((ok[0] = (arg[0].isGeoConic())) && (ok[1] =
			// (arg[1].isGeoLine()))) return
			// intersectLineConicRegion(c.getLabels(), (GeoLine) arg[1],
			// (GeoConic) arg[0]);


			// Segment - Conic(as region)
			if ((ok[0] = (arg[0].isGeoSegment()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = intersectSegmentConicRegion(c.getLabels(),
						(GeoSegment) arg[0], (GeoConic) arg[1]);
				return ret;
			} else if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isGeoSegment()))) {
				GeoElement[] ret = intersectSegmentConicRegion(c.getLabels(),
						(GeoSegment) arg[1], (GeoConic) arg[0]);
				return ret;
			}

			// polyLine - Conic(as region)
			if ((ok[0] = (arg[0].isGeoPolyLine()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = intersectPolyConicRegion(c.getLabels(),
						(GeoPoly) arg[0], (GeoConic) arg[1], false);
				return ret;
			} else if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isGeoPolyLine()))) {
				GeoElement[] ret = intersectPolyConicRegion(c.getLabels(),
						(GeoPoly) arg[1], (GeoConic) arg[0], false);
				return ret;
			}

			// polygon(as boundary) - Conic(as region)
			if ((ok[0] = (arg[0].isGeoPolygon()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = intersectPolyConicRegion(c.getLabels(),
						(GeoPoly) arg[0], (GeoConic) arg[1], true);
				return ret;
			} else if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isGeoPolygon()))) {
				GeoElement[] ret = intersectPolyConicRegion(c.getLabels(),
						(GeoPoly) arg[1], (GeoConic) arg[0], true);
				return ret;
			}

			// Polygon(as region) - Polygon(as region) in 2D

			if ((ok[0] = arg[0].isGeoPolygon()) && arg[1].isGeoPolygon()) {
				GeoElement[] ret = getAlgoDispatcher()
						.IntersectPolygons(c.getLabels(), (GeoPolygon) arg[0],
						(GeoPolygon) arg[1], true);
				return ret;
			}

			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}


	/**
	 * IntersectLineConic yields intersection points named label1, label2 of
	 * line g and conic c and intersection lines named in lowcase of the label
	 */

	// final private GeoLine[] intersectLineConicRegion(String[] labels,
	// GeoLine g, GeoConic c) {
	// AlgoIntersectLineConicRegion algo = new AlgoIntersectLineConicRegion(
	// cons, labels, g, c);
	//
	// GeoLine[] lines = algo.getIntersectionLines();
	//
	// return lines;
	// }

	/**
	 * yields intersection segments named label of GeoPoly poly and conic(as
	 * region)
	 */
	final private GeoElement[] intersectPolyConicRegion(String[] labels,
			GeoPoly poly, GeoConic conic, boolean isPolyClosed) {
		AlgoIntersectPolyLineConicRegion algo = new AlgoIntersectPolyLineConicRegion(
				cons, labels, poly, conic, isPolyClosed);
		GeoElement[] ret = algo.getOutput();
		return ret;
	}

	/**
	 * yields intersection segments named label of segment seg and conic(as
	 * region)
	 */
	final private GeoElement[] intersectSegmentConicRegion(String[] labels,
			GeoSegment seg, GeoConic conic) {
		AlgoIntersectSegmentConicRegion algo = new AlgoIntersectSegmentConicRegion(
				cons, labels, seg, conic);
		GeoElement[] ret = algo.getOutput();
		// GeoElement.setLabels(labels, ret);
		return ret;
	}

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