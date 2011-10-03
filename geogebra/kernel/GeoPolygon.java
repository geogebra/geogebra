/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.main.Application;
import geogebra.util.MyMath;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Polygon through given points
 * 
 * @author Markus Hohenwarter
 */
public class GeoPolygon extends GeoElement implements NumberValue, Path, Region, GeoSurfaceFinite, Traceable,Rotateable,PointRotateable,
MatrixTransformable,Mirrorable,Translateable,Dilateable,GeoCoordSys2D,GeoPolyLineInterface, Transformable{
	
	private static final long serialVersionUID = 1L;

	/** maximal number of vertices for polygon tool */
	public static final int POLYGON_MAX_POINTS = 100;
	/** polygon vertices */
	protected GeoPointND [] points;
	/** polygon edges */
	protected GeoSegmentND [] segments;
	
	
	/** first point for region coord sys */
	protected GeoPoint p0;
	/** second point for region coord sys */
	protected GeoPoint p1;
	/** third point for region coord sys */
	protected GeoPoint p2;
	/** number of points in coord sys */
	protected int numCS = 0;
	

	/** directed area */
	protected double area;
	private boolean defined = false;		
	private boolean initLabelsCalled = false;
	
	/** says if the polygon had created its segments itself (used for 3D) */
	private boolean createSegments = true;
	
	/** common constructor for 2D.
	 * @param c the construction
	 * @param points vertices 
	 */
	public GeoPolygon(Construction c, GeoPointND[] points) {
		this(c,points,null,true);
	}
	
	/** common constructor for 3D.
	 * @param c the construction
	 * @param points vertices 
	 * @param cs for 3D stuff : 2D coord sys
	 * @param createSegments says if the polygon has to creates its edges
	 */	
	public GeoPolygon(Construction c, GeoPointND[] points, CoordSys cs, boolean createSegments) {
		this(c);
		//Application.printStacktrace("poly");
		this.createSegments=createSegments;
		setPoints(points, cs, createSegments);
		setLabelVisible(false);
		setAlphaValue(ConstructionDefaults.DEFAULT_POLYGON_ALPHA);
	}
	
	/**
	 * Creates new GeoPolygon
	 * @param cons construction
	 */
	public GeoPolygon(Construction cons) {
		super(cons);
		
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		//http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

	}

	/** for 3D stuff (unused here)
	 * @param cs GeoCoordSys2D
	 */
	public void setCoordSys(CoordSys cs) {
		
	}
	
	
	

	public String getClassName() {
		return "GeoPolygon";
	}
	
    protected String getTypeString() {
    	if (points == null) 
    		return "Polygon";
    	
    	switch (points.length) {
    		case 3:
    			return "Triangle";
    			
    		case 4:
    			return "Quadrilateral";
    			
    		case 5:
    			return "Pentagon";
    		
    		case 6:    			
    			return "Hexagon";
    		
    		default:
    			return "Polygon";    	
    	}    	    			
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_POLYGON;
    }
	
    
    
    
    /**
     * set the vertices to points
     * @param points the vertices
     */
    public void setPoints(GeoPointND [] points) {
    	setPoints(points,null,true);
    }

    	
    /**
     * set the vertices to points (cs is only used for 3D stuff)
     * @param points the vertices
     * @param cs used for 3D stuff
     * @param createSegments says if the polygon has to creates its edges
     */
    public void setPoints(GeoPointND [] points, CoordSys cs, boolean createSegments) {
		this.points = points;
		setCoordSys(cs);
		
		if (createSegments)
			updateSegments();
		
//		if (points != null) {
//		Application.debug("*** " + this + " *****************");
//        Application.debug("POINTS: " + points.length);
//        for (int i=0; i < points.length; i++) {
//			Application.debug(" " + i + ": " + points[i]);		     	        	     	
//		}   
//        Application.debug("SEGMENTS: " + segments.length);
//        for (int i=0; i < segments.length; i++) {
//			Application.debug(" " + i + ": " + segments[i]);		     	        	     	
//		}  
//        Application.debug("********************");
//		}
	}    
    
    
    
    ///////////////////////////////
    // ggb3D 2009-03-08 - start
    
	/** return number for points
	 * @return number for points
	 */
	public int getPointsLength(){
		if (points==null) //TODO remove this (preview bug)
			return 0;
		return points.length;
	}
	
	
	/**
	 * return the x-coordinate of the i-th vertex
	 * @param i number of vertex
	 * @return the x-coordinate 
	 */
	public double getPointX(int i){
		return getPoint(i).inhomX;
	}
	
	/**
	 * return the y-coordinate of the i-th vertex
	 * @param i number of vertex
	 * @return the y-coordinate 
	 */
	public double getPointY(int i){
		return getPoint(i).inhomY;
	}

	
    // ggb3D 2009-03-08 - end
    ///////////////////////////////
	


    /**
     * Inits the labels of this polygon, its segments and its points.
     * labels[0] for polygon itself, labels[1..n] for segments,
     * labels[n+1..2n-2] for points (only used for regular polygon)
     * @param labels
     */
    void initLabels(String [] labels) {    
    	if(cons.isSuppressLabelsActive())
    		return;
    	initLabelsCalled = true;
    	// Application.debug("INIT LABELS");
    	
    	// label polygon
    	if (labels == null || labels.length == 0) {    		
        	// Application.debug("no labels given");
        	
             setLabel(null);
             if (segments != null) {
            	 defaultSegmentLabels();
             }
             return;
    	}    	    	
    	
    	// label polygon              
        // first label for polygon itself
        setLabel(labels[0]);    

    	// label segments and points
    	if (points != null && segments != null) {
    		
    		// additional labels for the polygon's segments
    		// poly + segments + points - 2 for AlgoPolygonRegular
    		if (labels.length == 1 + segments.length + points.length - 2) {
    			//Application.debug("labels for segments and points");
    			
	            int i=1;
    			for (int k=0; k < segments.length; k++, i++) {
	               segments[k].setLabel(labels[i]);    				
	             }		            
    			for (int k=2; k < points.length; k++, i++) {    				
	                points[k].setLabel(labels[i]);
	            }
	        } 
    		    		
    		// additional labels for the polygon's segments
    		// poly + segments for AlgoPolygon
    		else if (labels.length == 1 + segments.length) {
    			//Application.debug("labels for segments");
    			
            	int i=1;
    			for (int k=0; k < segments.length; k++, i++) {
	                segments[k].setLabel(labels[i]);
	            }		            	           	            
	        } 
    		
	        else { 
	        	//Application.debug("label for polygon (autoset segment labels)");     	
	        	defaultSegmentLabels();
	        }
    	}    	
    	

    }
    
    /**
     * Returns whether the method initLabels() was called for this polygon.
     * This is important to know whether the segments have gotten labels.
     * @return true iff the method initLabels() was called for this polygon.
     */
    final public boolean wasInitLabelsCalled() {
    	return initLabelsCalled;
    }
    
    private void defaultSegmentLabels() {
    	//  no labels for segments specified
        //  set labels of segments according to point names
        if (points.length == 3) {    
        	
        	// make sure segment opposite C is called c not a_1
        	if (getParentAlgorithm() instanceof AlgoPolygonRegular)
        		points[2].setLabel(null);
        	
           setLabel(segments[0], points[2]);
           setLabel(segments[1], points[0]);
           setLabel(segments[2], points[1]); 
        } else {
           for (int i=0; i < points.length; i++) {
               setLabel(segments[i], points[i]);
           }
        }       
    }
    
    /**
     * Sets label of segment to lower case label of point.
     * If the point has no label, a default label is used for the segment.
     * If the lower case label of the point is already used, an indexed label
     * is created.
     */
    private void setLabel(GeoSegmentND s, GeoPointND p) {
        if (!p.isLabelSet() || p.getLabel() == null) {
        	s.setLabel(null);
        } else {
        	// use lower case of point label as segment label
        	String lowerCaseLabel = ((GeoElement)p).getFreeLabel(p.getLabel().toLowerCase());
        	s.setLabel(lowerCaseLabel);
        }
    }
	
    /**
     * Updates all segments of this polygon for its point array.
     * Note that the point array may be changed: this method makes
     * sure that segments are reused if possible.
     */
	 private void updateSegments() {  	
		 if (points == null) return;
		 
		GeoSegmentND [] oldSegments = segments;				
		segments = new GeoSegmentND[points.length]; // new segments
				
		if (oldSegments != null) {
			// reuse or remove old segments
			for (int i=0; i < oldSegments.length; i++) {        	
	        	if (i < segments.length &&
	        		oldSegments[i].getStartPointAsGeoElement() == points[i] && 
	        		oldSegments[i].getEndPointAsGeoElement() == points[(i+1) % points.length]) 
	        	{		
        			// reuse old segment
        			segments[i] = oldSegments[i];        		
         		} 
	        	else {
        			// remove old segment
        			//((AlgoJoinPointsSegment) oldSegments[i].getParentAlgorithm()).removeSegmentOnly();
        			removeSegment(oldSegments[i]);
	        	}	        	
			}
		}			
		
		// make sure segments created visible if appropriate
		setDefined();
		
		// create missing segments
        for (int i=0; i < segments.length; i++) {
        	GeoPointND startPoint = points[i];
        	GeoPointND endPoint = points[(i+1) % points.length];
        	
        	if (segments[i] == null) {
         		segments[i] = createSegment(startPoint, endPoint, i == 0 ? isEuclidianVisible() : segments[0].isEuclidianVisible());
        	}     
        }         
        
    }
	 
	 

	 /**
	  * remove an old segment
	  * @param oldSegment the old segment 
	  */
	 public void removeSegment(GeoSegmentND oldSegment){	
		 AlgoElement parentAlgo = ((GeoSegment) oldSegment).getParentAlgorithm();
		 //if this polygon is Polygon[<list of points>], we don't do anything
		 if(parentAlgo instanceof AlgoJoinPointsSegment)
			 ((AlgoJoinPointsSegment) parentAlgo).removeSegmentOnly();
	 }
	 
	 
	 
	 /**
	  * return a segment joining startPoint and endPoint
	  * @param startPoint the start point
	  * @param endPoint the end point
	  * @param euclidianVisible
	  * @return the segment
	  */
	 public GeoSegmentND createSegment(GeoPointND startPoint, GeoPointND endPoint, boolean euclidianVisible){

		 AlgoJoinPointsSegment algoSegment = new AlgoJoinPointsSegment(cons, (GeoPoint) startPoint, (GeoPoint) endPoint, this);            
		 cons.removeFromConstructionList(algoSegment);  
		 
		 return createSegment(algoSegment.getSegment(), euclidianVisible);
	 }


	 /**
	  * ends the creation of the segment
	  * @param segment
	  * @param euclidianVisible
	  * @return the segment modified
	  */
	 protected GeoSegmentND createSegment(GeoSegmentND segment, boolean euclidianVisible){
		 // refresh color to ensure segments have same color as polygon:
		 segment.setObjColor(getObjectColor()); 
		 segment.setLineThickness(getLineThickness()); 
		 segment.setEuclidianVisible(euclidianVisible);
		 
		if (condShowObject != null) {
			try { ((GeoElement)segment).setShowObjectCondition(getShowObjectCondition());}
			catch (Exception e) {}
		}

		 segment.setHighlightingAncestor(this);

		 return segment;
	 }

	 

	/**
	 * The copy of a polygon is a number (!) with
	 * its value set to the polygons current area
	 */      
	public GeoElement copy() {
		return new GeoNumeric(cons, getArea());        
	}    
	
	public GeoElement copyInternal(Construction cons) {						
		GeoPolygon ret = newGeoPolygon(cons);
		ret.points = copyPoints(cons);		
		ret.set(this);
				
		return ret;		
	} 	
	
	protected GeoPolygon newGeoPolygon(Construction cons){
		return new GeoPolygon(cons, null); 
	}
	
	protected GeoPointND[] copyPoints(Construction cons){
		return GeoElement.copyPoints(cons, points);
	}
	
	/**
	 * Returns new point in the same construction
	 * @return new point
	 */
	protected GeoPointND newGeoPoint(){
		return new GeoPoint(cons);
	}
	
	public void set(GeoElement geo) {
		GeoPolygon poly = (GeoPolygon) geo;		
		area = poly.area;
		
		
		// make sure both arrays have same size
		if (points.length != poly.points.length) {
			GeoPointND [] tempPoints = new GeoPointND[poly.points.length];
			for (int i=0; i < tempPoints.length; i++) {
				tempPoints[i] = i < points.length ? points[i] : newGeoPoint();
			}
			points = tempPoints;
		}
		
		for (int i=0; i < points.length; i++) {				
			((GeoElement) points[i]).set((GeoElement) poly.points[i]);
		}	
		
		setCoordSys(null);
		updateSegments();
		defined = poly.defined;	
		
		setCoordParentNumber(poly.getCoordParentNumber());
	}
	
	
	/**
	 * Returns the i-th point of this polygon.
	 * Note that this array may change dynamically.
	 * @param i number of point
	 * @return the i-th point
	 */
	public GeoPoint getPoint(int i) {
		return (GeoPoint) points[i];
	}

	/**
	 * Returns the points of this polygon as GeoPoints.
	 * Note that this array may change dynamically.
	 * @return points of this polygon.
	 */
	public GeoPointND [] getPoints() {
		return points;
	}
	
	/**
	 * Returns the points of this polygon as GeoPointNDs.
	 * Note that this array may change dynamically.
	 * @return points of this polygon
	 */
	public GeoPointND [] getPointsND() {
		return points;
	}
	
	/**
	 * Returns i-th vertex of this polygon
	 * @param i
	 * @return i-th pointt
	 */
	public GeoPointND getPointND(int i) {
		return points[i];
	}	
	
	/**
	 * Returns the segments of this polygon.
	 * Note that this array may change dynamically.
	 * @return segments of this polygon.
	 */
	public GeoSegmentND [] getSegments() {
		return segments;
	}
	
	/** sets the segments (used by GeoPolyhedron)
	 * @param segments the segments
	 */
	public void setSegments(GeoSegmentND [] segments){
		this.segments = segments;
	}

	public boolean isFillable() {
		return true;
	}
	
//	Michael Borcherds 2008-01-26 BEGIN
	/** 
	 * Calculates this polygon's area . This method should only be called by
	 * its parent algorithm of type AlgoPolygon
	 */
	public void calcArea() {
		area = calcAreaWithSign(getPoints());	
		defined = !(Double.isNaN(area) || Double.isInfinite(area));
	}
	
	/**
	 * Returns undirected area
	 * @return undirected area
	 */
	public double getArea() {
		if (isDefined())
			return Math.abs(area);				        
		else 
			return Double.NaN;			        	
	}
	
	public double getMeasure() {
		return getArea();
	}
	
	public Path getBoundary() {
		this.getConstruction().getKernel().setSilentMode(true);
		
		GeoPointND[] pointsForPolyLine = new GeoPointND[points.length+1];
		System.arraycopy(points, 0, pointsForPolyLine, 0, points.length);
		pointsForPolyLine[points.length]=pointsForPolyLine[0];
		
		GeoPolyLine pl = new GeoPolyLine(this.getConstruction(), pointsForPolyLine);
		
		this.getConstruction().getKernel().setSilentMode(false);
		
		return pl;
	}
	
	/**
	 * clockwise=-1
	 * anticlockwise=+1
	 * no area=0
	 * @return orientation of area 
	 */
	public double getDirection() { 
		if (defined)
			return MyMath.sgn(kernel, area);				        
		else 
			return Double.NaN;			        	
	}	

	/**
	 * Returns the area of a polygon given by points P
	 * @param P vertices of polygon
	 * @return undirected area
	 */	
	final static public double calcArea(GeoPoint [] P) {
	    return Math.abs(calcAreaWithSign(P));
	}
	/**
	 * Returns the area of a polygon given by points P, negative if clockwise
	 * changed name from calcArea as we need the sign when calculating the centroid Michael Borcherds 2008-01-26
	 * TODO Does not work if polygon is self-entrant
	 * @param points2 
	 * @return directed area
	 */	
	final static public double calcAreaWithSign(GeoPointND[] points2) {
		if (points2 == null || points2.length < 2)
			return Double.NaN;
		
	   int i = 0;   
	   for (; i < points2.length; i++) {
		   if (points2[i].isInfinite())
			return Double.NaN;
	   }
    
	   // area = 1/2 | det(P[i], P[i+1]) |
	   int last = points2.length - 1;
	   double sum = 0;                     
	   for (i=0; i < last; i++) {
			sum += GeoPoint.det((GeoPoint)points2[i], (GeoPoint)points2[i+1]);
	   }
	   sum += GeoPoint.det((GeoPoint)points2[last], (GeoPoint)points2[0]);
	   return sum / 2.0;  // positive (anticlockwise points) or negative (clockwise)
   }   
	
	/**
	 * Calculates the centroid of this polygon and writes
	 * the result to the given point.
	 * algorithm at http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/
	 * TODO Does not work if polygon is self-entrant
	 * @param centroid 
	 */
	public void calcCentroid(GeoPoint centroid) {
		if (!defined) {
			centroid.setUndefined();
			return;
		}
	
		double xsum = 0;
		double ysum = 0;
		double factor=0;
		for (int i=0; i < points.length; i++) {
			factor=pointsClosedX(i)*pointsClosedY(i+1)-pointsClosedX(i+1)*pointsClosedY(i);
			xsum+=(pointsClosedX(i)+pointsClosedX(i+1)) * factor;
			ysum+=(pointsClosedY(i)+pointsClosedY(i+1)) * factor;
		}
		centroid.setCoords(xsum, ysum, 6.0*getAreaWithSign()); // getArea includes the +/- to compensate for clockwise/anticlockwise
	}
	
	private double pointsClosedX(int i)
	{
		// pretend array has last element==first element
		if (i==points.length) //return points[0].inhomX; else return points[i].inhomX;
			return getPointX(0); else return getPointX(i);
	}
	 	
	private double pointsClosedY(int i)
	{
		// pretend array has last element==first element
		if (i==points.length) //return points[0].inhomY; else return points[i].inhomY;
			return getPointY(0); else return getPointY(i);
	}
	 	
	/**
	 * Returns directed area
	 * @return directed area
	 */
	public double getAreaWithSign() {
		if (defined)
			return area;				        
		else 
			return Double.NaN;			        	
	}	
//	Michael Borcherds 2008-01-26 END

	/*
	 * overwrite methods
	 */
	public boolean isDefined() {
		return defined;
   }	
   /**
    * Sets the polygon state to "defined"
    */
   public void setDefined() {
   		defined = true;
   }
   
   public void setUndefined() {
		   defined = false;
	}
        
   public final boolean showInAlgebraView() {	   
	   //return defined;
	   return true;
   }
   
   
   /** 
	* Yields true if the area of this polygon is equal to the
	* area of polygon p.
	*/
	// Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		// return false if it's a different type
		if (geo.isGeoPolygon()) return Kernel.isEqual(getArea(), ((GeoPolygon)geo).getArea());
		else return false;
	}

   public void setEuclidianVisible(boolean visible) {

	   setEuclidianVisible(visible,true);
	
   }  
   
   /**
    * Switches visibility of this polygon
    * @param visible visibility flag
    * @param updateSegments if true, applies also to segments
    */
   public void setEuclidianVisible(boolean visible, boolean updateSegments) {
		super.setEuclidianVisible(visible);
		if (updateSegments && segments != null) {
			for (int i=0; i < segments.length; i++) {
				segments[i].setEuclidianVisible(visible);			
				segments[i].update();
			}
		}		
   }  


   public void setObjColor(Color color) {
   		super.setObjColor(color);
   		if (segments != null && createSegments) {
   			for (int i=0; i < segments.length; i++) {
   				segments[i].setObjColor(color);
   				segments[i].update();
   			}
   		}	
   }
   
   public void setLineType(int type) {
	   setLineType(type,true);
   }
 
   
   /**
    * set the line type (and eventually the segments)
    * @param type
    * @param updateSegments
    */
   public void setLineType(int type, boolean updateSegments) {
	   super.setLineType(type);
	   if (updateSegments)
		   if (segments != null) {
			   for (int i=0; i < segments.length; i++) {
				   segments[i].setLineType(type);	
				   segments[i].update();
			   }
		   }	
   }
   
   public void setLineTypeHidden(int type) {
	   setLineTypeHidden(type, true);
   }

   /** set the hidden line type (and eventually the segments)
    * @param type
    * @param updateSegments
    */
   public void setLineTypeHidden(int type, boolean updateSegments) {
	   super.setLineTypeHidden(type);
		if (updateSegments)
			if (segments != null) {
				for (int i=0; i < segments.length; i++) {
					((GeoElement) segments[i]).setLineTypeHidden(type);	
					segments[i].update();
				}
			}	
  }
   
   public void setLineThickness(int th) {
	   setLineThickness(th,true);
   }

   /** set the line thickness (and eventually the segments)
    * @param th
    * @param updateSegments
    */
   public void setLineThickness(int th, boolean updateSegments) {
	   super.setLineThickness(th);

		if (updateSegments)
			if (segments != null) {
				for (int i=0; i < segments.length; i++) {
					segments[i].setLineThickness(th);
					segments[i].update();
				}
			}	
   }
	
   final public String toString() {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(kernel.format( getArea() ));
	    return sbToString.toString();
   }      

   final public String toStringMinimal() {
		sbToString.setLength(0);
		sbToString.append(regrFormat(getArea()));
	    return sbToString.toString();
   }      
   
   private StringBuilder sbToString = new StringBuilder(50);

   final public String toValueString() {
	   return kernel.format(getArea());
   }

	
	 /**
     * interface NumberValue
     */    
    public MyDouble getNumber() {    	
        return new MyDouble(kernel,  getArea() );
    }     
    
    final public double getDouble() {
        return getArea();
    }   
        
    final public boolean isConstant() {
        return false;
    }
    
    final public boolean isLeaf() {
        return true;
    }
    
    final public HashSet<GeoElement> getVariables() {
        HashSet<GeoElement> varset = new HashSet<GeoElement>();        
        varset.add(this);        
        return varset;          
    }                   
    
    final public ExpressionValue evaluate() { return this; }	

	protected boolean showInEuclidianView() {
		return defined;
	}    
	
	public boolean isNumberValue() {
		return true;
	}

	public boolean isVectorValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}   
	
	public boolean isTextValue() {
		return false;
	}   
	
	public boolean isGeoPolygon() {
		return true;
	}
	
	/*
	 * Path interface implementation
	 */
	
	public boolean isPath() {
		return true;
	}

	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	public double getMaxParameter() {
		return segments.length;
	}

	public double getMinParameter() {		
		return 0;
	}

	public boolean isClosedPath() {
		return true;
	}

	public boolean isOnPath(GeoPointND PI, double eps) {
		
		GeoPoint P = (GeoPoint) PI;
		
		if (P.getPath() == this)
			return true;
		
		// check if P is on one of the segments
		for (int i=0; i < segments.length; i++) {
			if (segments[i].isOnPath(P, eps))
				return true;
		}				
		return false;
	}

	public void pathChanged(GeoPointND PI) {		
		
		// parameter is between 0 and segment.length,
		// i.e. floor(parameter) gives the segment index
		
		PathParameter pp = PI.getPathParameter();
		pp.t = pp.t % segments.length;
		if (pp.t < 0) 
			pp.t += segments.length;
		int index = (int) Math.floor(pp.t) ;		
		GeoSegmentND seg = segments[index];
		double segParameter = pp.t - index;
		
		// calc point for given parameter
		PI.setCoords2D(seg.getPointX(segParameter), seg.getPointY(segParameter), 1);
	}

	public void pointChanged(GeoPointND PI) {
		
		Coords coords = PI.getCoordsInD(2);
		double qx = coords.getX()/coords.getZ();
		double qy = coords.getY()/coords.getZ();
		
		double minDist = Double.POSITIVE_INFINITY;
		double resx=0, resy=0, resz=0, param=0;
		
		// find closest point on each segment
		PathParameter pp = PI.getPathParameter();
		for (int i=0; i < segments.length; i++) {
			PI.setCoords2D(qx, qy, 1);
			segments[i].pointChanged(PI);
			
			coords = PI.getCoordsInD(2);
			double x = coords.getX()/coords.getZ() - qx; 
			double y = coords.getY()/coords.getZ() - qy;
			double dist = x*x + y*y;			
			if (dist < minDist) {
				minDist = dist;
				// remember closest point
				resx = coords.getX();
				resy = coords.getY();
				resz = coords.getZ();
				param = i + pp.t;
			}
		}				
			

		PI.setCoords2D(resx, resy, resz);
		
		pp.t = param;	
	}	
	
	
	
	
	
	/*
	 * Region interface implementation
	 */
	
	//by default, a polygon is always a region. 
	// A polygon can also be a path (as the boundary of the region)
	public boolean isRegion() {
		return true;
	}
	
	protected boolean isInRegion(GeoPointND PI, boolean update){
		
		Coords coords = PI.getCoordsInD(2);				
		return isInRegion(coords.getX()/coords.getZ(),coords.getY()/coords.getZ());		
		
	}
	
	public boolean isInRegion(GeoPointND PI){
		
		return isInRegion(PI,false);

	}
	
	/** says if the point (x0,y0) is in the region
	 * @param x0 x-coord of the point
	 * @param y0 y-coord of the point
	 * @return true if the point (x0,y0) is in the region
	 */
	public boolean isInRegion(double x0, double y0){
		
		double x1,y1,x2,y2;
		int numPoints = points.length;
		x1=getPointX(numPoints-1)-x0;
		y1=getPointY(numPoints-1)-y0;
		
		boolean ret=false;
		for (int i=0;i<numPoints;i++){
			x2=getPointX(i)-x0;
			y2=getPointY(i)-y0;
			int inter = intersectOx(x1, y1, x2, y2);
			if (inter==2)
				return true; //point on an edge
			ret = ret ^ (inter==1);
			x1=x2;
			y1=y2;
		}
			
		return ret;
	}
	
	final public void regionChanged(GeoPointND P){
		

		
		//GeoPoint P = (GeoPoint) PI;
		RegionParameters rp = P.getRegionParameters();
		
		if (rp.isOnPath())
			pathChanged(P);
		else{
			//pointChangedForRegion(P);
			double xu = p1.inhomX - p0.inhomX;
			double yu = p1.inhomY - p0.inhomY;				
			double xv = p2.inhomX - p0.inhomX;
			double yv = p2.inhomY - p0.inhomY;
			
			setRegionChanged(P,
					p0.inhomX + rp.getT1()*xu + rp.getT2()*xv,
					p0.inhomY + rp.getT1()*yu + rp.getT2()*yv);
			
			if (!isInRegion(P,false)){
				pointChanged(P);
				rp.setIsOnPath(true);
			}	
			
		}
	}
	
	
	/** set region coords (x,y) to point PI
	 * @param PI point
	 * @param x x-coord
	 * @param y y-coord
	 */
	public void setRegionChanged(GeoPointND PI, double x, double y){
		GeoPoint P = (GeoPoint) PI;
		P.x = x;
		P.y = y;
		P.z = 1;
				
	}
	
	
	public void pointChangedForRegion(GeoPointND P){
		
		P.updateCoords2D();
		
		RegionParameters rp = P.getRegionParameters();
		
		//Application.debug("isInRegion : "+isInRegion(P));
		
		if (!isInRegion(P)){		
			pointChanged(P);
			rp.setIsOnPath(true);
		}else{
			if(numCS!=3){ //if the coord sys is not defined by 3 independent points, then the point lies on the path
				pointChanged(P);
				rp.setIsOnPath(true);
			}else{
				rp.setIsOnPath(false);
				double xu = p1.inhomX - p0.inhomX;
				double yu = p1.inhomY - p0.inhomY;				
				double xv = p2.inhomX - p0.inhomX;
				double yv = p2.inhomY - p0.inhomY;				
				double x = P.getX2D() - p0.inhomX;
				double y = P.getY2D() - p0.inhomY;
				rp.setT1((xv*y-x*yv)/(xv*yu-xu*yv));
				rp.setT2((x*yu-xu*y)/(xv*yu-xu*yv));
				
				//Application.debug(rp.getT1()+","+rp.getT2());
				
				P.updateCoordsFrom2D(false,null);
			}
		}
	}
	

	

	
	
	/**
	 * update the coord sys used for region parameters
	 */
	final public void updateRegionCS() {
		// TODO add condition to calculate it		
		if(p2==null || GeoPoint.collinear(p0, p1, p2)){
			p0 = getPoint(0);	
			numCS = 1;
			//Application.debug(" p0 = "+p0.inhomX+","+p0.inhomY);

			int secondPoint = -1;
			boolean secondPointFound = false;
			for (secondPoint=1; secondPoint<getPoints().length && !secondPointFound; secondPoint++){
				p1=getPoint(secondPoint);
				//Application.debug(" p1 ("+secondPoint+") = "+p1.inhomX+","+p1.inhomY);
				if (!Kernel.isEqual(p0.inhomX, p1.inhomX, Kernel.STANDARD_PRECISION))
					secondPointFound = true;
				else if (!Kernel.isEqual(p0.inhomY, p1.inhomY, Kernel.STANDARD_PRECISION))
					secondPointFound = true;
				//Application.debug(" secondPointFound = "+secondPointFound);
			}

			int thirdPoint = -1;
			if (secondPointFound){
				numCS++;
				secondPoint--;
				boolean thirdPointFound = false;
				for (thirdPoint=getPoints().length-1; thirdPoint>secondPoint && !thirdPointFound; thirdPoint--){
					p2=getPoint(thirdPoint);
					if (!GeoPoint.collinear(p0, p1, p2)){
						thirdPointFound = true;
						numCS++;
					}
				}			
			}

			//thirdPoint++;
			//Application.debug(" secondPoint = "+secondPoint+"\n thirdPoint = "+thirdPoint);
		}
		
		//Application.debug(this+"\n p0 = "+p0.getLabel()+"\n p1 = "+p1.getLabel()+"\n p2 = "+p2.getLabel());

			
			
	}
	
	
	
	////////////////////////////
	// interface GeoSurfaceFinite
	///////////////////////////////
	private boolean asBoundary = false;
	
	public void setRole(boolean isAsBoundary) {
		this.asBoundary = isAsBoundary; //false means 'as region'
	}
	public boolean asBoundary() {
		return asBoundary;
	}
	


	
	
	
	
	/** returns true if the segment ((x1,y1),(x2,y2)) intersects [Ox) */
	private int intersectOx(double x1, double y1, double x2, double y2){
		
		double eps = Kernel.STANDARD_PRECISION;
		
		if (Kernel.isZero(y1)){ //first point on (Ox)
			if (Kernel.isZero(y2)){ //second point on (Ox)
				if ((x1+eps<0) && (x2+eps<0)) //segment totally on the left
					return -1;
				else if ((x1>eps) && (x2>eps)) //segment totally on the right
					return 1;
				else //O on segment
					return 2;
			}else
				return -1; //only first point is on (Ox)
		}else if (y1*y2>eps) //segment totally above or under
			return -1;
		else{ 
			if (y1>y2){ //first point under (Ox)
				double y=y1; y1=y2; y2=y;
				double x=x1; x1=x2; x2=x;
			}

			if ((x1+eps<0) && (x2+eps<0)) //segment totally on the left
				return -1;
			else if ((x1>eps) && (x2>eps)) //segment totally on the right
				return 1;
			else if (x1*y2>x2*y1+eps) //angle > 0
				return 1;
			else if (x1*y2+eps<x2*y1) //angle < 0
				return -1;
			else
				return 2; //angle ~ 0
		}
	}
	
	
	/**
	 * returns all class-specific xml tags for getXML
	 * GeoGebra File Format
	 */
	protected void getXMLtags(StringBuilder sb) {
		getLineStyleXML(sb);
		getXMLvisualTags(sb);
		getXMLanimationTags(sb);
		getXMLfixedTag(sb);
		getAuxiliaryXML(sb);
		getBreakpointXML(sb);		
		getScriptTags(sb);
	}
	
	
	

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return minimum line thickness (normally 1, but 0 for polygons, integrals etc)
	 */
	public int getMinimumLineThickness() {		
		return 0;
	}

   private boolean trace;


	public boolean isTraceable() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}
	
	
	
	/////////////////////
	// 3D stuff
	/////////////////////



	/**
	 * Returns the i-th 3D point of this polygon.
	 * @param i number of point
	 * @return the i-th point
	 */	 
	public Coords getPoint3D(int i){
		return getPoint(i).getInhomCoordsInD(3);
		/*
  		Coords v = new Coords(4);
  		v.set(getPoint(i).getInhomCoordsInD(3));
  		v.setW(1);
  		return v;
  		*/
	}
	

	/** if this is a part of a closed surface
	 * @return if this is a part of a closed surface
	 */
	public boolean isPartOfClosedSurface(){
		return false; //TODO
	}


	public boolean hasDrawable3D() {
		return true;
	}	
	
	public Coords getLabelPosition(){
		double x = 0;
		double y = 0;
		double z = 0;
		for (int i=0; i<getPointsLength(); i++){
			Coords coords = getPoint3D(i);
			x+=coords.getX();
			y+=coords.getY();
			z+=coords.getZ();
		}
		return new Coords(x/getPointsLength(), y/getPointsLength(), z/getPointsLength(), 1);
	}
	
	
	
	////////////////////////////////////////
	// GEOCOORDSYS2D INTERFACE
	////////////////////////////////////////

	public CoordSys getCoordSys() {
		return CoordSys.Identity3D();
	}

	public Coords getPoint(double x2d, double y2d) {		
		return getCoordSys().getPoint(x2d, y2d);
	}

	public Coords[] getNormalProjection(Coords coords){
		return getCoordSys().getNormalProjection(coords);
	}

	public Coords[] getProjection(Coords oldCoords, Coords willingCoords, Coords willingDirection) {
		return willingCoords.projectPlaneThruVIfPossible(getCoordSys().getMatrixOrthonormal(),oldCoords,willingDirection);
	}
	
	
	
	//////////////////////////////////////////////////////
	// PARENT NUMBER (HEIGHT OF A PRISM, ...)
	//////////////////////////////////////////////////////

	private GeoNumeric changeableCoordNumber = null;
	private GeoElement changeableCoordDirector = null;
	
	/**
	 * sets the parent number for changing coords
	 * @param geo
	 */
	final public void setCoordParentNumber(GeoNumeric geo) {
		changeableCoordNumber=geo;
	}
	
	/**
	 * sets the parent director for changing coords
	 * @param geo
	 */
	final public void setCoordParentDirector(GeoElement geo) {
		changeableCoordDirector=geo;
	}	
	
	final private GeoNumeric getCoordParentNumber() {
		return changeableCoordNumber;
	}
	
	public boolean hasChangeableCoordParentNumbers() {
		return (getCoordParentNumber()!=null);
	}
	
	private double startValue;
	
	private Coords direction;

	public void recordChangeableCoordParentNumbers() {
		startValue = getCoordParentNumber().getValue();
		//direction = getMainDirection().normalized();
		this.direction=changeableCoordDirector.getMainDirection();
	}
	
	public boolean moveFromChangeableCoordParentNumbers(Coords rwTransVec, Coords endPosition, 
			Coords viewDirection, ArrayList updateGeos, ArrayList tempMoveObjectList){
		
		GeoNumeric var = getCoordParentNumber();
				
		if (var==null)
			return false;
		if (endPosition==null){ //comes from arrows keys -- all is added
			var.setValue( var.getValue() +rwTransVec.getX()+rwTransVec.getY()+rwTransVec.getZ());
			addChangeableCoordParentNumberToUpdateList(var, updateGeos, tempMoveObjectList);
			return true;
		}else{ //comes from mouse
			Coords direction2=direction.sub(viewDirection.mul(viewDirection.dotproduct(direction)));
			double ld = direction2.dotproduct(direction2);
			if (Kernel.isZero(ld))
				return false;			
			double val = direction2.dotproduct(rwTransVec);
			var.setValue(startValue+val/ld);
			addChangeableCoordParentNumberToUpdateList(var, updateGeos, tempMoveObjectList);
			return true;
		}
		
		
	}

	public void rotate(NumberValue r) {
		for(int i=0;i<points.length;i++)
			((GeoPoint)points[i]).rotate(r);	
	}

	public void rotate(NumberValue r, GeoPoint S) {
		for(int i=0;i<points.length;i++)
			((GeoPoint)points[i]).rotate(r,S);	
	}

	public void matrixTransform(double a00, double a01, double a10, double a11) {
		for(int i=0;i<points.length;i++)
			((GeoPoint)points[i]).matrixTransform(a00, a01, a10, a11);		
		this.calcArea();
	}

	public void translate(Coords v) {
		for(int i=0;i<points.length;i++)
			((GeoPoint)points[i]).translate(v);		
	}

	public void dilate(NumberValue r, GeoPoint S) {
		for(int i=0;i<points.length;i++)
			((GeoPoint)points[i]).dilate(r,S);		
		this.calcArea();
	}
	
	public void mirror(GeoPoint Q) {
		for(int i=0;i<points.length;i++)
			((GeoPoint)points[i]).mirror(Q);				
	}

	public void mirror(GeoLine g) {
		for(int i=0;i<points.length;i++)
			((GeoPoint)points[i]).mirror(g);				
	}


	/**
	 * Returns true iff all vertices are labeled
	 * @return true iff all vertices are labeled
	 */
	public boolean isAllVertexLabelsSet() {
		for(int i=0;i<points.length;i++)
			if(!((GeoPoint)points[i]).isLabelSet())return false;
		return true;
	}

	/**
	 * Returns true iff number of vertices is not volatile
	 * @return true iff number of vertices is not volatile
	 */
	public boolean isVertexCountFixed() {
		//regularPolygon[vertex,vertex,count]
		if(getParentAlgorithm() instanceof AlgoPolygonRegular)return false;
		//polygon[list]
		if(getParentAlgorithm().getInput().length<3)return false;
		return true;
	}

	
	


	public Coords getDirectionInD3(){
		return new Coords(0,0,1,0);
	}
	
	
	//////////////////////////////////
	// 2D VIEW
	
	private EuclidianView euclidianViewForPlane;
	
	public void createView2D(){
		euclidianViewForPlane = app.createEuclidianViewForPlane(this);
	
	}
	
	public void update(){
		super.update();
		if (euclidianViewForPlane!=null){
			euclidianViewForPlane.updateForPlane();
		}
			
	}

	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		for(int i=0;i<points.length;i++)
			((GeoPoint)points[i]).matrixTransform(a00, a01, a02, a10, a11, a12, a20, a21, a22);
		
	}
	public  void toGeoCurveCartesian(GeoCurveCartesian curve){
		curve.setFromPolyLine(points, true);
    }
}

