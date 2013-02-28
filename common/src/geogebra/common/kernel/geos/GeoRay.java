/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.PathMover;
import geogebra.common.kernel.PathMoverGeneric;
import geogebra.common.kernel.PathParameter;
import geogebra.common.kernel.Transform;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoConicPartCircumcircle;
import geogebra.common.kernel.algos.AlgoDirection;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoJoinPointsRay;
import geogebra.common.kernel.algos.AlgoRayPointVector;
import geogebra.common.kernel.algos.AlgoTranslate;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoRayND;
import geogebra.common.plugin.GeoClass;


/**
 * @author Markus Hohenwarter
 */
final public class GeoRay extends GeoLine implements LimitedPath, GeoRayND {
	
	private boolean allowOutlyingIntersections = false;
	private boolean keepTypeOnGeometricTransform = true;
	
	/**
	 * Creates ray with start point A.
	 * @param c construction
	 * @param A start point
	 */
	public GeoRay(Construction c, GeoPoint A) {
		this(c);
		setStartPoint(A);
	}
	
	/**
	 * Creates new ray
	 * @param c construction
	 */
	public GeoRay(Construction c) {
		super(c);
		setConstructionDefaults();
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.RAY;
	}

	 
	/**
	 * the copy of a ray is an independent line
	 *
	public GeoElement copy() {
		return new GeoLine(this); 
	}*/
	 
	
	@Override
	public GeoElement copyInternal(Construction cons1) {
		GeoRay ray = new GeoRay(cons1, (GeoPoint) startPoint.copyInternal(cons1));
		ray.set(this);
		return ray;
	}
	
	@Override
	public void set(GeoElement geo) {
		super.set(geo);	
		if (!geo.isGeoRay()) return;
		
		GeoRay ray = (GeoRay) geo;		
		keepTypeOnGeometricTransform = ray.keepTypeOnGeometricTransform; 
										
		startPoint.set((GeoElement) ray.startPoint);		
	}
	
	/**
	 * Sets this ray using direction line and start point
	 * @param s start point
	 * @param direction line
	 */
	public void set(GeoPoint s, GeoVec3D direction) {
		super.set(direction);
		setStartPoint(s);
	}
	
	@Override
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);
		
		if (geo.isGeoRay()) { 
			GeoRay ray = (GeoRay) geo;
			allowOutlyingIntersections = ray.allowOutlyingIntersections;
		}
	}
	
	/* 
	 * Path interface
	 */	 
	@Override
	public void pointChanged(GeoPointND P) {
		super.pointChanged(P);
		
		// ensure that the point doesn't get outside the ray
		// i.e. ensure 0 <= t 
		PathParameter pp = P.getPathParameter();
		if (pp.t < 0.0) {
			P.setCoords2D(startPoint.x, startPoint.y,startPoint.z);
			P.updateCoordsFrom2D(false,null);
			pp.t = 0.0;
		} 
	}

	@Override
	public void pathChanged(GeoPointND PI) {
		
		//if kernel doesn't use path/region parameters, do as if point changed its coords
		if(!getKernel().usePathAndRegionParameters(PI)){
			pointChanged(PI);
			return;
		}
		
		GeoPoint P = (GeoPoint) PI;
		
		PathParameter pp = P.getPathParameter();
		if (pp.t < 0.0) {
			pp.t = 0;
		} 		
		
		// calc point for given parameter
		P.x = startPoint.inhomX + pp.t * y;
		P.y = startPoint.inhomY - pp.t * x;
		P.z = 1.0;		
	}
	
	public boolean allowOutlyingIntersections() {
		return allowOutlyingIntersections;
	}
	
	public void setAllowOutlyingIntersections(boolean flag) {
		allowOutlyingIntersections = flag;		
	}
	
	public boolean keepsTypeOnGeometricTransform() {		
		return keepTypeOnGeometricTransform;
	}

	public void setKeepTypeOnGeometricTransform(boolean flag) {
		keepTypeOnGeometricTransform = flag;
	}
	
	@Override
	final public boolean isLimitedPath() {
		return true;
	}
	
    @Override
	public boolean isIntersectionPointIncident(GeoPoint p, double eps) {
    	if (allowOutlyingIntersections)
			return isOnFullLine(p, eps);
		return isOnPath(p, eps);
    }
      	
	/**
	 * Returns the smallest possible parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 * @return smallest possible parameter
	 */
	@Override
	public double getMinParameter() {
		return 0;
	}
	
	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 * @return largest possible parameter
	 */
	@Override
	public double getMaxParameter() {
		return Double.POSITIVE_INFINITY;
	}
	
	@Override
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}
	
	/**
     * returns all class-specific xml tags for saveXML
     */
	@Override
	protected void getXMLtags(StringBuilder sb) {
        super.getXMLtags(sb);
		
        // allowOutlyingIntersections
        sb.append("\t<outlyingIntersections val=\"");
        sb.append(allowOutlyingIntersections);
        sb.append("\"/>\n");
        
        // keepTypeOnGeometricTransform
        sb.append("\t<keepTypeOnTransform val=\"");
        sb.append(keepTypeOnGeometricTransform);
        sb.append("\"/>\n");
 
    }

   
    /**
     * Creates a new ray using a geometric transform.
     * @param t transform
     */

	public GeoElement [] createTransformedObject(Transform t,String transformedLabel) {	
		AlgoElement parent = keepTypeOnGeometricTransform ?
				getParentAlgorithm() : null;				
		
		// CREATE RAY
		if (parent instanceof AlgoJoinPointsRay) {	
			//	transform points
			AlgoJoinPointsRay algo = (AlgoJoinPointsRay) parent;
			GeoPointND [] points = {algo.getP(), algo.getQ()};
			points = t.transformPoints(points);	
			if(t.isAffine()){
				GeoElement ray = (GeoElement) kernel.RayND(transformedLabel, points[0], points[1]);
				ray.setVisualStyleForTransformations(this);
				GeoElement [] geos = {ray, (GeoElement) points[0], (GeoElement) points[1]};
			return geos;
			}
			GeoPoint inf = new GeoPoint(cons);
			inf.setCoords(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
			inf = (GeoPoint)t.doTransform(inf);
			AlgoConicPartCircumcircle ae = new AlgoConicPartCircumcircle( cons, Transform.transformedGeoLabel(this),
					(GeoPoint) points[0], (GeoPoint) points[1],inf,GeoConicPart.CONIC_PART_ARC);
			cons.removeFromAlgorithmList(ae);
			GeoElement arc = ae.getConicPart();//GeoConicPart 				
			arc.setVisualStyleForTransformations(this);
			GeoElement [] geos = {arc, (GeoElement) points[0], (GeoElement) points[1]};
			return geos;
		}
		else if (parent instanceof AlgoRayPointVector) {			
			// transform startpoint
			GeoPointND [] points = {getStartPoint()};
			points = t.transformPoints(points);					
						
			boolean oldSuppressLabelCreation = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			AlgoDirection ad = new AlgoDirection( cons,this);
			cons.removeFromAlgorithmList(ad);
			GeoVector direction = ad.getVector();
			if(t.isAffine()) {
				
				direction = (GeoVector)t.doTransform(direction);
				cons.setSuppressLabelCreation(oldSuppressLabelCreation);
				
				// ray through transformed point with direction of transformed line
				GeoElement ray = kernel.getAlgoDispatcher().Ray(transformedLabel, (GeoPoint) points[0], direction);
				ray.setVisualStyleForTransformations(this);
				GeoElement [] geos = new GeoElement[] {ray, (GeoElement) points[0]};
				return geos;
			}
				AlgoTranslate at = new AlgoTranslate( cons,getStartPoint(),direction);
				cons.removeFromAlgorithmList(at);
				GeoPoint thirdPoint = (GeoPoint) at.getResult();
				GeoPoint inf = new GeoPoint(cons);
				inf.setCoords(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
						
				GeoPointND [] points2 = new GeoPointND[] {thirdPoint,inf};
				points2 = t.transformPoints(points2);
				cons.setSuppressLabelCreation(oldSuppressLabelCreation);
				AlgoConicPartCircumcircle ae = new AlgoConicPartCircumcircle(cons, Transform.transformedGeoLabel(this),
			    		(GeoPoint) points[0], (GeoPoint) points2[0], (GeoPoint) points2[1],GeoConicPart.CONIC_PART_ARC);
				GeoElement arc = ae.getConicPart();//GeoConicPart 				
				arc.setVisualStyleForTransformations(this);
				GeoElement [] geos = {arc, (GeoElement) points[0]};
				return geos;		
						
			
			
							
			
		} else {
			//	create LINE	
			GeoElement transformedLine = t.getTransformedLine(this);
			transformedLine.setLabel(transformedLabel);
			GeoElement [] ret = { transformedLine };
			return ret;
		}	
	}		
	
	@Override
	public boolean isGeoRay() {
		return true;
	}
    // Michael Borcherds 2008-04-30
	@Override
	final public boolean isEqual(GeoElement geo) {
		// return false if it's a different type, otherwise check direction and start point
		if (!geo.isGeoRay()) return false;
		
		return isSameDirection((GeoLine)geo) && ((GeoRay)geo).getStartPoint().isEqual(getStartPoint());

	}
	
	
	
    @Override
	public boolean isOnPath(Coords Pnd, double eps) {    	
    	Coords P2d = Pnd.getCoordsIn2DView();
    	if  (!super.isOnPath(P2d, eps))
    		return false;
    	
    	return respectLimitedPath(P2d, eps);
	   	
    }
    
    @Override
	public boolean respectLimitedPath(Coords Pnd, double eps) {    	
    	Coords P2d = Pnd.getCoordsIn2DView();
    	PathParameter pp = getTempPathParameter();
    	doPointChanged(P2d,pp);
    	double t = pp.getT();

    	return  t >= -eps;   	
    }
    
    public boolean isAllEndpointsLabelsSet() {
		return startPoint.isLabelSet();		
	} 
    
    @Override
	public boolean respectLimitedPath(double parameter){
		return Kernel.isGreaterEqual(parameter, 0);
	}

}
