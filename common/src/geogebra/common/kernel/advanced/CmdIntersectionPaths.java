package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoIntersectLineConicRegion;
import geogebra.common.kernel.algos.AlgoIntersectLinePolygonalRegion;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.main.MyError;


/**
 * IntersectionPaths[ <GeoLine>, <GeoPolygon> ]
 * IntersectionPaths[ <GeoLine>, <GeoConic> ]
 * IntersectionPaths[ <GeoPlane>, <GeoPolygon> ]
 * IntersectionPaths[ <GeoPlane>, <GeoQuadric> ]
 */
public class CmdIntersectionPaths extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIntersectionPaths(Kernel kernel) {
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
						IntersectLinePolygonalRegion(
								c.getLabels(),
								(GeoLine) arg[0],
								(GeoPolygon) arg[1]);
				return ret;
			} else if ((ok[0] = (arg[0] .isGeoPolygon()))
					&& (ok[1] = (arg[1] .isGeoLine()))) {
				GeoElement[] ret =
						IntersectLinePolygonalRegion(
								c.getLabels(),
								(GeoLine) arg[1],
								(GeoPolygon) arg[0]);
				return ret;
			}
			// Line - Conic
			else if (
					(ok[0] = (arg[0] .isGeoLine()))
					&& (ok[1] = (arg[1] .isGeoConic())))
				return IntersectLineConicRegion(
						c.getLabels(),
						(GeoLine) arg[0],
						(GeoConic) arg[1]);
			else if (
					(ok[0] = (arg[0] .isGeoConic()))
					&& (ok[1] = (arg[1] .isGeoLine())))
				return IntersectLineConicRegion(
						c.getLabels(),
						(GeoLine) arg[1],
						(GeoConic) arg[0]);




			/*  
            //implicit Poly - Polynomial
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
	                    && (ok[1] = (arg[1] .isGeoFunctionable())
	                    && (ok[1]=((GeoFunctionable) arg[1]).getGeoFunction().isPolynomialFunction(false))
	                    && (ok[1] != (arg[1].isGeoLine()))))
					return kernel.IntersectImplicitpolyPolynomial(
	                    c.getLabels(), (GeoImplicitPoly) arg[0],
	                    ((GeoFunctionable) arg[1]).getGeoFunction()
	                   );
			else if (ok[0] = (arg[0] .isGeoFunctionable())
	                    && (ok[1]=((GeoFunctionable) arg[0]).getGeoFunction().isPolynomialFunction(false))
	                    && (ok[1] = (arg[1] .isGeoImplicitPoly()))
	                    && (ok[1] != (arg[1].isGeoLine())))
					return kernel.IntersectImplicitpolyPolynomial(
	                    c.getLabels(), (GeoImplicitPoly) arg[1],
	                    ((GeoFunctionable) arg[0]).getGeoFunction()
	                   );
            //implicitPoly - Line
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
	                    && (ok[1] = (arg[1].isGeoLine())))
					return kernel.IntersectImplicitpolyLine(
	                    c.getLabels(), (GeoImplicitPoly) arg[0],
	                    (GeoLine) arg[1] );
			else if (
	                (ok[1] = (arg[1] .isGeoImplicitPoly()))
	                    && (ok[0] = (arg[0].isGeoLine())))
					return kernel.IntersectImplicitpolyLine(
	                    c.getLabels(), (GeoImplicitPoly) arg[1],
	                    (GeoLine) arg[0] );
            //implicitPoly - implicitPoly
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
	                    && (ok[1] = (arg[1].isGeoImplicitPoly())))
					return kernel.IntersectImplicitpolys(
	                    c.getLabels(), (GeoImplicitPoly) arg[0],
	                    (GeoImplicitPoly) arg[1] );
            //implicitPoly-conic
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
                    && (ok[1] = (arg[1].isGeoConic())))
				return kernel.IntersectImplicitpolyConic(
                    c.getLabels(), (GeoImplicitPoly) arg[0],
                    (GeoConic) arg[1] );
			else if (
	                (ok[1] = (arg[1] .isGeoImplicitPoly()))
                    && (ok[0] = (arg[0].isGeoConic())))
				return kernel.IntersectImplicitpolyConic(
                    c.getLabels(), (GeoImplicitPoly) arg[1],
                    (GeoConic) arg[0] );*/
			/* moved to CmdIntersection to allow Intersect[List, List] to intersect list elements in the future
			// intersection of two lists
			else if (arg[0].isGeoList() && arg[1].isGeoList() ) {
				GeoElement[] ret = { 
						kernel.Intersection(c.getLabel(),
						(GeoList) arg[0], (GeoList)arg[1] ) };
				return ret;
			} */



			throw argErr(app, c.getName(), getBadArg(ok,arg));

		default :
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * IntersectLineConic yields intersection points named label1, label2 of
	 * line g and conic c and intersection lines named in lowcase of the label
	 */
	final private GeoLine[] IntersectLineConicRegion(String[] labels, GeoLine g,
			GeoConic c) {
		AlgoIntersectLineConicRegion algo = new AlgoIntersectLineConicRegion(
				cons, labels, g, c);

		GeoLine[] lines = algo.getIntersectionLines();

		return lines;
	}
	

	/**
	 * yields intersection segments named label of line g and polygon p (as
	 * region)
	 */
	final private GeoElement[] IntersectLinePolygonalRegion(String[] labels,
			GeoLine g, GeoPolygon p) {
		AlgoIntersectLinePolygonalRegion algo = new AlgoIntersectLinePolygonalRegion(
				cons, labels, g, p);
		return algo.getOutput();
	}
}