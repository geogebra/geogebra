package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoIntersect;
import geogebra.common.kernel.algos.AlgoIntersectConics;
import geogebra.common.kernel.algos.AlgoIntersectFunctions;
import geogebra.common.kernel.algos.AlgoIntersectLineConic;
import geogebra.common.kernel.algos.AlgoIntersectPolynomialLine;
import geogebra.common.kernel.algos.AlgoIntersectPolynomials;
import geogebra.common.kernel.algos.AlgoIntersectSingle;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolyLine;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.implicit.AlgoIntersectImplicitpolys;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.main.MyError;


/**
 * Intersect[ <GeoLine>, <GeoLine> ] Intersect[ <GeoLine>, <GeoPolygon> ] 
 * Intersect[ <GeoLine>, <GeoConic> ]
 * Intersect[ <GeoConic>, <GeoLine> ] Intersect[ <GeoConic>, <GeoConic> ]
 * Intersect[ <GeoFunction>, <GeoFunction> ] Intersect[ <GeoFunction>, <GeoLine> ]
 * Intersect[ <GeoImplicitPoly>, <GeoImplicitPoly> ] Intersect[ <GeoImplicitPoly>, <GeoLine> ]
 * Intersect[ <GeoImplicitPoly>, <GeoFunction(Polynomial)> ]
 * Intersect[ <GeoFunction>, <GeoFunction>, <NumberValue>, <NumberValue> ]
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
	public  GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		//    Application.debug(n,1);

		switch (n) {
		case 2 :
			arg = resArgs(c);
			// Line - Line
			if ((ok[0] = (arg[0] .isGeoLine()))
					&& (ok[1] = (arg[1] .isGeoLine()))) {
				GeoElement[] ret =
					{
						(GeoElement) kernelA.IntersectLines(
								c.getLabel(),
								(GeoLine) arg[0],
								(GeoLine) arg[1])};
				return ret;
			}
			// Line - Parametric Curve
			else if ((ok[0] = (arg[0] .isGeoLine()))
					&& (ok[1] = (arg[1] instanceof GeoCurveCartesian))) {
				GeoElement[] ret =
						kernelA.IntersectLineCurve(
								c.getLabels(),
								(GeoLine) arg[0],
								(GeoCurveCartesian) arg[1]);
				return ret;
			} else if ((ok[0] = (arg[0] instanceof GeoCurveCartesian))
					&& (ok[1] = (arg[1] .isGeoLine()))) {
				GeoElement[] ret =
						kernelA.IntersectLineCurve(
								c.getLabels(),
								(GeoLine) arg[1],
								(GeoCurveCartesian) arg[0]);
				return ret;
			}
			// Line - PolyLine
			else if ((ok[0] = (arg[0] .isGeoLine()))
					&& (ok[1] = (arg[1] instanceof GeoPolyLine))) {
				GeoElement[] ret =
						kernelA.IntersectLinePolyLine(
								c.getLabels(),
								(GeoLine) arg[0],
								(GeoPolyLine) arg[1]);
				return ret;
			} else if ((ok[0] = (arg[0] instanceof GeoPolyLine))
					&& (ok[1] = (arg[1] .isGeoLine()))) {
				GeoElement[] ret =
						kernelA.IntersectLinePolyLine(
								c.getLabels(),
								(GeoLine) arg[1],
								(GeoPolyLine) arg[0]);
				return ret;
			}
			// Line - Polygon(as boudary)
			else if ((ok[0] = (arg[0] .isGeoLine()))
					&& (ok[1] = (arg[1] .isGeoPolygon()))) {
				GeoElement[] ret =
						kernelA.IntersectLinePolygon(
								c.getLabels(),
								(GeoLine) arg[0],
								(GeoPolygon) arg[1]);
				return ret;
			} else if ((ok[0] = (arg[0] .isGeoPolygon()))
					&& (ok[1] = (arg[1] .isGeoLine()))) {
				GeoElement[] ret =
						kernelA.IntersectLinePolygon(
								c.getLabels(),
								(GeoLine) arg[1],
								(GeoPolygon) arg[0]);
				return ret;
			}

			// Line - Polygon(as region)
			// ---- see CmdIntersectionPaths

			// Line - Conic
			else if (
					(ok[0] = (arg[0] .isGeoLine()))
					&& (ok[1] = (arg[1] .isGeoConic())))
				return kernelA.IntersectLineConic(
						c.getLabels(),
						(GeoLine) arg[0],
						(GeoConic) arg[1]);
			else if (
					(ok[0] = (arg[0] .isGeoConic()))
					&& (ok[1] = (arg[1] .isGeoLine())))
				return kernelA.IntersectLineConic(
						c.getLabels(),
						(GeoLine) arg[1],
						(GeoConic) arg[0]);
			// Polynomial - Conic
			else if (
					(ok[0] = (arg[0] .isGeoFunction()))
					&& (ok[1] = (arg[1] .isGeoConic())))
				return kernelA.IntersectPolynomialConic(
						c.getLabels(),
						(GeoFunction) arg[0],
						(GeoConic) arg[1]);
			else if (
					(ok[0] = (arg[0] .isGeoConic()))
					&& (ok[1] = (arg[1] .isGeoFunction())))
				return kernelA.IntersectPolynomialConic(
						c.getLabels(),
						(GeoFunction) arg[1],
						(GeoConic) arg[0]);
			// Line - Cubic
			else if (
					(ok[0] = (arg[0] .isGeoConic()))
					&& (ok[1] = (arg[1] .isGeoConic())))
				return (GeoElement[]) kernelA.IntersectConics(
						c.getLabels(),
						(GeoConic) arg[0],
						(GeoConic) arg[1]);
			else if (
					(ok[0] = (arg[0] .isGeoFunctionable()))
					&& (ok[1] = (arg[1] .isGeoLine())))
				return kernelA.IntersectPolynomialLine(
						c.getLabels(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(GeoLine) arg[1]);
			else if (
					(ok[0] = (arg[0] .isGeoLine()))
					&& (ok[1] = (arg[1] .isGeoFunctionable())))
				return kernelA.IntersectPolynomialLine(
						c.getLabels(),
						((GeoFunctionable) arg[1]).getGeoFunction(),
						(GeoLine) arg[0]);
			else if ( // check after GeoLine as GeoLine is now GeoFunctionable
					(ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoFunctionable())))
				return kernelA.IntersectPolynomials(
						c.getLabels(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						((GeoFunctionable) arg[1]).getGeoFunction());
			//implicit Poly - Polynomial
			else if (
					(ok[0] = (arg[0] .isGeoImplicitPoly()))
					&& (ok[1] = (arg[1] .isGeoFunctionable())
					//&& (ok[1]=((GeoFunctionable) arg[1]).getGeoFunction().isPolynomialFunction(false))
					&& (ok[1] = !(arg[1].isGeoLine()))))
				return kernelA.IntersectImplicitpolyPolynomial(
						c.getLabels(), (GeoImplicitPoly) arg[0],
						((GeoFunctionable) arg[1]).getGeoFunction()
						);
			else if ((ok[0] = arg[0] .isGeoFunctionable())
					//&& (ok[0]=((GeoFunctionable) arg[0]).getGeoFunction().isPolynomialFunction(false))
					&& (ok[0] = !(arg[0].isGeoLine()))
					&& (ok[1] = arg[1] .isGeoImplicitPoly()))
				return kernelA.IntersectImplicitpolyPolynomial(
						c.getLabels(), (GeoImplicitPoly) arg[1],
						((GeoFunctionable) arg[0]).getGeoFunction()
						);
			//implicitPoly - Line
			else if (
					(ok[0] = (arg[0] .isGeoImplicitPoly()))
					&& (ok[1] = (arg[1].isGeoLine())))
				return kernelA.IntersectImplicitpolyLine(
						c.getLabels(), (GeoImplicitPoly) arg[0],
						(GeoLine) arg[1] );
			else if (
					(ok[1] = (arg[1] .isGeoImplicitPoly()))
					&& (ok[0] = (arg[0].isGeoLine())))
				return kernelA.IntersectImplicitpolyLine(
						c.getLabels(), (GeoImplicitPoly) arg[1],
						(GeoLine) arg[0] );
			//implicitPoly - implicitPoly
			else if (
					(ok[0] = (arg[0] .isGeoImplicitPoly()))
					&& (ok[1] = (arg[1].isGeoImplicitPoly())))
				return kernelA.IntersectImplicitpolys(
						c.getLabels(), (GeoImplicitPoly) arg[0],
						(GeoImplicitPoly) arg[1] );
			//implicitPoly-conic
			else if (
					(ok[0] = (arg[0] .isGeoImplicitPoly()))
					&& (ok[1] = (arg[1].isGeoConic())))
				return kernelA.IntersectImplicitpolyConic(
						c.getLabels(), (GeoImplicitPoly) arg[0],
						(GeoConic) arg[1] );
			else if (
					(ok[1] = (arg[1] .isGeoImplicitPoly()))
					&& (ok[0] = (arg[0].isGeoConic())))
				return kernelA.IntersectImplicitpolyConic(
						c.getLabels(), (GeoImplicitPoly) arg[1],
						(GeoConic) arg[0] );
			/* moved to CmdIntersection to allow Intersect[List, List] to intersect list elements in the future
			// intersection of two lists
			else if (arg[0].isGeoList() && arg[1].isGeoList() ) {
				GeoElement[] ret = { 
						kernelA.Intersection(c.getLabel(),
						(GeoList) arg[0], (GeoList)arg[1] ) };
				return ret;
			} */



			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
			}

		case 3 : // only one of the intersection points: the third argument
			// states which one
			arg = resArgs(c);
			// Line - Conic
			if ((ok[0] = (arg[0] .isGeoLine()))
					&& (ok[1] = (arg[1] .isGeoConic()))
					&& (ok[2] = (arg[2] .isNumberValue()))) {
				GeoElement[] ret =
					{
						IntersectLineConicSingle(
								c.getLabel(),
								(GeoLine) arg[0],
								(GeoConic) arg[1],
								(NumberValue) arg[2])};
				return ret;
			} else if (
					(ok[0] = (arg[0] .isGeoConic()))
					&& (ok[1] = (arg[1] .isGeoLine()))
					&& (ok[2] = (arg[2] .isNumberValue()))) {
				GeoElement[] ret =
					{
						IntersectLineConicSingle(
								c.getLabel(),
								(GeoLine) arg[1],
								(GeoConic) arg[0],
								(NumberValue) arg[2])};
				return ret;
			}
			// Line - Conic with startPoint
			else if ((ok[0] = (arg[0] .isGeoLine()))
					&& (ok[1] = (arg[1] .isGeoConic()))
					&& (ok[2] = (arg[2] .isGeoPoint()))) {
				GeoElement[] ret = {
						IntersectLineConicSingle(
								c.getLabel(),
								(GeoLine) arg[0],
								(GeoConic) arg[1],
								(GeoPoint) arg[2])};
				return ret;
			} else if (
					(ok[0] = (arg[0] .isGeoConic()))
					&& (ok[1] = (arg[1] .isGeoLine()))
					&& (ok[2] = (arg[2] .isGeoPoint()))) {
				GeoElement[] ret = {
						IntersectLineConicSingle(
								c.getLabel(),
								(GeoLine) arg[1],
								(GeoConic) arg[0],
								(GeoPoint) arg[2])};
				return ret;
			}
			// Conic - Conic with startPoint
			else if ((ok[0] = (arg[0] .isGeoConic()))
					&& (ok[1] = (arg[1] .isGeoConic()))
					&& (ok[2] = (arg[2] .isGeoPoint()))) {
				GeoElement[] ret = {
						IntersectConicsSingle(
								c.getLabel(),
								(GeoConic) arg[0],
								(GeoConic) arg[1],
								(GeoPoint) arg[2])};
				return ret;
			}
			// Conic - Conic
			else if (
					(ok[0] = (arg[0] .isGeoConic()))
					&& (ok[1] = (arg[1] .isGeoConic()))
					&& (ok[2] = (arg[2] .isNumberValue()))) {
				GeoElement[] ret =
					{
						IntersectConicsSingle(
								c.getLabel(),
								(GeoConic) arg[0],
								(GeoConic) arg[1],
								(NumberValue) arg[2])};
				return ret;
			}
			// Polynomial - Line with index of point
			// check before GeoFunctionable as GeoLine is now GeoFunctionable
			else if (
					(ok[0] = (arg[0] .isGeoFunctionable()))
					&& (ok[1] = (arg[1] .isGeoLine()))
					&& (ok[2] = (arg[2] .isNumberValue()))) {
				GeoElement[] ret =
					{
						IntersectPolynomialLineSingle(
								c.getLabel(),
								((GeoFunctionable) arg[0]).getGeoFunction(),
								(GeoLine) arg[1],
								(NumberValue) arg[2])};
				return ret;
			}
			// Polynomial - Polynomial with index of point
			else if (
					(ok[0] = (arg[0] .isGeoFunctionable()))
					&& (ok[1] = (arg[1] .isGeoFunctionable()))
					&& (ok[2] = (arg[2] .isNumberValue()))) {
				GeoElement[] ret =
					{
						IntersectPolynomialsSingle(
								c.getLabel(),
								((GeoFunctionable) arg[0]).getGeoFunction(),
								((GeoFunctionable) arg[1]).getGeoFunction(),
								(NumberValue) arg[2])};
				return ret;
			}
			// Line - Polynomial with index of point
			else if (
					(ok[0] = (arg[0] .isGeoLine()))
					&& (ok[1] = (arg[1] .isGeoFunctionable()))
					&& (ok[2] = (arg[2] .isNumberValue()))) {
				GeoElement[] ret =
					{
						IntersectPolynomialLineSingle(
								c.getLabel(),
								((GeoFunctionable) arg[1]).getGeoFunction(),
								(GeoLine) arg[0],
								(NumberValue) arg[2])};
				return ret;
			}
			//Polynomial - Conic with index of point
			else if (
					(ok[0] = (arg[0] .isGeoFunction()))
					&& (ok[1] = (arg[1] .isGeoConic()))
					&& (ok[2] = (arg[2] .isNumberValue())) )
				return new GeoElement[]{IntersectPolynomialConicSingle(
						c.getLabel(),
						(GeoFunction) arg[0],
						(GeoConic) arg[1],(NumberValue)arg[2])};
			else if (
					(ok[0] = (arg[0] .isGeoConic()))
					&& (ok[1] = (arg[1] .isGeoFunction()))
					&& (ok[2] = (arg[2] .isNumberValue())))
				return new GeoElement[]{IntersectPolynomialConicSingle(
						c.getLabel(),
						(GeoFunction) arg[1],
						(GeoConic) arg[0],(NumberValue)arg[2])};
			//ImplicitPoly - Functionable
			else if (
					(ok[0] = (arg[0] .isGeoImplicitPoly()))
					&& (ok[1] = (arg[1] .isGeoFunctionable())
					&& (ok[1]=((GeoFunctionable) arg[1]).getGeoFunction().isPolynomialFunction(false)))
					&& (ok[2] = (arg[2] .isNumberValue())))
				return new GeoElement[]{IntersectImplicitpolyPolynomialSingle(
						c.getLabel(), (GeoImplicitPoly) arg[0],
						((GeoFunctionable) arg[1]).getGeoFunction(),(NumberValue)arg[2]
						)};
			else if ((ok[0] =arg[0] .isGeoFunctionable())
					&& (ok[0]=((GeoFunctionable) arg[0]).getGeoFunction().isPolynomialFunction(false))
					&& (ok[1] = (arg[1] .isGeoImplicitPoly()))
					&& (ok[2] = (arg[2] .isNumberValue())))
				return new GeoElement[]{IntersectImplicitpolyPolynomialSingle(
						c.getLabel(), (GeoImplicitPoly) arg[0],
						((GeoFunctionable) arg[1]).getGeoFunction(),(NumberValue)arg[2]
						)};
			//implicitPoly - Line
			else if (
					(ok[0] = (arg[0] .isGeoImplicitPoly()))
					&& (ok[1] = (arg[1].isGeoLine()))
					&& (ok[2] = (arg[2] .isNumberValue())) )
				return new GeoElement[]{IntersectImplicitpolyLineSingle(
						c.getLabel(), (GeoImplicitPoly) arg[0],
						(GeoLine) arg[1] ,(NumberValue)arg[2])};
			else if (
					(ok[1] = (arg[1] .isGeoImplicitPoly()))
					&& (ok[0] = (arg[0].isGeoLine()))
					&& (ok[2] = (arg[2] .isNumberValue())))
				return new GeoElement[]{IntersectImplicitpolyLineSingle(
						c.getLabel(), (GeoImplicitPoly) arg[1],
						(GeoLine) arg[0] ,(NumberValue)arg[2])};
			//implicitPoly - implicitPoly
			else if (
					(ok[0] = (arg[0] .isGeoImplicitPoly()))
					&& (ok[1] = (arg[1].isGeoImplicitPoly()))
					&& (ok[2]=arg[2].isNumberValue()))
				return new GeoElement[]{IntersectImplicitpolysSingle(
						c.getLabel(), (GeoImplicitPoly) arg[0],
						(GeoImplicitPoly) arg[1], (NumberValue) arg[2] )};
			//implicitPoly-conic
			else if (
					(ok[0] = (arg[0] .isGeoImplicitPoly()))
					&& (ok[1] = (arg[1].isGeoConic()))
					&& (ok[2]=arg[2].isNumberValue()))
				return new GeoElement[]{IntersectImplicitpolyConicSingle(
						c.getLabel(), (GeoImplicitPoly) arg[0],
						(GeoConic) arg[1], (NumberValue) arg[2] )};
			else if (
					(ok[1] = (arg[1] .isGeoImplicitPoly()))
					&& (ok[0] = (arg[0].isGeoConic()))
					&& (ok[2]=arg[2].isNumberValue()))
				return new GeoElement[]{IntersectImplicitpolyConicSingle(
						c.getLabel(), (GeoImplicitPoly) arg[1],
						(GeoConic) arg[0], (NumberValue) arg[2] )};
			// Function - Line with startPoint
			// check before GeoFunctionable as GeoLine is now GeoFunctionable
			else if (
					(ok[0] = (arg[0] .isGeoFunctionable()))
					&& (ok[1] = (arg[1] .isGeoLine()))
					&& (ok[2] = (arg[2] .isGeoPoint()))) {
				GeoElement[] ret =
					{
						kernelA.IntersectFunctionLine(
								c.getLabel(),
								((GeoFunctionable) arg[0]).getGeoFunction(),
								(GeoLine) arg[1],
								(GeoPoint) arg[2])};
				return ret;
			}
			// Function - Function with startPoint
			else if (
					(ok[0] = (arg[0] .isGeoFunctionable()))
					&& (ok[1] = (arg[1] .isGeoFunctionable()))
					&& (ok[2] = (arg[2] .isGeoPoint()))) {
				GeoElement[] ret =
					{
						kernelA.IntersectFunctions(
								c.getLabel(),
								((GeoFunctionable) arg[0]).getGeoFunction(),
								((GeoFunctionable) arg[1]).getGeoFunction(),
								(GeoPoint) arg[2])};
				return ret;
			}
			// Line - Function with startPoint
			else if (
					(ok[0] = (arg[0] .isGeoLine()))
					&& (ok[1] = (arg[1] .isGeoFunctionable()))
					&& (ok[2] = (arg[2] .isGeoPoint()))) {
				GeoElement[] ret =
					{
						kernelA.IntersectFunctionLine(
								c.getLabel(),
								((GeoFunctionable) arg[1]).getGeoFunction(),
								(GeoLine) arg[0],
								(GeoPoint) arg[2])};
				return ret;
			}
			// Syntax Error
			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				else
					throw argErr(app, c.getName(), arg[2]);
			}

		case 4:
			arg = resArgs(c);
			// Function - Function in interval [a,b]
			// Polynomial - Polynomial with index of point
			if (
					(ok[0] = (arg[0] .isGeoFunction()))
					&& (ok[1] = (arg[1] .isGeoFunction()))
					&& (ok[2] = (arg[2] .isNumberValue()))
					&& (ok[3] = (arg[3] .isNumberValue()))
					) {
				GeoElement[] ret =
						IntersectFunctions(
								c.getLabels(),
								(GeoFunction) arg[0],
								(GeoFunction) arg[1],
								(NumberValue) arg[2],
								(NumberValue) arg[3]
								);
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok,arg));

		default :
			throw argNumErr(app, c.getName(), n);
		}
	}


	/**
	 * get single intersection points of a implicitPoly and a line
	 * 
	 * @param idx
	 *            index of choosen point
	 */
	final private GeoPoint IntersectImplicitpolyLineSingle(String label,
			GeoImplicitPoly p, GeoLine l, NumberValue idx) {
		AlgoIntersect algo = kernelA.getIntersectionAlgorithm(p, l);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo,
				(int) idx.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}

	/**
	 * get single intersection points of implicitPoly and conic
	 * 
	 * @param idx
	 *            index of choosen point
	 */
	final private GeoPoint IntersectImplicitpolyConicSingle(String label,
			GeoImplicitPoly p1, GeoConic c1, NumberValue idx) {
		AlgoIntersectImplicitpolys algo = kernelA.getIntersectionAlgorithm(p1, c1);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo,
				(int) idx.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/**
	 * get single intersection points of two implicitPolys
	 * 
	 * @param idx
	 *            index of choosen point
	 */
	final private GeoPoint IntersectImplicitpolysSingle(String label,
			GeoImplicitPoly p1, GeoImplicitPoly p2, NumberValue idx) {
		AlgoIntersectImplicitpolys algo = kernelA.getIntersectionAlgorithm(p1, p2);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo,
				(int) idx.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	
	/**
	 * get single intersection points of a implicitPoly and a line
	 * 
	 * @param idx
	 *            index of choosen point
	 */
	final private GeoPoint IntersectImplicitpolyPolynomialSingle(String label,
			GeoImplicitPoly p, GeoFunction f, NumberValue idx) {
		if (!f.isPolynomialFunction(false))
			return null;
		AlgoIntersect algo = kernelA.getIntersectionAlgorithm(p, f);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo,
				(int) idx.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	

	final private GeoPoint IntersectPolynomialConicSingle(String label,
			GeoFunction f, GeoConic c, NumberValue idx) {
		AlgoIntersect algo = kernelA.getIntersectionAlgorithm(f, c);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo,
				(int) idx.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	

	/**
	 * get only one intersection point of two conics
	 */
	final private GeoPoint IntersectConicsSingle(String label, GeoConic a,
			GeoConic b, GeoPoint refPoint) {
		AlgoIntersectConics algo = kernelA.getIntersectionAlgorithm(a, b); // index - 1
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
	final private GeoPoint IntersectConicsSingle(String label, GeoConic a,
			GeoConic b, NumberValue index) {
		AlgoIntersectConics algo = kernelA.getIntersectionAlgorithm(a, b); // index - 1
																	// to start
																	// at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo,
				(int) index.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	

	/**
	 * get only one intersection point of line/Conic near to a given point
	 */
	final private GeoPoint IntersectLineConicSingle(String label, GeoLine a,
			GeoConic b, GeoPoint refPoint) {
		AlgoIntersectLineConic algo = kernelA.getIntersectionAlgorithm(a, b); // index -
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
	final private GeoPoint IntersectLineConicSingle(String label, GeoLine g,
			GeoConic c, NumberValue index) {
		AlgoIntersectLineConic algo = kernelA.getIntersectionAlgorithm(g, c); // index -
																		// 1 to
																		// start
																		// at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo,
				(int) index.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	

	/**
	 * get only one intersection point of a line and a function
	 */
	final private GeoPoint IntersectPolynomialLineSingle(String label,
			GeoFunction f, GeoLine l, NumberValue index) {
		if (!f.isPolynomialFunction(false))
			return null;

		AlgoIntersectPolynomialLine algo = kernelA.getIntersectionAlgorithm(f, l);
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo,
				(int) index.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	

	/**
	 * get only one intersection point of two polynomials a, b with given index
	 */
	final private GeoPoint IntersectPolynomialsSingle(String label,
			GeoFunction a, GeoFunction b, NumberValue index) {
		if (!a.isPolynomialFunction(false) || !b.isPolynomialFunction(false))
			return null;

		AlgoIntersectPolynomials algo = kernelA.getIntersectionAlgorithm(a, b); // index
																		// - 1
																		// to
																		// start
																		// at 0
		AlgoIntersectSingle salgo = new AlgoIntersectSingle(label, algo,
				(int) index.getDouble() - 1);
		GeoPoint point = salgo.getPoint();
		return point;
	}
	

	/**
	 * Intersects f and g in interfal [left,right] numerically
	 */
	final private GeoPoint[] IntersectFunctions(String[] labels, GeoFunction f,
			GeoFunction g, NumberValue left, NumberValue right) {
		AlgoIntersectFunctions algo = new AlgoIntersectFunctions(cons, labels,
				f, g, left, right);
		GeoPoint[] S = algo.getIntersectionPoints();
		return S;
	}
}