package geogebra3D.euclidianFor3D;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.euclidian.EuclidianControllerD;
import geogebra3D.kernel3D.GeoPoint3D;

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
			
		return kernel.Translate(null, geo, (GeoVector) vec);
		
	}
	
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
	
	/**
	 * Method used when geos are both 2D
	 * @param a first geo
	 * @param b second geo
	 * @return single intersection point
	 */
	protected GeoPointND getSingleIntersectionPointFrom2D(GeoElement a, GeoElement b) {
		return super.getSingleIntersectionPoint(a, b);
	}

	
	@Override
	protected GeoPointND getSingleIntersectionPoint(GeoElement a, GeoElement b) {

		// check if a and b are two 2D geos
		if (!a.isGeoElement3D() && !b.isGeoElement3D())
			return getSingleIntersectionPointFrom2D(a, b);
		
		
		// first hit is a line
		if (a.isGeoLine()) {
			if (b.isGeoLine()) {
				/*
				if (!((GeoLine) a).linDep((GeoLine) b)) {
					return kernel
							.IntersectLines(null, (GeoLine) a, (GeoLine) b);
				}
				*/
				return (GeoPoint3D) getKernel().getManager3D().Intersect(null,  a,  b);
			} else if (b.isGeoConic()) {
				return getKernel().getManager3D().IntersectLineConicSingle(null, 
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
				return getKernel().getManager3D().IntersectLineConicSingle(null, 
						(GeoLineND)b, (GeoConicND)a, xRW, yRW, view.getInverseMatrix());
			} else if (b.isGeoConic() && !a.isEqual(b)) {
				return getKernel().getManager3D().IntersectConicsSingle(null, 
						(GeoConicND)a, (GeoConicND)b, xRW , yRW, view.getInverseMatrix());
			} else {
				return null;
			}
		}
		
		return null;
		
	}

	
	
}
