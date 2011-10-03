/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoRayND;


/**
 * @author Markus Hohenwarter
 */
final public class GeoRay extends GeoLine implements LimitedPath, GeoRayND {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean allowOutlyingIntersections = false;
	private boolean keepTypeOnGeometricTransform = true;
	
	/**
	 * Creates ray with start point A.
	 * @param c construction
	 * @param A start point
	 */
	public GeoRay(Construction c, GeoPoint A) {
		super(c);		
		setStartPoint(A);
	}
	
	public GeoRay(Construction c) {
		super(c);
	}

	public String getClassName() {	
		return "GeoRay";
 	}
	
	 protected String getTypeString() {
		return "Ray";
	}

	public int getGeoClassType() {
		return GEO_CLASS_RAY;
	}

	 
	/**
	 * the copy of a ray is an independent line
	 *
	public GeoElement copy() {
		return new GeoLine(this); 
	}*/
	 
	
	public GeoElement copyInternal(Construction cons) {
		GeoRay ray = new GeoRay(cons, (GeoPoint) startPoint.copyInternal(cons));
		ray.set(this);
		return ray;
	}
	
	public void set(GeoElement geo) {
		super.set(geo);	
		if (!geo.isGeoRay()) return;
		
		GeoRay ray = (GeoRay) geo;		
		keepTypeOnGeometricTransform = ray.keepTypeOnGeometricTransform; 
										
		startPoint.set((GeoElement) ray.startPoint);		
	}
	
	public void set(GeoPoint s, GeoVec3D direction) {
		super.set(direction);
		setStartPoint(s);
	}
	
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

	public void pathChanged(GeoPointND PI) {
		
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
	
	final public boolean isLimitedPath() {
		return true;
	}
	
    public boolean isIntersectionPointIncident(GeoPoint p, double eps) {
    	if (allowOutlyingIntersections)
			return isOnFullLine(p, eps);
		else
			return isOnPath(p, eps);
    }
      	
	/**
	 * Returns the smallest possible parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 * @return smallest possible parameter
	 */
	public double getMinParameter() {
		return 0;
	}
	
	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 * @return largest possible parameter
	 */
	public double getMaxParameter() {
		return Double.POSITIVE_INFINITY;
	}
	
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}
	
	/**
     * returns all class-specific xml tags for saveXML
     */
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

	public GeoElement [] createTransformedObject(Transform t,String label) {	
		AlgoElement algoParent = keepTypeOnGeometricTransform ?
				getParentAlgorithm() : null;				
		
		// CREATE RAY
		if (algoParent instanceof AlgoJoinPointsRay) {	
			//	transform points
			AlgoJoinPointsRay algo = (AlgoJoinPointsRay) algoParent;
			GeoPoint [] points = {algo.getP(), algo.getQ()};
			points = t.transformPoints(points);	
			if(t.isAffine()){
				GeoElement ray = kernel.Ray(label, points[0], points[1]);
				ray.setVisualStyleForTransformations(this);
				GeoElement [] geos = {ray, points[0], points[1]};
			return geos;
			}
			else {
				GeoPoint inf = new GeoPoint(cons);
				inf.setCoords(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
				inf = (GeoPoint)t.doTransform(inf);
				AlgoConicPartCircumcircle ae = new AlgoConicPartCircumcircle(cons, Transform.transformedGeoLabel(this),
			    		points[0], points[1],inf,GeoConicPart.CONIC_PART_ARC);
				cons.removeFromAlgorithmList(ae);
				GeoElement arc = ae.getConicPart(); 				
				arc.setVisualStyleForTransformations(this);
				GeoElement [] geos = {arc, points[0], points[1]};
				return geos;		
			}
		}
		else if (algoParent instanceof AlgoRayPointVector) {			
			// transform startpoint
			GeoPoint [] points = {getStartPoint()};
			points = t.transformPoints(points);					
						
			boolean oldSuppressLabelCreation = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			AlgoDirection ad = new AlgoDirection(cons,this);
			cons.removeFromAlgorithmList(ad);
			GeoVector direction = ad.getVector();
			if(t.isAffine()) {
				
				direction = (GeoVector)t.doTransform(direction);
				cons.setSuppressLabelCreation(oldSuppressLabelCreation);
				
				// ray through transformed point with direction of transformed line
				GeoElement ray = kernel.Ray(label, points[0], direction);
				ray.setVisualStyleForTransformations(this);
				GeoElement [] geos = new GeoElement[] {ray, points[0]};
				return geos;
			}else {
				AlgoTranslate at = new AlgoTranslate(cons,getStartPoint(),direction);
				cons.removeFromAlgorithmList(at);
				GeoPoint thirdPoint = (GeoPoint) at.getResult();
				GeoPoint inf = new GeoPoint(cons);
				inf.setCoords(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
						
				GeoPoint [] points2 = new GeoPoint[] {thirdPoint,inf};
				points2 = t.transformPoints(points2);
				cons.setSuppressLabelCreation(oldSuppressLabelCreation);
				AlgoConicPartCircumcircle ae = new AlgoConicPartCircumcircle(cons, Transform.transformedGeoLabel(this),
			    		points[0], points2[0],points2[1],GeoConicPart.CONIC_PART_ARC);
				GeoElement arc = ae.getConicPart(); 				
				arc.setVisualStyleForTransformations(this);
				GeoElement [] geos = {arc, points[0]};
				return geos;		
						
			}
			
							
			
		} else {
			//	create LINE	
			GeoElement transformedLine = t.getTransformedLine(this);
			transformedLine.setLabel(label);
			GeoElement [] ret = { transformedLine };
			return ret;
		}	
	}		
	
	public boolean isGeoRay() {
		return true;
	}
    // Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		// return false if it's a different type, otherwise check direction and start point
		if (!geo.isGeoRay()) return false;
		
		return isSameDirection((GeoLine)geo) && ((GeoRay)geo).getStartPoint().isEqual(getStartPoint());

	}
	
	
	
    public boolean isOnPath(Coords Pnd, double eps) {    	
    	Coords P2d = Pnd.getCoordsIn2DView();
    	if  (!super.isOnPath(P2d, eps))
    		return false;
    	
    	return respectLimitedPath(P2d, eps);
	   	
    }
    
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
    
    public GeoPoint getInnerPoint(){    	
    	
    	double nx = startPoint.x+y;
    	double ny = startPoint.y-x;
    	GeoPoint ret = new GeoPoint(cons);
    	ret.setCoords(nx, ny, 1);
    	if(!isOnPath(ret, Kernel.EPSILON)){
    		nx = startPoint.x-y;
        	ny = startPoint.y+x;
        	ret.setCoords(nx, ny, 1);
    	}
    	return ret;
    }
 
	
}
