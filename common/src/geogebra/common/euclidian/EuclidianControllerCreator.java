package geogebra.common.euclidian;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.algos.AlgoCirclePointRadius;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import geogebra.common.kernel.algos.AlgoMidpoint;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Class that creates geos for EuclidianController.
 * Needed for special 3D stuff.
 * 
 * @author mathieu
 *
 */
public class EuclidianControllerCreator {
	
	protected EuclidianController ec;
	
	public EuclidianControllerCreator(EuclidianController ec){
		this.ec = ec;
	}
	
	protected GeoAngle createAngle(GeoPointND A, GeoPointND B, GeoPointND C) {
		ec.checkZooming(); 
		
		return ec.getAlgoDispatcher().Angle(null, (GeoPoint) A, (GeoPoint) B, (GeoPoint) C);
	}

	protected GeoElement[] createAngles(GeoPolygon p){
		return ec.getAlgoDispatcher().Angles(null, p);
	}
	

	protected GeoAngle createAngle(GeoVectorND v1, GeoVectorND v2){
		return ec.getAlgoDispatcher().Angle(null, (GeoVector) v1, (GeoVector) v2);
	}
	

	public GeoAngle createAngle(GeoPointND A, GeoPointND B, GeoNumberValue num, boolean clockWise) {
		return (GeoAngle) ec.getAlgoDispatcher().Angle(null, (GeoPoint) A, (GeoPoint) B, num, !clockWise)[0];
	}
	

	
	protected GeoAngle createLineAngle(GeoLineND g, GeoLineND h){
		return ec.getAlgoDispatcher().createLineAngle((GeoLine) g, (GeoLine) h);
	}
	

	protected GeoElement[] translate(GeoElement geo, GeoVectorND vec) {
		ec.checkZooming(); 
		
		return ec.getAlgoDispatcher().Translate(null, geo, (GeoVector) vec);
	}
	

	protected GeoElement[] mirrorAtPoint(GeoElement geo, GeoPointND point){
		return ec.getAlgoDispatcher().Mirror(null, geo, (GeoPoint) point);		
	}



	protected GeoElement[] mirrorAtLine(GeoElement geo, GeoLineND line){
		return ec.getAlgoDispatcher().Mirror(null, geo, (GeoLine) line);		
	}
	

	public GeoElement[] dilateFromPoint(GeoElement geo, NumberValue num, GeoPointND point) {
		return ec.kernel.getAlgoDispatcher().Dilate(null,  geo, num, (GeoPoint) point);	
	}
	
	/**
	 * 
	 * @param a first geo
	 * @param b second geo
	 * @return single intersection points from geos a,b
	 */
	public GeoPointND getSingleIntersectionPoint(GeoElement a, GeoElement b, boolean coords2D) {
		GeoPointND point = null;
		
		// first hit is a line
		if (a.isGeoLine()) {
			if (b.isGeoLine()) {
				if (!((GeoLine) a).linDep((GeoLine) b)) {
					point = ec.getAlgoDispatcher()
							.IntersectLines(null, (GeoLine) a, (GeoLine) b);
				}else
					return null;
			} else if (b.isGeoConic()) {
				point = ec.getAlgoDispatcher().IntersectLineConicSingle(null, (GeoLine) a,
						(GeoConic) b, ec.xRW, ec.yRW);
			} else if (b.isGeoCurveCartesian()) { 
				return (GeoPointND) ec.getAlgoDispatcher().IntersectLineCurve(null, (GeoLine) a, 
						(GeoCurveCartesian) b)[0];
			} else if (b.isGeoFunctionable()) {
				// line and function
				GeoFunction f = ((GeoFunctionable) b).getGeoFunction();
				if (f.isPolynomialFunction(false)) {
					point = ec.getAlgoDispatcher().IntersectPolynomialLineSingle(null, f,
							(GeoLine) a, ec.xRW, ec.yRW);
				}
				GeoPoint initPoint = new GeoPoint(
						ec.kernel.getConstruction());
				initPoint.setCoords(ec.xRW, ec.yRW, 1.0);
				point = ec.getAlgoDispatcher().IntersectFunctionLine(null, f, (GeoLine) a,
						initPoint);
			} else {
				return null;
			}
		}
		// first hit is a conic
		else if (a.isGeoConic()) {
			if (b.isGeoLine()) {
				point = ec.getAlgoDispatcher().IntersectLineConicSingle(null, (GeoLine) b,
						(GeoConic) a, ec.xRW, ec.yRW);
			} else if (b.isGeoConic() && !a.isEqual(b)) {
				point = ec.getAlgoDispatcher().IntersectConicsSingle(null, (GeoConic) a,
						(GeoConic) b, ec.xRW, ec.yRW);
			} else {
				return null;
			}
		}
		// first hit is a function
		else if (a.isGeoFunctionable()) {
			GeoFunction aFun = ((GeoFunctionable) a).getGeoFunction();
			if (b.isGeoLine()) {
				// line and function
				if (aFun.isPolynomialFunction(false)) {
					point = ec.getAlgoDispatcher().IntersectPolynomialLineSingle(null, aFun,
							(GeoLine) b, ec.xRW, ec.yRW);
				} else {
					GeoPoint initPoint = new GeoPoint(
							ec.kernel.getConstruction());
					initPoint.setCoords(ec.xRW, ec.yRW, 1.0);
					point = ec.getAlgoDispatcher().IntersectFunctionLine(null, aFun,
							(GeoLine) b, initPoint);
				}
			} else if (b.isGeoFunctionable()) {
				GeoFunction bFun = ((GeoFunctionable) b).getGeoFunction();
				if (aFun.isPolynomialFunction(false)
						&& bFun.isPolynomialFunction(false)) {
					return ec.getAlgoDispatcher().IntersectPolynomialsSingle(null, aFun, bFun,
							ec.xRW, ec.yRW);
				}
				GeoPoint initPoint = new GeoPoint(
						ec.kernel.getConstruction());
				initPoint.setCoords(ec.xRW, ec.yRW, 1.0);
				point = ec.getAlgoDispatcher().IntersectFunctions(null, aFun, bFun,
						initPoint);
			} else {
				return null;
			}
		} else if (a.isGeoCurveCartesian()) { 
			if (b.isGeoCurveCartesian()) { 
				return (GeoPointND) ec.getAlgoDispatcher().IntersectCurveCurveSingle(null, (GeoCurveCartesian) a, 
						(GeoCurveCartesian) b, ec.xRW, ec.yRW)[0]; 
			} else if (b.isGeoLine()) { 
				return (GeoPointND) ec.getAlgoDispatcher().IntersectLineCurve(null, (GeoLine) b, 
						(GeoCurveCartesian) a)[0]; 
			}			
		} 

		if (point!=null) {
			if (!coords2D) {
				point.setCartesian3D();
				point.update();
			}
		}
		
		return point;
	}
	
	
	protected GeoElement[] orthogonal(GeoPointND point, GeoLineND line) {
		ec.checkZooming(); 
		
		return new GeoElement[] { ec.getAlgoDispatcher().OrthogonalLine(null,
				(GeoPoint) point, (GeoLine) line) };
	}
	
	/**
	 * 
	 * @param forPreviewable
	 * @param path
	 * @param x
	 * @param y
	 * @param z
	 * @param complex
	 * @param coords2D
	 * @return new point for the path
	 */
	public GeoPointND createNewPoint(String label, boolean forPreviewable, Path path, double x,
			double y, double z, boolean complex, boolean coords2D) {
		
		return ec.createNewPoint2D(label, forPreviewable, path, x, y, complex, coords2D);
	}
	
	

	/**
	 * 
	 * @param segment
	 * @return midpoint for segment
	 */
	protected GeoElement midpoint(GeoSegmentND segment){	

		return ec.getAlgoDispatcher().Midpoint(null, (GeoSegment) segment);

	}
	
	

	/**
	 * 
	 * @param conic
	 * @return center of conic
	 */
	protected GeoElement midpoint(GeoConicND conic){	

		return (GeoElement) ec.getAlgoDispatcher().Center(null, conic);

	}
	
	
	/**
	 * 
	 * @param p1 first point
	 * @param p2 second point
	 * @return midpoint for two points
	 */
	protected GeoElement midpoint(GeoPointND p1, GeoPointND p2){
		
		AlgoMidpoint algo = new AlgoMidpoint(ec.kernel.getConstruction(), (GeoPoint) p1, (GeoPoint) p2);
		return algo.getPoint();

	}
	
	
	/**
	 * 
	 * @param geoPoint1 first point
	 * @param geoPoint2 second point
	 * @param value n vertices
	 * @return regular polygon
	 */
	public GeoElement[] regularPolygon(GeoPointND geoPoint1, GeoPointND geoPoint2, GeoNumberValue value){
		ec.kernel.addingPolygon();
		GeoElement[] elms = ec.getAlgoDispatcher().RegularPolygon(null, geoPoint1, geoPoint2, value);
		ec.kernel.notifyPolygonAdded();
		return elms;
		//return kernel.getAlgoDispatcher().RegularPolygon(null, geoPoint1, geoPoint2, value);
	}
	
	
	/**
	 * 
	 * @param cons
	 * @param p1
	 * @param p2
	 * @return segment [p1 p2] algorithm
	 */
	protected AlgoElement segmentAlgo(Construction cons, GeoPointND p1, GeoPointND p2){
		return new AlgoJoinPointsSegment(cons, (GeoPoint) p1, (GeoPoint) p2, null);
	}
	
	
	protected GeoElement[] createCircle2(GeoPointND p0, GeoPointND p1) {

		return new GeoElement[] { ec.getAlgoDispatcher().Circle(null, (GeoPoint) p0,
				(GeoPoint) p1) };
	}
	
	protected GeoConicND circle(Construction cons, GeoPointND center, NumberValue radius){
		AlgoCirclePointRadius algo = new AlgoCirclePointRadius(cons, null, (GeoPoint) center, radius);
		return algo.getCircle();
	}
	
	protected GeoElement[] angularBisector(GeoLineND g, GeoLineND h){
		return ec.getAlgoDispatcher().AngularBisector(null, (GeoLine) g, (GeoLine) h);
	}
	
	protected GeoElement angularBisector(GeoPointND A, GeoPointND B, GeoPointND C){
		return ec.getAlgoDispatcher().AngularBisector(null, (GeoPoint) A, (GeoPoint) B, (GeoPoint) C);
	}
	
	protected GeoElement circleArc(GeoPointND p1, GeoPointND p2, GeoPointND p3){
		return ec.getAlgoDispatcher().CircleArc(null, (GeoPoint) p1,
				(GeoPoint) p2, (GeoPoint) p3);
	}
	
	protected GeoElement circleSector(GeoPointND p1, GeoPointND p2, GeoPointND p3){
		return ec.getAlgoDispatcher().CircleArc(null, (GeoPoint) p1,
				(GeoPoint) p2, (GeoPoint) p3);		
	}
	

	protected GeoElement circumcircleArc(GeoPointND p1, GeoPointND p2, GeoPointND p3){
		return ec.getAlgoDispatcher().CircumcircleArc(null, (GeoPoint) p1, (GeoPoint) p2, (GeoPoint) p3);
	}
	

	protected GeoElement circumcircleSector(GeoPointND p1, GeoPointND p2, GeoPointND p3){
		return ec.getAlgoDispatcher().CircumcircleSector(null, (GeoPoint) p1, (GeoPoint) p2, (GeoPoint) p3);
	}
}
