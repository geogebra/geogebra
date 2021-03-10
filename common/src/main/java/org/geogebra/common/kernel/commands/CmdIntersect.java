package org.geogebra.common.kernel.commands;

import org.geogebra.common.geogebra3D.kernel3D.implicit3D.GeoImplicitSurface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.algos.AlgoIntersect;
import org.geogebra.common.kernel.algos.AlgoIntersectConics;
import org.geogebra.common.kernel.algos.AlgoIntersectCurveCurve;
import org.geogebra.common.kernel.algos.AlgoIntersectFunctions;
import org.geogebra.common.kernel.algos.AlgoIntersectLineConic;
import org.geogebra.common.kernel.algos.AlgoIntersectPolynomialLine;
import org.geogebra.common.kernel.algos.AlgoIntersectPolynomials;
import org.geogebra.common.kernel.algos.AlgoIntersectSingle;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.implicit.AlgoIntersectImplicitpolys;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Intersect[ &lt;GeoLine>, &lt;GeoLine> ]
 * 
 * Intersect[ &lt;GeoLine>, &lt;GeoPolygon> ]
 * 
 * Intersect[ &lt;GeoPolyLine>, &lt;GeoPolyLine> ]
 * 
 * Intersect[ &lt;GeoLine>, &lt;GeoConic> ]
 * 
 * Intersect[ &lt;GeoConic>, &lt;GeoLine> ]
 * 
 * Intersect[ &lt;GeoConic>, &lt;GeoConic> ]
 * 
 * Intersect[ &lt;GeoFunction>, &lt;GeoFunction> ]
 * 
 * Intersect[ &lt;GeoFunction>, &lt;GeoLine> ]
 * 
 * Intersect[ &lt;GeoImplicitPoly>, &lt;GeoImplicitPoly> ]
 * 
 * Intersect[ &lt;GeoImplicitPoly>, &lt;GeoLine> ]
 * 
 * Intersect[ &lt;GeoImplicitPoly>, &lt;GeoFunction(Polynomial)> ]
 * 
 * Intersect[ &lt;GeoFunction>, &lt;GeoFunction>, &lt;NumberValue>,
 * &lt;NumberValue> ] Intersect[ &lt;Path>, &lt;Point> ]
 */
public class CmdIntersect extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIntersect(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		// Application.debug(n,1);

		switch (n) {
		case 2:
			arg = resArgs(c);
			// Line - Line
			return intersect2(arg, c);

		case 3: // only one of the intersection points: the third argument
			// states which one
			arg = resArgs(c);
			// Line - Conic
			return intersect3(arg, c);

		case 4:
			arg = resArgs(c);
			// Function - Function in interval [a,b]
			// Polynomial - Polynomial with index of point
			if ((ok[0] = (arg[0].isRealValuedFunction()))
					&& (ok[1] = (arg[1].isRealValuedFunction()))
					&& (ok[2] = (arg[2] instanceof GeoNumberValue))
					&& (ok[3] = (arg[3] instanceof GeoNumberValue))) {
				GeoElement[] ret = intersectFunctions(c.getLabels(),
						(GeoFunctionable) arg[0], (GeoFunctionable) arg[1],
						(GeoNumberValue) arg[2], (GeoNumberValue) arg[3]);
				return ret;
				// intersection of curves with starting point for iteration
			} else if ((ok[0] = (arg[0] instanceof GeoCurveCartesianND))
					&& (ok[1] = (arg[1] instanceof GeoCurveCartesianND))
					&& (ok[2] = (arg[2] instanceof GeoNumberValue))
					&& (ok[3] = (arg[3] instanceof GeoNumberValue))

			) {
				AlgoIntersectCurveCurve algo = new AlgoIntersectCurveCurve(cons,
						c.getLabels(), (GeoCurveCartesianND) arg[0], (GeoCurveCartesianND) arg[1],
						(GeoNumberValue) arg[2],
						(GeoNumberValue) arg[3]);
				return algo.getOutput();
			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @param arg
	 *            arguments
	 * @param c
	 *            command
	 * @return intersection of two objects
	 */
	public GeoElement[] intersect2(GeoElement[] arg, Command c) {
		boolean[] ok = new boolean[2];
		if ((ok[0] = (arg[0].isGeoLine())) && (ok[1] = (arg[1].isGeoLine()))) {
			GeoElement[] ret = {
					(GeoElement) getAlgoDispatcher().intersectLines(
							c.getLabel(), (GeoLine) arg[0], (GeoLine) arg[1]) };
			return ret;
		}
		// Line - Parametric Curve
		else if ((ok[0] = (arg[0].isGeoLine()))
				&& (ok[1] = (arg[1] instanceof GeoCurveCartesian))) {
			GeoElement[] ret = getAlgoDispatcher().intersectLineCurve(
					c.getLabels(), (GeoLine) arg[0],
					(GeoCurveCartesian) arg[1]);
			return ret;
		} else if ((ok[0] = (arg[0] instanceof GeoCurveCartesian))
				&& (ok[1] = (arg[1].isGeoLine()))) {
			GeoElement[] ret = getAlgoDispatcher().intersectLineCurve(
					c.getLabels(), (GeoLine) arg[1],
					(GeoCurveCartesian) arg[0]);
			return ret;
		}
		// curve - curve
		else if ((ok[0] = (arg[0] instanceof GeoCurveCartesian))
				&& (ok[1] = (arg[1] instanceof GeoCurveCartesian))) {
			GeoElement[] ret = getAlgoDispatcher().intersectCurveCurve(
					c.getLabels(), (GeoCurveCartesian) arg[1],
					(GeoCurveCartesian) arg[0]);
			return ret;
		}
		// Line - PolyLine
		else if ((ok[0] = (arg[0].isGeoLine()))
				&& (ok[1] = (arg[1] instanceof GeoPolyLine))) {
			GeoElement[] ret = getAlgoDispatcher().intersectLinePolyLine(
					c.getLabels(), (GeoLine) arg[0], (GeoPolyLine) arg[1]);
			return ret;
		} else if ((ok[0] = (arg[0] instanceof GeoPolyLine))
				&& (ok[1] = (arg[1].isGeoLine()))) {
			GeoElement[] ret = getAlgoDispatcher().intersectLinePolyLine(
					c.getLabels(), (GeoLine) arg[1], (GeoPolyLine) arg[0]);
			return ret;
		}

		/**
		 * @author thilina
		 */
		// PolyLine - PolyLine
		else if ((ok[0] = (arg[0] instanceof GeoPolyLine))
				&& (ok[1] = (arg[1] instanceof GeoPolyLine))) {
			GeoElement[] ret = getAlgoDispatcher().intersectPolyLines(
					c.getLabels(), (GeoPolyLine) arg[1], (GeoPolyLine) arg[0]);
			return ret;
		}

		// Line - Polygon(as boudary)
		else if ((ok[0] = (arg[0].isGeoLine()))
				&& (ok[1] = (arg[1].isGeoPolygon()))) {
			GeoElement[] ret = getAlgoDispatcher().intersectLinePolygon(
					c.getLabels(), (GeoLine) arg[0], (GeoPolygon) arg[1]);
			return ret;
		} else if ((ok[0] = (arg[0].isGeoPolygon()))
				&& (ok[1] = (arg[1].isGeoLine()))) {
			GeoElement[] ret = getAlgoDispatcher().intersectLinePolygon(
					c.getLabels(), (GeoLine) arg[1], (GeoPolygon) arg[0]);
			return ret;
		}

		// Line - Polygon(as region)
		// ---- see CmdIntersectionPaths

		/**
		 * @author thilina
		 */
		// PolyLine - Polygon(as boundary)
		else if ((ok[0] = arg[0].isGeoPolyLine())
				&& (ok[1] = arg[1].isGeoPolygon())) {
			GeoElement[] ret = getAlgoDispatcher().intersectPolyLinePolygon(
					c.getLabels(), (GeoPolyLine) arg[0], (GeoPolygon) arg[1]);
			return ret;
		} else if ((ok[0] = arg[0].isGeoPolygon())
				&& (ok[1] = arg[1].isGeoPolyLine())) {
			GeoElement[] ret = getAlgoDispatcher().intersectPolyLinePolygon(
					c.getLabels(), (GeoPolyLine) arg[1], (GeoPolygon) arg[0]);
			return ret;
		}

		/**
		 * @author thilina
		 */
		// Polygon(as boundary) - Polygon(as boundary)
		else if ((ok[0] = arg[0].isGeoPolygon())
				&& (ok[1] = arg[1].isGeoPolygon())) {
			GeoElement[] ret = getAlgoDispatcher().intersectPolygons(
					c.getLabels(), (GeoPolygon) arg[0], (GeoPolygon) arg[1],
					false);
			return ret;
		}

		// polygon(as region) - Polygon(as region)
		// ---- see CmdIntersectionPaths

		// Line - Conic
		else if ((ok[0] = (arg[0].isGeoLine()))
				&& (ok[1] = (arg[1].isGeoConic()))) {
			return (GeoElement[]) getAlgoDispatcher().intersectLineConic(
					c.getLabels(), (GeoLine) arg[0], (GeoConic) arg[1]);
		} else if ((ok[0] = (arg[0].isGeoConic()))
				&& (ok[1] = (arg[1].isGeoLine()))) {
			return (GeoElement[]) getAlgoDispatcher().intersectLineConic(
					c.getLabels(), (GeoLine) arg[1], (GeoConic) arg[0]);
		} else if ((ok[0] = (arg[0].isGeoPolyLine()))
				&& (ok[1] = (arg[1].isGeoConic()))) {
			return getAlgoDispatcher().intersectPolyLineConic(c.getLabels(),
					(GeoPolyLine) arg[0], (GeoConic) arg[1]);
		} else if ((ok[0] = (arg[0].isGeoConic()))
				&& (ok[1] = (arg[1].isGeoPolyLine()))) {
			return getAlgoDispatcher().intersectPolyLineConic(c.getLabels(),
					(GeoPolyLine) arg[1], (GeoConic) arg[0]);
		} else if ((ok[0] = (arg[0].isGeoPolygon()))
				&& (ok[1] = (arg[1].isGeoConic()))) {
			return getAlgoDispatcher().intersectPolygonConic(c.getLabels(),
					(GeoPolygon) arg[0], (GeoConic) arg[1]);
		} else if ((ok[0] = (arg[0].isGeoConic()))
				&& (ok[1] = (arg[1].isGeoPolygon()))) {
			return getAlgoDispatcher().intersectPolygonConic(c.getLabels(),
					(GeoPolygon) arg[1], (GeoConic) arg[0]);
		} else if ((ok[0] = (arg[0].isGeoFunction()))
				&& (ok[1] = (arg[1].isGeoConic()))) {
			return getAlgoDispatcher().intersectPolynomialConic(c.getLabels(),
					(GeoFunction) arg[0], (GeoConic) arg[1]);
		} else if ((ok[0] = (arg[0].isGeoConic()))
				&& (ok[1] = (arg[1].isGeoFunction()))) {
			return getAlgoDispatcher().intersectPolynomialConic(c.getLabels(),
					(GeoFunction) arg[1], (GeoConic) arg[0]);
		} else if ((ok[0] = (arg[0].isGeoConic()))
				&& (ok[1] = (arg[1].isGeoConic()))) {
			return (GeoElement[]) getAlgoDispatcher().intersectConics(
					c.getLabels(), (GeoConic) arg[0], (GeoConic) arg[1]);
		} else if ((ok[0] = (arg[0].isRealValuedFunction()))
				&& (ok[1] = (arg[1].isGeoLine()))) {
			return getAlgoDispatcher().intersectPolynomialLine(c.getLabels(),
					(GeoFunctionable) arg[0],
					(GeoLine) arg[1], null);
		} else if ((ok[0] = (arg[0].isGeoLine()))
				&& (ok[1] = (arg[1].isRealValuedFunction()))) {
			return getAlgoDispatcher().intersectPolynomialLine(c.getLabels(),
					(GeoFunctionable) arg[1],
					(GeoLine) arg[0], null);
		} else if ((ok[0] = (arg[0].isRealValuedFunction()))
				&& (ok[1] = (arg[1].isGeoPolyLine()))
				&& ((GeoFunctionable) arg[0]).isPolynomialFunction(false)) {

			return getAlgoDispatcher().intersectPolynomialPolyLine(
					c.getLabels(), (GeoFunctionable) arg[0],
					(GeoPolyLine) arg[1]);

		} else if ((ok[0] = (arg[0].isGeoPolyLine()))
				&& (ok[1] = (arg[1].isRealValuedFunction()))
				&& ((GeoFunctionable) arg[1]).isPolynomialFunction(false)) {

			return getAlgoDispatcher().intersectPolynomialPolyLine(
					c.getLabels(), (GeoFunctionable) arg[1],
					(GeoPolyLine) arg[0]);
		}

		// polynomial-polygon
		else if ((ok[0] = (arg[0].isRealValuedFunction()))
				&& (ok[1] = (arg[1].isGeoPolygon()))
				&& ((GeoFunctionable) arg[0]).isPolynomialFunction(false)) {

			return getAlgoDispatcher().intersectPolynomialPolygon(c.getLabels(),
					(GeoFunctionable) arg[0],
					(GeoPolygon) arg[1]);

		} else if ((ok[0] = (arg[0].isGeoPolygon()))
				&& (ok[1] = (arg[1].isRealValuedFunction()))
				&& ((GeoFunctionable) arg[1]).isPolynomialFunction(false)) {

			return getAlgoDispatcher().intersectPolynomialPolygon(c.getLabels(),
					(GeoFunctionable) arg[1],
					(GeoPolygon) arg[0]);

		} else if ((ok[0] = (arg[0].isRealValuedFunction()
				|| arg[0] instanceof GeoFunction))
				&& (ok[1] = (arg[1].isRealValuedFunction()
						|| arg[1] instanceof GeoFunction))) {
			// check after GeoLine as GeoLine is now GeoFunctionable
			return getAlgoDispatcher().intersectPolynomials(c.getLabels(),
					(GeoFunctionable) arg[0], (GeoFunctionable) arg[1]);
		} else if ((ok[0] = (arg[0].isGeoImplicitCurve()))
				&& (ok[1] = (arg[1].isGeoPolyLine()))) {
			return getAlgoDispatcher().intersectImplicitpolyPolyLine(
					c.getLabels(), (GeoImplicit) arg[0], (GeoPolyLine) arg[1]);
		} else if ((ok[1] = (arg[1].isGeoImplicitCurve()))
				&& (ok[0] = (arg[0].isGeoPolyLine()))) {
			return getAlgoDispatcher().intersectImplicitpolyPolyLine(
					c.getLabels(), (GeoImplicit) arg[1], (GeoPolyLine) arg[0]);
		} else if ((ok[0] = arg[0].isGeoImplicitCurve())
				&& (ok[1] = arg[1].isGeoLine())) {
			return getAlgoDispatcher().intersectImplicitCurveLine(c.getLabels(),
					(GeoImplicitCurve) arg[0], (GeoLine) arg[1]);
		} else if ((ok[0] = arg[0].isGeoImplicitCurve())
				&& (ok[1] = arg[1].isGeoConic())) {
			return getAlgoDispatcher().intersectImplicitCurveConic(
					c.getLabels(), (GeoImplicitCurve) arg[0],
					(GeoConic) arg[1]);
		} else if ((ok[0] = arg[0].isGeoImplicitCurve())
				&& (ok[1] = arg[1].isGeoFunction())) {
			return getAlgoDispatcher().intersectImplicitCurveFunction(
					c.getLabels(), (GeoImplicitCurve) arg[0],
					(GeoFunction) arg[1]);
		} else if ((ok[0] = arg[0].isGeoImplicitCurve())
				&& (ok[1] = arg[1].isGeoImplicitCurve())) {
			return getAlgoDispatcher().intersectImplicitCurveImpCurve(
					c.getLabels(), (GeoImplicit) arg[0], (GeoImplicit) arg[1]);
		} else if ((ok[0] = arg[0].isGeoLine())
				&& (ok[1] = arg[1].isGeoImplicitCurve())) {
			return getAlgoDispatcher().intersectImplicitCurveLine(c.getLabels(),
					(GeoImplicitCurve) arg[1], (GeoLine) arg[0]);
		} else if ((ok[0] = arg[0].isGeoConic())
				&& (ok[1] = arg[1].isGeoImplicitCurve())) {
			return getAlgoDispatcher().intersectImplicitCurveConic(
					c.getLabels(), (GeoImplicitCurve) arg[1],
					(GeoConic) arg[0]);
		} else if ((ok[0] = arg[0].isGeoFunction())
				&& (ok[1] = arg[1].isGeoImplicitCurve())) {
			return getAlgoDispatcher().intersectImplicitCurveFunction(
					c.getLabels(), (GeoImplicitCurve) arg[1],
					(GeoFunction) arg[0]);
		} else if ((ok[0] = arg[0].isGeoLine())
				&& (ok[1] = arg[1].isGeoImplicitSurface())) {
			return getAlgoDispatcher().intersectImplicitSurfaceLine(
					c.getLabels(), (GeoImplicitSurface) arg[1],
					(GeoLineND) arg[0]);
		} else if ((ok[0] = arg[0].isGeoImplicitSurface())
				&& (ok[1] = arg[1].isGeoLine())) {
			return getAlgoDispatcher().intersectImplicitSurfaceLine(
					c.getLabels(), (GeoImplicitSurface) arg[0],
					(GeoLineND) arg[1]);
		}
		// intersect path and point
		else if ((ok[0] = arg[0] instanceof Path)
				&& (ok[1] = arg[1].isGeoPoint())) {
			return getAlgoDispatcher().intersectPathPoint(c.getLabel(),
					(Path) arg[0], (GeoPointND) arg[1]);
		} else if ((ok[0] = arg[0].isGeoPoint())
				&& (ok[1] = arg[1] instanceof Path)) {
			return getAlgoDispatcher().intersectPathPoint(c.getLabel(),
					(Path) arg[1], (GeoPointND) arg[0]);
		}
		/*
		 * moved to CmdIntersection to allow Intersect[List, List] to intersect
		 * list elements in the future // intersection of two lists else if
		 * (arg[0].isGeoList() && arg[1].isGeoList() ) { GeoElement[] ret = {
		 * getAlgoDispatcher().Intersection(c.getLabel(), (GeoList) arg[0],
		 * (GeoList)arg[1] ) }; return ret; }
		 */

		else {
			if (!ok[0]) {
				throw argErr(c, arg[0]);
			}
			throw argErr(c, arg[1]);
		}
	}

	private GeoElement[] intersect3(GeoElement[] arg, Command c) {
		boolean[] ok = new boolean[3];
		if ((ok[0] = (arg[0].isGeoLine())) && (ok[1] = (arg[1].isGeoConic()))
				&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
			GeoElement[] ret = {
					intersectLineConicSingle(c.getLabel(), (GeoLine) arg[0],
							(GeoConic) arg[1], (GeoNumberValue) arg[2]) };
			return ret;
		} else if ((ok[0] = (arg[0].isGeoConic()))
				&& (ok[1] = (arg[1].isGeoLine()))
				&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
			GeoElement[] ret = {
					intersectLineConicSingle(c.getLabel(), (GeoLine) arg[1],
							(GeoConic) arg[0], (GeoNumberValue) arg[2]) };
			return ret;
		}
		// Line - Conic with startPoint
		else if ((ok[0] = (arg[0].isGeoLine()))
				&& (ok[1] = (arg[1].isGeoConic()))
				&& (ok[2] = (arg[2].isGeoPoint()))) {
			GeoElement[] ret = { intersectLineConicSingle(c.getLabel(),
					(GeoLine) arg[0], (GeoConic) arg[1], (GeoPoint) arg[2]) };
			return ret;
		} else if ((ok[0] = (arg[0].isGeoConic()))
				&& (ok[1] = (arg[1].isGeoLine()))
				&& (ok[2] = (arg[2].isGeoPoint()))) {
			GeoElement[] ret = { intersectLineConicSingle(c.getLabel(),
					(GeoLine) arg[1], (GeoConic) arg[0], (GeoPoint) arg[2]) };
			return ret;
		}
		// Conic - Conic with startPoint
		else if ((ok[0] = (arg[0].isGeoConic()))
				&& (ok[1] = (arg[1].isGeoConic()))
				&& (ok[2] = (arg[2].isGeoPoint()))) {
			GeoElement[] ret = { intersectConicsSingle(c.getLabel(),
					(GeoConic) arg[0], (GeoConic) arg[1], (GeoPoint) arg[2]) };
			return ret;
		}
		// Conic - Conic
		else if ((ok[0] = (arg[0].isGeoConic()))
				&& (ok[1] = (arg[1].isGeoConic()))
				&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
			GeoElement[] ret = {
					intersectConicsSingle(c.getLabel(), (GeoConic) arg[0],
							(GeoConic) arg[1], (GeoNumberValue) arg[2]) };
			return ret;
		}
		// Polynomial - Line with index of point
		// check before GeoFunctionable as GeoLine is now GeoFunctionable
		else if ((ok[0] = (arg[0].isRealValuedFunction()))
				&& (ok[1] = (arg[1].isGeoLine()))
				&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
			GeoPoint ret =

					intersectPolynomialLineSingle(c.getLabel(),
							(GeoFunctionable) arg[0],
							(GeoLine) arg[1], (GeoNumberValue) arg[2]);

			if (ret == null) {
				throw argErr(c, arg[0]);
			}

			return new GeoElement[] { ret };
		}
		// Line - Polynomial with index of point
		// check before GeoFunctionable as GeoLine is now GeoFunctionable
		else if ((ok[0] = (arg[0].isGeoLine()))
				&& (ok[1] = (arg[1].isRealValuedFunction()))
				&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
			GeoPoint ret =

					intersectPolynomialLineSingle(c.getLabel(),
							(GeoFunctionable) arg[1],
							(GeoLine) arg[0], (GeoNumberValue) arg[2]);

			if (ret == null) {
				throw argErr(c, arg[0]);
			}

			return new GeoElement[] { ret };
		}
		// Polynomial - Polynomial with index of point
		else if ((ok[0] = (arg[0].isRealValuedFunction()))
				&& (ok[1] = (arg[1].isRealValuedFunction()))
				&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
			GeoElement[] ret = { intersectPolynomialsSingle(c, arg,
					(GeoFunctionable) arg[0],
					(GeoFunctionable) arg[1],
					(GeoNumberValue) arg[2]) };
			return ret;
		}
		// Polynomial - Conic with index of point
		else if ((ok[0] = (arg[0].isGeoFunction()))
				&& (ok[1] = (arg[1].isGeoConic()))
				&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
			return new GeoElement[] { intersectPolynomialConicSingle(
					c.getLabel(), (GeoFunction) arg[0], (GeoConic) arg[1],
					(GeoNumberValue) arg[2]) };
		} else if ((ok[0] = (arg[0].isGeoConic()))
				&& (ok[1] = (arg[1].isGeoFunction()))
				&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
			return new GeoElement[] { intersectPolynomialConicSingle(
					c.getLabel(), (GeoFunction) arg[1], (GeoConic) arg[0],
					(GeoNumberValue) arg[2]) };
		} else if ((ok[0] = (arg[0].isGeoImplicitCurve()))
				&& (ok[1] = (arg[1].isGeoLine()))
				&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
			return new GeoElement[] { intersectImplicitpolyLineSingle(
					c.getLabel(), (GeoImplicit) arg[0], (GeoLine) arg[1],
					(GeoNumberValue) arg[2]) };
		} else if ((ok[1] = (arg[1].isGeoImplicitCurve()))
				&& (ok[0] = (arg[0].isGeoLine()))
				&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {
			return new GeoElement[] { intersectImplicitpolyLineSingle(
					c.getLabel(), (GeoImplicit) arg[1], (GeoLine) arg[0],
					(GeoNumberValue) arg[2]) };
		}
		// ImplicitPoly - Functionable
		// TODO decide polynomial before CAS loaded
		else if ((ok[0] = (arg[0].isGeoImplicitCurve()))
				&& (ok[1] = (arg[1].isRealValuedFunction())
						&& (ok[2] = (arg[2] instanceof GeoNumberValue))
						// this line uses CAS so check last
						&& (ok[1] = ((GeoFunctionable) arg[1])
								.isPolynomialFunction(false)))) {

			GeoPoint ret = intersectImplicitpolyPolynomialSingle(c.getLabel(),
					(GeoImplicit) arg[0],
					(GeoFunctionable) arg[1],
					(GeoNumberValue) arg[2]);

			if (ret == null) {
				throw argErr(c, arg[0]);
			}

			return new GeoElement[] { ret };
		} else if ((ok[0] = arg[0].isRealValuedFunction())
				&& (ok[1] = (arg[1].isGeoImplicitCurve()))
				&& (ok[2] = (arg[2] instanceof GeoNumberValue))
				// this line uses CAS so check last
				&& (ok[0] = ((GeoFunctionable) arg[0])
						.isPolynomialFunction(false))) {

			GeoPoint ret = intersectImplicitpolyPolynomialSingle(c.getLabel(),
					(GeoImplicit) arg[1],
					(GeoFunctionable) arg[0],
					(GeoNumberValue) arg[2]);

			if (ret == null) {
				throw argErr(c, arg[1]);
			}

			return new GeoElement[] { ret };
		}

		// implicitPoly - implicitPoly
		else if ((ok[0] = (arg[0].isGeoImplicitCurve()))
				&& (ok[1] = (arg[1].isGeoConic()))
				&& (ok[2] = arg[2] instanceof GeoNumberValue)) {
			return new GeoElement[] { intersectImplicitpolyConicSingle(
					c.getLabel(), (GeoImplicit) arg[0], (GeoConic) arg[1],
					(GeoNumberValue) arg[2]) };
		} else if ((ok[0] = (arg[0].isRealValuedFunction()))
				&& (ok[1] = (arg[1].isGeoLine()))
				&& (ok[2] = (arg[2].isGeoPoint()))) {
			GeoElement[] ret = { getAlgoDispatcher().intersectFunctionLine(
					c.getLabel(), (GeoFunctionable) arg[0],
					(GeoLine) arg[1], (GeoPoint) arg[2]) };
			return ret;
		}
		// Function - Function with startPoint
		else if ((ok[0] = (arg[0].isRealValuedFunction()
				|| arg[0] instanceof GeoFunction))
				&& (ok[1] = (arg[1].isRealValuedFunction()
						|| arg[1] instanceof GeoFunction))
				&& (ok[2] = (arg[2].isGeoPoint()))) {
			GeoElement[] ret = { getAlgoDispatcher().intersectFunctions(
					c.getLabel(), (GeoFunctionable) arg[0],
					(GeoFunctionable) arg[1],
					(GeoPoint) arg[2]) };
			return ret;
		}
		// Line - Function with startPoint
		else if ((ok[0] = (arg[0].isGeoLine()))
				&& (ok[1] = (arg[1].isRealValuedFunction()))
				&& (ok[2] = (arg[2].isGeoPoint()))) {
			GeoElement[] ret = { getAlgoDispatcher().intersectFunctionLine(
					c.getLabel(), (GeoFunctionable) arg[1],
					(GeoLine) arg[0], (GeoPoint) arg[2]) };
			return ret;
		}
		// polyLine - NonPolynomialFunction with startPoint
		else if ((ok[0] = (arg[0].isGeoPolyLine()))
				&& (ok[1] = (arg[1].isRealValuedFunction()))
				&& (ok[2] = (arg[2].isGeoPoint()))) {
			GeoElement[] ret = getAlgoDispatcher().intersectNPFunctionPolyLine(
					c.getLabels(), (GeoFunctionable) arg[1],
					(GeoPolyLine) arg[0], (GeoPoint) arg[2]);
			return ret;
		} else if ((ok[0] = (arg[0].isRealValuedFunction()))
				&& (ok[1] = (arg[1].isGeoPolyLine()))
				&& (ok[2] = (arg[2].isGeoPoint()))) {
			GeoElement[] ret = getAlgoDispatcher().intersectNPFunctionPolyLine(
					c.getLabels(), (GeoFunctionable) arg[0],
					(GeoPolyLine) arg[1], (GeoPoint) arg[2]);
			return ret;
		}
		// polygon - NonPolynomialFunction with startPoint
		else if ((ok[0] = (arg[0].isGeoPolygon()))
				&& (ok[1] = (arg[1].isRealValuedFunction()))
				&& (ok[2] = (arg[2].isGeoPoint()))) {
			GeoElement[] ret = getAlgoDispatcher().intersectNPFunctionPolygon(
					c.getLabels(), (GeoFunctionable) arg[1],
					(GeoPolygon) arg[0], (GeoPoint) arg[2]);
			return ret;
		} else if ((ok[0] = (arg[0].isRealValuedFunction()))
				&& (ok[1] = (arg[1].isGeoPolygon()))
				&& (ok[2] = (arg[2].isGeoPoint()))) {
			GeoElement[] ret = getAlgoDispatcher().intersectNPFunctionPolygon(
					c.getLabels(), (GeoFunctionable) arg[0],
					(GeoPolygon) arg[1], (GeoPoint) arg[2]);
			return ret;
		}

		// Syntax Error
		else {
			if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			} else {
				throw argErr(c, arg[2]);
			}
		}
	}

	/**
	 * get single intersection points of a implicitPoly and a line
	 * 
	 * @param idx
	 *            index of choosen point
	 */
	final private GeoPoint intersectImplicitpolyLineSingle(String label,
			GeoImplicit p, GeoLine l, GeoNumberValue idx) {
		AlgoIntersect algo = getAlgoDispatcher().getIntersectionAlgorithm(p, l);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get single intersection points of implicitPoly and conic
	 * 
	 * @param idx
	 *            index of choosen point
	 */
	final private GeoPoint intersectImplicitpolyConicSingle(String label,
			GeoImplicit p1, GeoConic c1, GeoNumberValue idx) {
		AlgoIntersectImplicitpolys algo = getAlgoDispatcher()
				.getIntersectionAlgorithm(p1, c1);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get single intersection points of two implicitPolys
	 * 
	 * @param idx
	 *            index of choosen point
	 */
	final private GeoPoint intersectImplicitpolysSingle(String label,
			GeoImplicit p1, GeoImplicit p2, GeoNumberValue idx) {
		AlgoIntersectImplicitpolys algo = getAlgoDispatcher()
				.getIntersectionAlgorithm(p1, p2);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get single intersection points of a implicitPoly and a line
	 * 
	 * @param idx
	 *            index of choosen point
	 */
	final private GeoPoint intersectImplicitpolyPolynomialSingle(String label,
			GeoImplicit p, GeoFunctionable f, GeoNumberValue idx) {
		if (!f.getConstruction().isFileLoading()
				&& !f.isPolynomialFunction(false)) {
			return null;
		}
		AlgoIntersect algo = getAlgoDispatcher().getIntersectionAlgorithm(p, f);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	final private GeoPoint intersectPolynomialConicSingle(String label,
			GeoFunction f, GeoConic c, GeoNumberValue idx) {
		AlgoIntersect algo = getAlgoDispatcher().getIntersectionAlgorithm(f, c);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, idx);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get only one intersection point of two conics
	 */
	final private GeoPoint intersectConicsSingle(String label, GeoConic a,
			GeoConic b, GeoPoint refPoint) {
		AlgoIntersectConics algo = getAlgoDispatcher()
				.getIntersectionAlgorithm(a, b); // index - 1
		// to start
		// at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo,
				refPoint);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get only one intersection point of two conics
	 */
	final private GeoPoint intersectConicsSingle(String label, GeoConic a,
			GeoConic b, GeoNumberValue index) {
		AlgoIntersectConics algo = getAlgoDispatcher()
				.getIntersectionAlgorithm(a, b); // index - 1
		// to start
		// at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get only one intersection point of line/Conic near to a given point
	 */
	final private GeoPoint intersectLineConicSingle(String label, GeoLine a,
			GeoConic b, GeoPoint refPoint) {
		AlgoIntersectLineConic algo = getAlgoDispatcher()
				.getIntersectionAlgorithm(a, b); // index -
		// 1 to
		// start
		// at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo,
				refPoint);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get only one intersection point of a line and a conic
	 */
	final private GeoPoint intersectLineConicSingle(String label, GeoLine g,
			GeoConic c, GeoNumberValue index) {
		AlgoIntersectLineConic algo = getAlgoDispatcher()
				.getIntersectionAlgorithm(g, c); // index -
		// 1 to
		// start
		// at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get only one intersection point of a line and a function
	 */
	final private GeoPoint intersectPolynomialLineSingle(String label,
			GeoFunctionable f, GeoLine l, GeoNumberValue index) {
		if (!f.getConstruction().isFileLoading()
				&& !f.isPolynomialFunction(false)) {
			return null;
		}

		AlgoIntersectPolynomialLine algo = getAlgoDispatcher()
				.getIntersectionAlgorithm(f, l);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo, index);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get only one intersection point of two polynomials a, b with given index
	 */
	/**
	 * get only one intersection point of two polynomials a, b with given index
	 */
	final private GeoPoint intersectPolynomialsSingle(Command c,
			GeoElement[] arg, GeoFunctionable a, GeoFunctionable b,
			GeoNumberValue index) {

		if (!a.getConstruction().isFileLoading()
				&& !a.isPolynomialFunction(false)) {
			throw argErr(c, arg[0]);
		}
		if (!b.getConstruction().isFileLoading()
				&& !b.isPolynomialFunction(false)) {
			throw argErr(c, arg[1]);
		}

		AlgoIntersectPolynomials algo = getAlgoDispatcher()
				.getIntersectionAlgorithm(a, b); // index
		// - 1
		// to
		// start
		// at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(c.getLabel(), algo,
				index);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * Intersects f and g in interval [left,right] numerically
	 */
	final private GeoPoint[] intersectFunctions(String[] labels,
			GeoFunctionable f, GeoFunctionable g, GeoNumberValue left,
			GeoNumberValue right) {
		AlgoIntersectFunctions algo = new AlgoIntersectFunctions(cons, labels,
				f, g, left, right);
		GeoPoint[] S = algo.getIntersectionPoints();
		return S;
	}
}