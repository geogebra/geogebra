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
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Transform;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.plugin.GeoClass;

/**
 * @author Markus Hohenwarter
 */
final public class GeoSegment extends GeoLine implements GeoSegmentND {

	// GeoSegment is constructed by AlgoJoinPointsSegment 
	//private GeoPoint A, B;
	private double length;
	private boolean defined;
	private boolean allowOutlyingIntersections = false;
	private boolean keepTypeOnGeometricTransform = true; // for mirroring, rotation, ...
	/** no decoration */
	public static final int SEGMENT_DECORATION_NONE = 0;
	/** one tick */
	public static final int SEGMENT_DECORATION_ONE_TICK = 1;
	/** two ticks */
	public static final int SEGMENT_DECORATION_TWO_TICKS = 2;
	/** three ticks */
	public static final int SEGMENT_DECORATION_THREE_TICKS = 3;
//	 Michael Borcherds 20071006 start
	/** one arrow */
	public static final int SEGMENT_DECORATION_ONE_ARROW = 4;
	/** two arrows */
	public static final int SEGMENT_DECORATION_TWO_ARROWS = 5;
	/** three arrows */
	public static final int SEGMENT_DECORATION_THREE_ARROWS = 6;
//	 Michael Borcherds 20071006 end
	
	// added by Loic
	/**
	 * Returns array of all decoration types
	 * @see #SEGMENT_DECORATION_ONE_TICK etc.
	 * @return array of all decoration types
	 */
	public static final Integer[] getDecoTypes() {
		Integer[] ret = { new Integer(SEGMENT_DECORATION_NONE),
				Integer.valueOf(SEGMENT_DECORATION_ONE_TICK),
				Integer.valueOf(SEGMENT_DECORATION_TWO_TICKS),
				Integer.valueOf(SEGMENT_DECORATION_THREE_TICKS),
// Michael Borcherds 20071006 start
				Integer.valueOf(SEGMENT_DECORATION_ONE_ARROW),
				Integer.valueOf(SEGMENT_DECORATION_TWO_ARROWS),
				Integer.valueOf(SEGMENT_DECORATION_THREE_ARROWS)
// Michael Borcherds 20071006 end
				};
		return ret;
	}
	//end		
	
//	 Michael Borcherds 2007-11-20
	@Override
	public void setDecorationType(int type) {
		if (type>=getDecoTypes().length || type<0)
			decorationType=DECORATION_NONE;
		else
			decorationType = type;
	}
//	 Michael Borcherds 2007-11-20

	/**
	 * Creates new segment
	 * @param c construction
	 * @param A first endpoint
	 * @param B second endpoint
	 */
	public GeoSegment(Construction c, GeoPoint A, GeoPoint B) {
		this(c);
		setPoints(A, B);
	}
	
	/**
	 * common constructor
	 * @param c construction
	 */
	public GeoSegment(Construction c){
		super(c);
		setConstructionDefaults();
	}
	
	/**
	 * sets start and end points
	 * @param A start point
	 * @param B end point
	 */
	public void setPoints(GeoPoint A, GeoPoint B){
		setStartPoint(A);
		setEndPoint(B);
	}
	
	public void setTwoPointsCoords(Coords start, Coords end) {
		this.startPoint.setCoords(start.get(1),start.get(2),start.get(3));
		this.endPoint.setCoords(end.get(1),end.get(2),end.get(3));
		setPoints(this.startPoint, this.endPoint);
		calcLength();
	}
	

	/**
	 * Sets start point and end point with inhom coords
	 * @param start start point
	 * @param end end point
	 */
	public void setTwoPointsInhomCoords(Coords start, Coords end) {
		this.startPoint.setCoords(start.get(1),start.get(2),1);
		this.endPoint.setCoords(end.get(1),end.get(2),1);
		setPoints(this.startPoint, this.endPoint);
		calcLength();
	}
	 
	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.SEGMENT;
	}

	/**
	 * the copy of a segment is a number (!) with
	 * its value set to the segments current length
	 *
	public GeoElement copy() {
		return new GeoNumeric(cons, getLength());   		 
	}   */     
	 
	@Override
	public GeoElement copyInternal(Construction cons1) {
		GeoSegment seg = new GeoSegment(cons1, 
										(GeoPoint) startPoint.copyInternal(cons1), 
										(GeoPoint) endPoint.copyInternal(cons1));
		seg.set(this);
		return seg;
	}		
	
	@Override
	public void set(GeoElement geo) {
		super.set(geo);		
		if (!geo.isGeoSegment()) return;
		
		GeoSegment seg = (GeoSegment) geo;				
        length = seg.length;
        defined = seg.defined;      
        keepTypeOnGeometricTransform = seg.keepTypeOnGeometricTransform; 	
    	    	     		   
    	startPoint.set((GeoElement) seg.startPoint);
    	endPoint.set((GeoElement) seg.endPoint);    	
	}   

	/**
	 * @param s start point
	 * @param e end point
	 * @param line line
	 */
	public void set(GeoPoint s, GeoPoint e, GeoVec3D line) {
		super.set(line);		
	
		setStartPoint(s);
		setEndPoint(e);
		calcLength();
	}   

	
	@Override
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);
		
		if (geo.isGeoSegment()) { 
			GeoSegment seg = (GeoSegment) geo;
			allowOutlyingIntersections = seg.allowOutlyingIntersections;						
		}
	}
	
	/** 
	 * Calculates this segment's length . This method should only be called by
	 * its parent algorithm of type AlgoJoinPointsSegment
	 */
	public void calcLength() {			
		defined = startPoint.isFinite() && endPoint.isFinite();
		if (defined) {
			length = startPoint.distance(endPoint);
			
			if (Kernel.isZero(length)) length = 0;
		}
		else {
			length = Double.NaN;	
		}
	}
	
	public double getLength() {
		return length;
	}
	
	/*
	 * overwrite GeoLine methods
	 */
	@Override
	public boolean isDefined() {
		return defined;
	}	
	
	@Override
	public void setUndefined() {
		super.setUndefined();
		length = Double.NaN;
		defined = false;
	}
        
   @Override
public final boolean showInAlgebraView() {	   
	  // return defined;
	   return true;
   }
   
   @Override
   public boolean showInEuclidianView() {
	   // segments of polygons can have thickness 0
	   return defined && lineThickness != 0;
   }
   
   
   /** 
	* Yields true iff startpoint and endpoint of s are equal to
	* startpoint and endpoint of this segment.
	*/
	// Michael Borcherds 2008-05-01
	@Override
	final public boolean isEqual(GeoElement geo) {
		if (!geo.isGeoSegment())
			return false;
		GeoSegment s = (GeoSegment) geo;
		return startPoint.isEqual(s.startPoint) && endPoint.isEqual(s.endPoint);
	}
	
	@Override
	final public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(kernel.format(length,tpl));
		return sbToString.toString();
	}

	@Override
	final public String toStringMinimal(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(regrFormat(length));
		return sbToString.toString();
   }      

   
   private StringBuilder sbToString = new StringBuilder(30);

   private boolean forceSimpleTransform;
   
   @Override
final public String toValueString(StringTemplate tpl) {
	   if(tpl.hasType(StringType.MPREDUCE))
		   return "myabs(subtraction("+getStartPoint().toValueString(tpl)+","+getEndPoint().toValueString(tpl)+"))";
	   return kernel.format(length,tpl);
   }
   
	 /**
     * interface NumberValue
     */    
    public MyDouble getNumber() {    	
        return new MyDouble(kernel,  getLength() );
    }     
    
    final public double getDouble() {
        return getLength();
    }
        
    @Override
	final public boolean isConstant() {
        return false;
    }

	@Override
	public boolean isNumberValue() {
		return true;
	}

	@Override
	public boolean isVectorValue() {
		return false;
	}

	@Override
	public boolean isPolynomialInstance() {
		return false;
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
    
    
	/* 
	 * GeoSegmentInterface interface
	 */	 
     
    public GeoElement getStartPointAsGeoElement(){
    	return getStartPoint();
    }

    public GeoElement getEndPointAsGeoElement(){
    	return getEndPoint();
    }

    
	public double getPointX(double parameter){
		return startPoint.inhomX + parameter * y;
	}
	
	public double getPointY(double parameter){
		return startPoint.inhomY - parameter * x;
	}

	
	/* 
	 * Path interface
	 */	     	
    @Override
	public void pointChanged(GeoPointND P) {
			
		PathParameter pp = P.getPathParameter();
		
		// special case: segment of length 0
		if (length == 0) {
			P.setCoords2D(startPoint.inhomX, startPoint.inhomY,1);
			P.updateCoordsFrom2D(false,null);
			if (!(pp.t >= 0 && pp.t <= 1)) {
				pp.t = 0.0;
			}
			return;
		}
		
		// project point on line
		super.pointChanged(P);
			
		// ensure that the point doesn't get outside the segment
		// i.e. ensure 0 <= t <= 1 
		if (pp.t < 0.0) {
			P.setCoords2D(startPoint.x, startPoint.y,startPoint.z);
			P.updateCoordsFrom2D(false,null);				
			pp.t = 0.0;
		} else if  (pp.t > 1.0) {
			P.setCoords2D(endPoint.x, endPoint.y,endPoint.z);
			P.updateCoordsFrom2D(false,null);
			pp.t = 1.0;
		}
	}

	@Override
	public void pathChanged(GeoPointND P) {
		
		//if kernel doesn't use path/region parameters, do as if point changed its coords
		if(!getKernel().usePathAndRegionParameters(this)){
			pointChanged(P);
			return;
		}
		
		PathParameter pp = P.getPathParameter();
		
		// special case: segment of length 0
		if (length == 0) {
			P.setCoords2D(startPoint.inhomX, startPoint.inhomY,1);
			P.updateCoordsFrom2D(false,null);
			if (!(pp.t >= 0 && pp.t <= 1)) {
				pp.t = 0.0;
			}
			return;
		}
		
		if (pp.t < 0.0) {
			pp.t = 0;
		} 
		else if (pp.t > 1.0) {
			pp.t = 1;
		}
		
		// calc point for given parameter
		P.setCoords2D(startPoint.inhomX + pp.t * y, startPoint.inhomY - pp.t * x,1);
		P.updateCoordsFrom2D(false,null);
	}
	
	/**
	 * Returns the smallest possible parameter value for this
	 * path.
	 * @return smallest possible parameter
	 */
	@Override
	public double getMinParameter() {
		return 0;
	}
	
	/**
	 * Returns the largest possible parameter value for this
	 * path.
	 * @return largest possible parameter
	 */
	@Override
	public double getMaxParameter() {
		return 1;
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
	 * creates new transformed segment
	 */
    public GeoElement [] createTransformedObject(Transform t,String transformedLabel) {	

		if (keepTypeOnGeometricTransform && t.isAffine()) {			
			// mirror endpoints
			GeoPointND [] points = {getStartPoint(), getEndPoint()};
			points = t.transformPoints(points);	
			// create SEGMENT
			GeoElement segment = (GeoElement) kernel.SegmentND(transformedLabel, points[0], points[1]);
			segment.setVisualStyleForTransformations(this);
			GeoElement [] geos = {segment, (GeoElement) points[0], (GeoElement) points[1]};	
			return geos;	
		} 
		else if(!t.isAffine()) {			
			// mirror endpoints
			
			//boolean oldSuppressLabelCreation = cons.isSuppressLabelsActive();
			//cons.setSuppressLabelCreation(true);
			
			this.forceSimpleTransform = true;
			GeoElement [] geos = {t.transform(this, transformedLabel)[0]};
			return geos;	
		} 
		else {
			//	create LINE
			GeoElement transformedLine = t.getTransformedLine(this);
			transformedLine.setLabel(transformedLabel);
			transformedLine.setVisualStyleForTransformations(this);
			GeoElement [] geos = {transformedLine};
			return geos;
		}							
	}
	
	@Override
	public boolean isGeoSegment() {
		return true;
	}	
	
    @Override
	public void setZero() {
    	setCoords(0, 1, 0);
    }
    
    

	//////////////////////////////////////
	// 3D stuff
	//////////////////////////////////////
	
  	@Override
	public boolean hasDrawable3D() {
		return true;
	}
    
  	@Override
	public Coords getLabelPosition(){
		return new Coords(getPointX(0.5), getPointY(0.5), 0, 1);
	}
  	
  	@Override
	public Coords getPointInD(int dimension, double lambda){

		switch(dimension){
		case 3:
			return new Coords(getPointX(lambda), getPointY(lambda), 0, 1);
		case 2:
			return new Coords(getPointX(lambda), getPointY(lambda), 1);
		default:
			return null;
		}
	}
  	
  	/**
  	 * returns the paramter for the closest point to P on the Segment (extrapolated)
  	 * so answers can be returned outside the range [0,1]
  	 * @param ptx point x-coord
  	 * @param pty point y-coord
  	 * @return closest parameter
  	 */
  	final public double getParameter(double ptx, double pty){
  		double px=ptx;
  		double py=pty;
		// project P on line
		// param of projection point on perpendicular line
		double t = -(z + x*px + y*py) / (x*x + y*y); 
		// calculate projection point using perpendicular line
		px += t * x;
		py += t * y;
						
		// calculate parameter
		if (Math.abs(x) <= Math.abs(y)) {	
			return (startPoint.z * px - startPoint.x) / (y * startPoint.z);								
		}
		return (startPoint.y - startPoint.z * py) / (x * startPoint.z);
  		
  	}
  	
    /** Calculates the euclidian distance between this GeoSegment and GeoPoint P.
     * 
     * returns distance from endpoints if appropriate
     */
    @Override
	final public double distance(GeoPoint p) {     

    	double t = getParameter(p.inhomX, p.inhomY);

    	// if t is outside the range [0,1] then the closest point is not on the Segment
		if (t < 0) return p.distance(startPoint);
    	if (t > 1) return p.distance(endPoint);
    	
    	return super.distance(p);
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

    	return  t >= -eps &&  t <= 1 + eps;   	
    }
    
    /**
     * exact calculation for checking if point is on Segment[segStart,segEnd]
     * @param segStart start coords
     * @param segEnd end coords
     * @param point point to be checked
     * @param checkOnFullLine - if true, do extra calculation to make sure.
     * @param eps precision
     * @return true if point belongs to segment
     */
    public static boolean checkOnPath(Coords segStart, Coords segEnd,
    		Coords point, boolean checkOnFullLine, double eps) {
    	if (checkOnFullLine) {
    		if (segEnd.sub(segStart).crossProduct(
    				point.sub(segStart)).equalsForKernel(
    						new Coords(0,0,0), Kernel.EPSILON))
    			return false;
    	}
    	
    	double x1 = segStart.getInhomCoords().get(1);
    	double x2 = segEnd.getInhomCoords().get(1);
    	double x = point.getInhomCoords().get(1);
    	if ( x1-eps <= x2 && x2 <= x1 + eps ) {
       		double y1 = segStart.getInhomCoords().get(2);
       		double y2 = segEnd.getInhomCoords().get(2);
       		double y = point.getInhomCoords().get(2);
       		
       		if (y1-eps <= y2 && y2 <= y1 + eps) {
       			return true;
       		}
			return y1-eps<=y && y<=y2+eps || y2-eps<=y && y<=y1+eps;
       		
   		}
		return x1-eps <=x && x<=x2+eps || x2-eps<=x && x<=x1+eps;
    }

	public boolean isAllEndpointsLabelsSet() {
		return !forceSimpleTransform && startPoint.isLabelSet() && endPoint.isLabelSet();		
	}
	
	


    public void modifyInputPoints(GeoPointND P, GeoPointND Q){
    	AlgoJoinPointsSegment algo = (AlgoJoinPointsSegment) getParentAlgorithm();
    	algo.modifyInputPoints((GeoPoint) P, (GeoPoint) Q);    	
    }
    

	private GeoElement meta = null;
	
	@Override
	public boolean hasMeta() {
		return meta!=null;
	}
	
	public GeoElement getMeta(){
		return meta;
	}

	/**
	 * @param poly polygon or polyhedron creating this segment
	 */
	public void setFromMeta(GeoElement poly) {
		meta = poly;
	}
	
    @Override
	public boolean respectLimitedPath(double parameter){
		return Kernel.isGreaterEqual(parameter, 0) && Kernel.isGreaterEqual(1, parameter);
	}
    
    
    
    

}
