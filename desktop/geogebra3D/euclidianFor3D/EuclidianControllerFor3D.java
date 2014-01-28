package geogebra3D.euclidianFor3D;

import geogebra.common.geogebra3D.kernel3D.algos.AlgoJoinPoints3D;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoMidpoint3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.plugin.GeoClass;
import geogebra.euclidian.EuclidianControllerD;

/**
 * class for Euclidian Controller used in ggb3D
 * @author matthieu
 *
 */
public class EuclidianControllerFor3D extends EuclidianControllerD {

	/**
	 * constructor
	 * @param kernel kernel
	 */
	public EuclidianControllerFor3D(Kernel kernel) {
		super(kernel);
	}
	
	@Override
	protected GeoAngle createAngle(GeoPointND A, GeoPointND B, GeoPointND C){
		if (((GeoElement) A).isGeoElement3D() || ((GeoElement) B).isGeoElement3D() || ((GeoElement) C).isGeoElement3D()) {			
			return kernel.getManager3D().Angle3D(null, A, B, C);
		}
			
		return kernel.getAlgoDispatcher().Angle(null, (GeoPoint) A, (GeoPoint) B, (GeoPoint) C);
		
	}
	
	@Override
	protected GeoElement[] translate(GeoElement geo, GeoVectorND vec){
		if (geo.isGeoElement3D() || ((GeoElement) vec).isGeoElement3D()) {
			return kernel.getManager3D().Translate3D(null, geo, vec);
		}
			
		return super.translate(geo, vec);
		
	}
	
	
	@Override
	protected GeoElement[] mirrorAtPoint(GeoElement geo, GeoPointND point){
		if (geo.isGeoElement3D() || ((GeoElement) point).isGeoElement3D()) {
			return kernel.getManager3D().Mirror3D(null, geo, point);
		}
			
		return super.mirrorAtPoint(geo, point);
		
	}
	
	@Override
	protected GeoElement[] mirrorAtLine(GeoElement geo, GeoLineND line){
		if (geo.isGeoElement3D() || ((GeoElement) line).isGeoElement3D()) {
			return kernel.getManager3D().Mirror3D(null, geo, line);
		}
			
		return super.mirrorAtLine(geo, line);
		
	}
	
	
	

	
	

	@Override
	public GeoElement[] dilateFromPoint(GeoElement geo, NumberValue num, GeoPointND point) {
		
		if (geo.isGeoElement3D() || ((GeoElement) point).isGeoElement3D()) {
			return kernel.getManager3D().Dilate3D(null, geo, num, point);
		}
		
		return super.dilateFromPoint(geo, num, point);	
	}
	
	/*
	
	@Override
	protected boolean attach(GeoPointND p, Path path) {
		if (!((GeoElement) p).isGeoElement3D())
			return super.attach(p, path);
		return false;
	}
	
	@Override
	protected boolean attach(GeoPointND p, Region region) {
		if (!((GeoElement) p).isGeoElement3D())
			return super.attach(p, region);
		return false;
	}
	
	@Override
	protected boolean detach(GeoPointND p) {
		if (!((GeoElement) p).isGeoElement3D())
			return super.detach(p);
		return false;
	}
	
	*/
	
	/**
	 * Method used when geos are both 2D
	 * @param a first geo
	 * @param b second geo
	 * @return single intersection point
	 */
	protected GeoPointND getSingleIntersectionPointFrom2D(GeoElement a, GeoElement b, boolean coords2D) {
		return super.getSingleIntersectionPoint(a, b, coords2D);
	}

	
	@Override
	protected GeoPointND getSingleIntersectionPoint(GeoElement a, GeoElement b, boolean coords2D) {

		// check if a and b are two 2D geos
		if (!a.isGeoElement3D() && !b.isGeoElement3D())
			return getSingleIntersectionPointFrom2D(a, b, coords2D);
		
		
		GeoPointND point = null;
		
		// first hit is a line
		if (a.isGeoLine()) {
			if (b.isGeoLine()) {
				/*
				if (!((GeoLine) a).linDep((GeoLine) b)) {
					return kernel
							.IntersectLines(null, (GeoLine) a, (GeoLine) b);
				}
				*/
				point = (GeoPoint3D) getKernel().getManager3D().Intersect(null,  a,  b);
			} else if (b.isGeoConic()) {
				point = getKernel().getManager3D().IntersectLineConicSingle(null, 
    					(GeoLineND)a, (GeoConicND)b, xRW, yRW, view.getInverseMatrix());
			/*
			} else if (b.isGeoFunctionable()) {
				// line and function
				GeoFunction f = ((GeoFunctionable) b).getGeoFunction();
				if (f.isPolynomialFunction(false)) {
					return kernel.IntersectPolynomialLineSingle(null, f,
							(GeoLine) a, xRW, yRW);
				}
				GeoPoint2 initPoint = new GeoPoint2(
						kernel.getConstruction());
				initPoint.setCoords(xRW, yRW, 1.0);
				return kernel.IntersectFunctionLine(null, f, (GeoLine) a,
						initPoint);
			*/
			} else {
				return null;
			}
		}
		// first hit is a conic
		else if (a.isGeoConic()) {
			if (b.isGeoLine()) {
				point = getKernel().getManager3D().IntersectLineConicSingle(null, 
						(GeoLineND)b, (GeoConicND)a, xRW, yRW, view.getInverseMatrix());
			} else if (b.isGeoConic() && !a.isEqual(b)) {
				point = getKernel().getManager3D().IntersectConicsSingle(null, 
						(GeoConicND)a, (GeoConicND)b, xRW , yRW, view.getInverseMatrix());
			} else {
				return null;
			}
		}

		if (point!=null){
			if (coords2D){
				point.setCartesian();
			}else{
				point.setCartesian3D();
			}
			point.update();
		}
		
		return point;
		
	}



	@Override
	protected GeoElement[] orthogonal(GeoPointND point, GeoLineND line){
		return new GeoElement[] {(GeoElement) getKernel().getManager3D().OrthogonalLine3D(null,point, line, view.getDirection())};		

	}

	
	
	
	@Override
	public GeoPointND createNewPoint(String label, boolean forPreviewable, Path path, double x,
			double y, double z, boolean complex, boolean coords2D) {

		//check if the path is 3D geo or contains a 3D geo
		GeoElement geo = path.toGeoElement();
		if (geo.isGeoElement3D() || (geo.isGeoList() && ((GeoList) geo).containsGeoElement3D())) {
			checkZooming(forPreviewable); 

			GeoPointND point = kernel.getManager3D().Point3D(label, path, x, y, z,
					!forPreviewable, coords2D);

			return point;
		}
		
		//else use 2D 
		return createNewPoint2D(label, forPreviewable, path, x, y, complex, coords2D);
	}

	


	@Override
	protected GeoElement midpoint(GeoSegmentND segment){

		if (((GeoElement) segment).isGeoElement3D()) {
			return (GeoElement) kernel.getManager3D().Midpoint(null, segment);
		} 
		
		return super.midpoint(segment);

	}
	
	
	@Override
	protected GeoElement midpoint(GeoConicND conic){	

		if (((GeoElement) conic).isGeoElement3D()) {
			return (GeoElement) kernel.getManager3D().Center(null, conic);
		} 

		return super.midpoint(conic);

	}
	

	@Override
	protected GeoElement midpoint(GeoPointND p1, GeoPointND p2){

		if (((GeoElement) p1).isGeoElement3D()
				|| ((GeoElement) p2).isGeoElement3D()) {
			
			AlgoMidpoint3D algo = new AlgoMidpoint3D(kernel.getConstruction(), p1, p2);
			return algo.getPoint();
		}

		return super.midpoint(p1, p2);
	}

	
	
	@Override
	public GeoElement[] regularPolygon(GeoPointND geoPoint1, GeoPointND geoPoint2, GeoNumberValue value){
		
		if (geoPoint1.isGeoElement3D() || geoPoint2.isGeoElement3D()){
			return  kernel.getManager3D().RegularPolygon(null, geoPoint1, geoPoint2, value, view.getDirection());
		}
		
		return kernel.getAlgoDispatcher().RegularPolygon(null, geoPoint1, geoPoint2, value);
	}
	
	
	@Override
	protected AlgoElement segmentAlgo(Construction cons, GeoPointND p1, GeoPointND p2){
		if (p1.isGeoElement3D() || p2.isGeoElement3D()) {
			return new AlgoJoinPoints3D(cons, p1, p2, null, GeoClass.SEGMENT3D);
		}
		
		return super.segmentAlgo(cons, p1, p2);
	}
	
	@Override
	protected GeoElement[] createCircle2(GeoPointND p0, GeoPointND p1) {
		
		if (p0.isGeoElement3D() || p1.isGeoElement3D()) {
			return createCircle2For3D(p0, p1);
		}
		return new GeoElement[] { getAlgoDispatcher().Circle(null, (GeoPoint) p0,
				(GeoPoint) p1) };
	}
	
	/**
	 * 
	 * @param p0 center
	 * @param p1 point on circle
	 * @return circle in the current plane
	 */
	protected GeoElement[] createCircle2For3D(GeoPointND p0, GeoPointND p1) {
		return new GeoElement[] { kernel.getManager3D().Circle3D(null, p0, p1,
				view.getDirection()) };
	}
	
	@Override
	protected GeoConicND circle(Construction cons, GeoPointND center, NumberValue radius){
		if (center.isGeoElement3D()){
			return circleFor3D(cons, center, radius);
		}
		
		return super.circle(cons, center, radius);
	}
	
	/**
	 * 
	 * @param cons construction
	 * @param center center
	 * @param radius radius
	 * @return circle in the current plane
	 */
	protected GeoConicND circleFor3D(Construction cons, GeoPointND center, NumberValue radius){
		return kernel.getManager3D().Circle3D(null, center, radius, view.getDirection());
	}

}
