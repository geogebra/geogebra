/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/* 
 * Created on 03.12.2004
 */
package geogebra.kernel;

import geogebra.kernel.Matrix.CoordNearest;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.integration.EllipticArcLength;
import geogebra.kernel.kernelND.GeoPointND;


/**
 * GeoCirclePart for 
 * @author Markus Hohenwarter
 * 
 */
public class GeoConicPart extends GeoConic
implements LimitedPath, NumberValue, LineProperties {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** conic arc*/
	public static final int CONIC_PART_ARC = 1;
	/** conic sector */
	public static final int CONIC_PART_SECTOR = 2;
		
	// parameters (e.g. angles) for arc
	private	double paramStart, paramEnd, paramExtent;
	private boolean posOrientation;
	private int conic_part_type;
	
	private double value, area;
	private boolean value_defined;
	
	private EllipticArcLength ellipticArcLength;
	private boolean allowOutlyingIntersections = false;
	private boolean keepTypeOnGeometricTransform = true;
			
	/**
	 * GeoCirclePart is constructed by AlgoCirclePart... 
	 * @param c
	 * @param type CONIC_PART_ARC or CONIC_PART_SECTOR
	 */
	public GeoConicPart(Construction c, int type) {
		super(c);				
		conic_part_type = type;			
	}
	
	/**
	 * Copy constructor
	 * @param conic
	 */
	public GeoConicPart(GeoConicPart conic) {
		this(conic.cons, conic.getConicPartType());
		set(conic);
	}
	
	public String getClassName() {	
		return "GeoConicPart";
 	}
	
    public int getGeoClassType() {
    	return GEO_CLASS_CONICPART;
    }
	
	protected String getTypeString() {      
		switch (conic_part_type) {
			case CONIC_PART_ARC: 
				return "Arc";
			
			case CONIC_PART_SECTOR: 
				return "Sector";
			
			default:
				return super.getTypeString();
		}                       
	}  
	
	public GeoElement copyInternal(Construction cons) {
		GeoConicPart ret = new GeoConicPart(cons, conic_part_type);
		ret.set(this);
		return ret;
	}
	
	public void set(GeoElement geo) {		
		super.set(geo);
		if (!geo.isGeoConicPart()) return;
		
		GeoConicPart cp = (GeoConicPart) geo;		
		
		// class specific attributes
		paramStart = cp.paramStart;
		paramEnd = cp.paramEnd;
		paramExtent = cp.paramExtent;
		posOrientation = cp.posOrientation;
		conic_part_type = cp.conic_part_type;
		
		lines = cp.lines;
		
		value = cp.value;
		value_defined = cp.value_defined;	
		
		keepTypeOnGeometricTransform = cp.keepTypeOnGeometricTransform;		
	}
	
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);
		
		if (geo.isGeoConicPart()) { 
			GeoConicPart cp = (GeoConicPart) geo;
			allowOutlyingIntersections = cp.allowOutlyingIntersections;
		}
	}
	/**
	 * Sector or arc
	 * @return CONIC_PART_ARC or CONIC_PART_SECTOR
	 */
	final public int getConicPartType() {
		return conic_part_type;
	}
	
	/**
	 * @return start parameter
	 */
	final public double getParameterStart() {
		return paramStart;
	}
	
	
	/**
	 * @return end parameter
	 */
	final public double getParameterEnd() {
		return paramEnd;
	}
	
	/**
	 * @return end parameter - start parameter
	 */
	final public double getParameterExtent() {
		return paramExtent;
	}
	
	/**
	 * @return start parameter
	 */
	final public boolean positiveOrientation() {
		return posOrientation;
	}	
	
	/**
	 * Returns wheter c is equal to this conic part
	 */
	// Michael Borcherds 2008-05-01
	final public boolean isEqual(GeoElement geo) {
		
		if (!geo.isGeoConicPart()) return false;
		
		GeoConicPart c = (GeoConicPart)geo;
		
		return 
			posOrientation == c.posOrientation &&
			conic_part_type == c.conic_part_type &&		
			Kernel.isEqual(paramStart, c.paramStart) &&
			Kernel.isEqual(paramEnd, c.paramEnd) &&
			super.isEqual(c);			 
	}
	
	/** 
	 * Sets parameters and calculates this object's value.
	 * For type CONIC_PART_ARC the value is
	 * the length, for CONIC_PART_SECTOR the value is an area. 
	 * This method should only be called by the parent algorithm 
	 * @param a 
	 * @param b 
	 * @param positiveOrientation 
	 */
	final public void setParameters(double a, double b, boolean positiveOrientation) {
		value_defined = super.isDefined();
		if (!value_defined) {
			value = Double.NaN;	
			return;
		}
		
		posOrientation = positiveOrientation;
		if (!posOrientation) {
			// internally we always use positive orientation, i.e. a <= b
			// the orientation flag is important for points on this path (see pathChanged())
			double temp = a;
			a = b;
			b = temp;
		}
		
		// handle conic types
		switch (type) {
			case GeoConic.CONIC_CIRCLE:
				paramStart = kernel.convertToAngleValue(a);
				paramEnd = kernel.convertToAngleValue(b);		
				paramExtent = paramEnd - paramStart;
				if (paramExtent < 0) paramExtent += Kernel.PI_2;
				
				double r = halfAxes[0];
				if (conic_part_type == CONIC_PART_ARC) {
					value = r * paramExtent; // length
					// area arc = area sector - area triangle
					area = r*r * (paramExtent - Math.sin(paramExtent) ) / 2.0; 
				} else {
					value = r*r * paramExtent / 2.0; // area		
					area = value; // area	
				}
				value_defined = !Double.isNaN(value) &&
								!Double.isInfinite(value);
				break;
			
			case GeoConic.CONIC_ELLIPSE:					
				paramStart = kernel.convertToAngleValue(a);
				paramEnd = kernel.convertToAngleValue(b);		
				paramExtent = paramEnd - paramStart;
				if (paramExtent < 0) paramExtent += Kernel.PI_2;
				
				if (ellipticArcLength == null)
					ellipticArcLength = new EllipticArcLength(this);
				
				if (conic_part_type == CONIC_PART_ARC) {
					// length
					value = ellipticArcLength.compute(paramStart, paramEnd);
				} else {
					// area
					value = halfAxes[0] * halfAxes[1] * paramExtent / 2.0; 		
				}
				value_defined = !Double.isNaN(value) &&
								!Double.isInfinite(value);
				
				break;
				
			// a circular arc through 3 points may degenerate
			// to a segment or two rays
			case GeoConic.CONIC_PARALLEL_LINES:
				if (conic_part_type == CONIC_PART_ARC && posOrientation) {
				    // length of segment 
					// bugfix Michael Borcherds 2008-05-27
					GeoPoint startPoint=lines[0].getStartPoint();
					GeoPoint endPoint=lines[0].getEndPoint();
				    if (startPoint!=null && endPoint!=null)
				    {
				    	value = startPoint.distance(endPoint);
				    }
				    else 
				    {
				    	value = Double.POSITIVE_INFINITY; 
						value_defined = false;
					    break;
				    }
//				  bugfix end

				} else { //sector or two rays
					value = Double.POSITIVE_INFINITY; // area or length of rays	
				}
				value_defined = true;
			    break;
			
			default:
				value_defined = false;
				//Application.debug("GeoConicPart: unsupported conic part for conic type: " + type);
		}		
	}
	
	final public boolean isDefined() {
		return value_defined;
	}		
	
	public void setUndefined() {
		value_defined = false;
	}
	
	/**
	 * Returns arc length
	 * @return arc length
	 */
	final public double getValue() {
		if(!value_defined)
			return Double.NaN;
		return value;
	}
	
	/**
	 * Returns the area
	 * @return area
	 */
	final public double getArea() {
		return area;
	}
	
    final public String toString() {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(toValueString());			     
        return sbToString.toString();
    }
	private StringBuilder sbToString = new StringBuilder(50);
	
	final public String toValueString() {
		if(value_defined)
			return kernel.format(value);
		return kernel.format(Double.NaN);
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
			return isOnFullConic(p, eps);
		else
			return isOnPath(p, eps);
    }
	
	/** 
	 * states wheter P lies on this conic part or not 
	 */
	public boolean isOnPath(GeoPointND PI, double eps) {
		
		GeoPoint P = (GeoPoint) PI;
		
		if (P.getPath() == this)
			return true;
		
		// check if P lies on conic first
    	if (!isOnFullConic(P, eps))
    		return false;
    		
		// idea: calculate path parameter and check
		//       if it is in [0, 1]
		
		// remember the old values
		double px = P.x, py = P.y, pz = P.z;
		PathParameter tempPP = getTempPathParameter();
		PathParameter pPP = P.getPathParameter();
		tempPP.set(pPP);
		
		switch (type) {
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
				setEllipseParameter(P.getCoordsInD2(getCoordSys()),P.getPathParameter());
			break;
			
			// degenerate case: two rays or one segment
			case CONIC_PARALLEL_LINES: 		
				if (posOrientation) {
					// segment
					lines[0].pointChanged(P);
				} else {
					// two rays: no point should lie on them
					P.getPathParameter().t = -1;
				}
			break;

			default:
				pPP.t = -1;
				//Application.debug("GeoConicPart.isIncident: unsupported conic part for conic type: " + type);
		}					
	
		// adapt eps for very large circles (almost line)
		if (halfAxes[0] > 100)
    		eps = Math.max(Kernel.MAX_PRECISION, eps / halfAxes[0]);    	    		    		   
		
		boolean result = 	pPP.t >= -eps && 
							pPP.t <= 1 + eps;
		
		// restore old values
		P.x = px; P.y = py; P.z = pz;
		pPP.set(tempPP);
		
		return result;
	}
	
	private PathParameter tempPP;
	private PathParameter getTempPathParameter() {
		if (tempPP == null)
			tempPP = new PathParameter();
		return tempPP;
	}
	
	/*
	 * Path Interface implementation
	 */
	
	public boolean isClosedPath() {
		return false;
	}
	
	protected void pointChanged(Coords P, PathParameter pp) {
		
		
		pp.setPathType(type);

		switch (type) {
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
				setEllipseParameter(P,pp);
				clipEllipseParameter(P,pp);		
			break;
			
			// degenerate case: two rays or one segment
			case CONIC_PARALLEL_LINES: 		
				if (posOrientation) {
					// segment
					lines[0].doPointChanged(P,pp);
					
					// make sure we don't get outside [0,1]
					if (pp.t < 0) {
						pp.t = 0;
						pathChanged(P,pp);
					}
					else if (pp.t > 1) {
						pp.t = 1;
						pathChanged(P,pp);
					}
				} else {
					// two rays
					// we take point at infinty
					/*
					P.x = -lines[0].y;
					P.y = lines[0].x;
					P.z = 0.0;
					 */
					P.setX(-lines[0].y);
					P.setY(lines[0].x);
					P.setZ(0);
				}
			break;

			default:
				pp.t = Double.NaN;
				//Application.debug("GeoConicPart.pointChanged(): unsupported conic part for conic type: " + type);
		}	
	}
	
	private void setEllipseParameter(Coords P, PathParameter pp) {
		// let GeoConic do the work
		super.pointChanged(P,pp);		
			
		// now transform parameter t from [paramStart, paramEnd] to [0, 1]
		if (pp.t < 0)
			pp.t += Kernel.PI_2;
		double t = pp.t - paramStart;
		if (t < 0) 
			t += Kernel.PI_2;
		pp.t = t / paramExtent;	
	}
	
	private void clipEllipseParameter(Coords P, PathParameter pp) {
		// make sure we don't get outside [0,1]
		// the values of the path parameter are now
		// between [0, 2pi/paramExtent]
		// [0, 1] is ok.
		// handle [1, 2pi/paramExtent]:
		// take 0 for parameter > (1 + 2pi/paramExtent)/2
		// else take 1
		if (pp.t > 0.5 + Math.PI/paramExtent) {
			if (posOrientation)
				pp.t = 0;
			else
				pp.t = 1;
			pathChanged(P,pp);
		}
		else if (pp.t > 1) {
			if (posOrientation)
				pp.t = 1;
			else
				pp.t = 0;
			pathChanged(P,pp);
		}
		else if (!posOrientation) {
			pp.t = 1.0 - pp.t;
		}	
	}
	
	protected void pathChanged(Coords P, PathParameter pp) {
		if(!value_defined){
			P.setX(Double.NaN);
			return;
		}
		if (pp.getPathType() != type || Double.isNaN(pp.t)) {		
			pointChanged(P,pp);
			return;
		}

		if (pp.t < 0.0) {
			pp.t = 0;
		} 
		else if (pp.t > 1.0) {
			pp.t = 1;
		}
		
		// handle conic types	
		switch (type) {
			case CONIC_CIRCLE:	
			case CONIC_ELLIPSE:	
				// if type of path changed (other conic) then we
				// have to recalc the parameter with pointChanged()
				if (pp.getPathType() != type) {					
					pointChanged(P,pp);
					return;
				}		
				
				// calc Point on conic using this parameter (in eigenvector space)
				double t = posOrientation ?
						pp.t:
						1.0 - pp.t;
				double angle = paramStart + t * paramExtent;

				P.setX(halfAxes[0] * Math.cos(angle));
				P.setY(halfAxes[1] * Math.sin(angle));
				P.setZ(1);
				coordsEVtoRW(P);							
			break;	
			
			case CONIC_PARALLEL_LINES:
				if (posOrientation) { // segment
					// if type of path changed (other conic) then we
					// have to recalc the parameter with pointChanged()
					if (pp.getPathType() != type) {					
						pointChanged(P,pp);						
					}	else {
						lines[0].pathChanged(P,pp);
					}
				} else {
					// two rays
					// we take point at infinty
					P.setX(-lines[0].y);
					P.setY(lines[0].x);
					P.setZ(0);

				}
			break;

			default:
				//Application.debug("GeoConicPart.pathChanged(): unsupported conic part for conic type: " + type);
		}
	}
	
	/**
	 * Returns the smallest possible parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 * 
	 */
	public double getMinParameter() {
		switch (type) {
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
				return 0;		
		
		// degenerate case: two rays or one segment
			case CONIC_PARALLEL_LINES: 		
				if (posOrientation)
					// segment
					return 0;
				else
					// two rays					
					return Double.NEGATIVE_INFINITY;			
	
			default:
				return Double.NaN;
		}
	}
	
	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 * 
	 */
	public double getMaxParameter() {
		switch (type) {
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
				return 1;		
		
		// degenerate case: two rays or one segment
			case CONIC_PARALLEL_LINES: 		
				if (posOrientation)
					// segment
					return 1;
				else
					// two rays					
					return Double.POSITIVE_INFINITY;			
	
			default:
				return Double.NaN;
		}
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
     * interface NumberValue
     */    
    public MyDouble getNumber() {    	
        return new MyDouble(kernel,  getValue() );
    }     
    
    final public double getDouble() {
        return getValue();
    }
    
	public boolean isNumberValue() {
		return true;
	}
	
	public boolean isGeoConicPart() {
		return true;
	}

	
	public GeoElement [] createTransformedObject(Transform t,String label) {				
		if (keepTypeOnGeometricTransform) {
			algoParent = getParentAlgorithm();			
		}	

		// CREATE CONIC PART
		if (algoParent instanceof AlgoConicPartCircle) {	
			//	transform points
			AlgoConicPartCircle algo = (AlgoConicPartCircle) algoParent;
			GeoPoint [] points = { algo.getCenter(), algo.getStartPoint(), algo.getEndPoint() };
			
		    // create circle with center through startPoint        
	        AlgoCircleTwoPoints algoCircle = new AlgoCircleTwoPoints(cons, points[0], points[1]);
	        cons.removeFromConstructionList(algoCircle);
	        GeoConic circle = algoCircle.getCircle();
	        
	        // transform points and circle
			points = t.transformPoints(points);			
			GeoConic transformedCircle = t.getTransformedConic(circle);	
			cons.removeFromConstructionList(transformedCircle.getParentAlgorithm());			
										
			// create a new arc from the transformed circle using startPoint and endPoint
			AlgoConicPartConicPoints algoResult = new AlgoConicPartConicPoints(cons, label, transformedCircle, points[1], points[2], conic_part_type);			
			GeoConicPart conicPart = algoResult.getConicPart();
			conicPart.setVisualStyleForTransformations(this);
			GeoElement [] geos = {conicPart, points[0], points[2], points[1]};
						
			return geos;					
		}
		
		else if (algoParent instanceof AlgoConicPartCircumcircle) {
			GeoPoint [] points ={ (GeoPoint) algoParent.input[0], 
					 (GeoPoint) algoParent.input[1],  (GeoPoint) algoParent.input[2]};			
			points = t.transformPoints(points);
			
			AlgoConicPartCircumcircle algo = new AlgoConicPartCircumcircle(cons, label, points[0], points[1], points[2], conic_part_type);
			GeoConicPart res = algo.getConicPart();
			res.setLabel(label);
			res.setVisualStyleForTransformations(this);
			GeoElement [] geos = {res, points[1], points[2], points[0]};
			return geos;
		}
				
		else if (algoParent instanceof AlgoConicPartConicParameters) {
			AlgoConicPartConicParameters algo = (AlgoConicPartConicParameters) algoParent;			
						
			GeoConic transformedConic = t.getTransformedConic(algo.conic);	
			cons.removeFromConstructionList(transformedConic.getParentAlgorithm());			
										
			algo = new AlgoConicPartConicParameters(cons, label, transformedConic, algo.startParam, algo.endParam, conic_part_type);			
			GeoElement ret = algo.getConicPart();
			ret.setVisualStyleForTransformations(this);
			GeoElement [] geos = {ret};
			return geos;
		}
		
		else if (algoParent instanceof AlgoConicPartConicPoints) {
			AlgoConicPartConicPoints algo = (AlgoConicPartConicPoints) algoParent;			
			GeoPoint [] points ={ algo.getStartPoint(), algo.getEndPoint() };			
			points = t.transformPoints(points);									
			GeoConic orgConic = algo.getConic();
			
			GeoConic transformedConic = t.getTransformedConic(orgConic);	
			cons.removeFromConstructionList(transformedConic.getParentAlgorithm());		
		
			algo = new AlgoConicPartConicPoints(cons, label, transformedConic, points[0], points[1], conic_part_type);			
			GeoConicPart conicPart = algo.getConicPart();
			conicPart.setVisualStyleForTransformations(this);
			GeoElement [] geos = {conicPart, points[0], points[1]};
			return geos;
		}
		
		else if (algoParent instanceof AlgoSemicircle) {			
			AlgoElement algo =  algoParent;			
			GeoPoint [] points ={ ((AlgoSemicircle)algo).getA(), ((AlgoSemicircle)algo).getB() };			
			points = t.transformPoints(points);
			
			GeoConic semCirc;
			if (t instanceof TransformMirror && t.changesOrientation()) {
				semCirc = kernel.Semicircle(label, points[1], points[0]);
			} else if(t.isSimilar()) {
				semCirc = kernel.Semicircle(label, points[0], points[1]);
			} else {
				
				GeoConic orgConic = ((AlgoSemicircle)algo).getConic();
				GeoConic transformedConic = t.getTransformedConic(orgConic);
				cons.removeFromConstructionList(transformedConic.getParentAlgorithm());
				if(t.changesOrientation()){
				algo = new AlgoConicPartConicPoints(cons, label, transformedConic, points[0], points[1], conic_part_type);
				}else
					algo = new AlgoConicPartConicPoints(cons, label, transformedConic, points[1], points[0], conic_part_type);
				GeoConicPart conicPart = ((AlgoConicPartConicPoints)algo).getConicPart();
				conicPart.setVisualStyleForTransformations(this);
				GeoElement [] geos = {conicPart, points[0], points[1]};
				return geos;				
			}
			semCirc.setVisualStyleForTransformations(this);
			GeoElement [] geos = {semCirc, points[0], points[1]};
			return geos;
		}
		
		else {
			//	create CONIC
			GeoConic transformedConic = t.getTransformedConic(this);
			transformedConic.setLabel(label);
			GeoElement [] ret = { transformedConic };
			return ret;
		}	
	}
	final public GeoElement copy() {
		return new GeoConicPart(this);
	}
	
	/**
	 * not supported for Arcs etc
	 * @param geoMatrix 
	 */
	public void matrixTransform(GeoList geoMatrix) {
		
		setUndefined();
		
	}
	
	
	public boolean isInRegion(double x0, double y0) {
			
		if(!super.isInRegion(x0, y0))
			return false;
		

		//for sector, check if (x0,y0) is on the arc outline
		if (getConicPartType()==CONIC_PART_SECTOR){
			double arg = computeArg(x0,y0);
			if (arg < 0) 
				arg += Kernel.PI_2;
			//Application.debug(arg+" <? "+paramExtent);

			return ((arg >= -Kernel.EPSILON) && (arg <= paramExtent+Kernel.EPSILON));
		}
			
		
		//for arc, check if is inside the arc : cross product with limit 
		//Application.debug(posOrientation);
		Coords midpoint = getMidpoint2D();
		Coords firstVec = getEigenvec(0).mul(getHalfAxis(0)*Math.cos(paramStart)).add(getEigenvec(1).mul(getHalfAxis(1)*Math.sin(paramStart)));
		Coords secondVec = getEigenvec(0).mul(getHalfAxis(0)*Math.cos(paramEnd)).add(getEigenvec(1).mul(getHalfAxis(1)*Math.sin(paramEnd)));

		double vx = (x0-midpoint.getX())-firstVec.getX(), vy = (y0-midpoint.getY())-firstVec.getY();
		double lx = secondVec.getX()-firstVec.getX(), ly = secondVec.getY()-firstVec.getY();
		
		return (vx*ly-vy*lx>0);
		
	}
	
	
	private double computeArg(double x0, double y0) {
		double px = x0 - b.x;
		double py = y0 - b.y;
		
		// rotate by -alpha
		double px2 = px * eigenvec[0].x + py * eigenvec[0].y;
		py = px * eigenvec[1].x + py * eigenvec[1].y;			

		// calc parameter 
			
		
		// relation between the internal parameter t and the angle theta:
		// t = atan(a/b tan(theta)) where tan(theta) = py / px
		double arg = Math.atan2(halfAxes[0]*py, halfAxes[1]*px2);
		if (arg < 0)
			arg += Kernel.PI_2;
		return arg - paramStart;
			
	}

	protected void moveBackToRegion(GeoPointND pi,RegionParameters rp) {
		Coords coords = pi.getCoordsInD2(getCoordSys());
		PathParameter pp = pi.getPathParameter();
		
		//try to find the nearest point in the conic part
		CoordNearest nearestPoint = new CoordNearest(coords);
		

		
		//check points of the conic part
		Coords midpoint = getMidpoint2D();
		if (getConicPartType()==CONIC_PART_SECTOR)
			nearestPoint.check(midpoint);
		
		Coords ev0 = new Coords(3); ev0.set(getEigenvec(0));
		Coords ev1 = new Coords(3); ev1.set(getEigenvec(1));	
		
		Coords firstPoint = midpoint.add(ev0.mul(getHalfAxis(0)*Math.cos(paramStart))).add(ev1.mul(getHalfAxis(1)*Math.sin(paramStart)));
		nearestPoint.check(firstPoint);
		Coords secondPoint = midpoint.add(ev0.mul(getHalfAxis(0)*Math.cos(paramEnd))).add(ev1.mul(getHalfAxis(1)*Math.sin(paramEnd)));
		nearestPoint.check(secondPoint);
		
		//check project points on segments edges
		Coords[] segPoint;
		if (getConicPartType()==CONIC_PART_SECTOR){
			segPoint = coords.projectLine(midpoint, firstPoint.sub(midpoint));
			if (segPoint[1].getX()>0 && segPoint[1].getX()<1) //check if the projected point is on the segment
				nearestPoint.check(segPoint[0]);
			segPoint = coords.projectLine(midpoint, secondPoint.sub(midpoint));
			if (segPoint[1].getX()>0 && segPoint[1].getX()<1) //check if the projected point is on the segment
				nearestPoint.check(segPoint[0]);
		}else{
			segPoint = coords.projectLine(firstPoint, secondPoint.sub(firstPoint));
			if (segPoint[1].getX()>0 && segPoint[1].getX()<1) //check if the projected point is on the segment
				nearestPoint.check(segPoint[0]);
		}
		
		
		//may calc the nearest point of the global conic
		if(!super.isInRegion(coords.getX(),coords.getY())){
			Coords pointConic = coords.copyVector();
			pointChanged(pointConic, pp);
			nearestPoint.check(pointConic);
			rp.setIsOnPath(true);
		}

		//take nearest point above all
		coords = nearestPoint.get();


		pi.setCoords2D(coords.getX(), coords.getY(), coords.getZ());
		pi.updateCoordsFrom2D(false,getCoordSys());
		pi.updateCoords();


	}
	
	public void regionChanged(GeoPointND PI){
		super.regionChanged(PI);
		PI.updateCoords2D();
		if(!isInRegion(PI))
			pointChanged(PI);
		
	}
	
	
	
    public boolean hasDrawable3D() {
    	return true;
    }
    
    public boolean isAllEndpointsLabelsSet() {
    	AlgoElement algo = this.getParentAlgorithm();
    	if(algo==null)//should never happen, just to be sure
    		return false; 
    	if(algo instanceof geogebra.kernel.AlgoConicPartConicPoints)
    		return ((AlgoConicPartConicPoints)algo).getStartPoint().isLabelSet() &&
    		((AlgoConicPartConicPoints)algo).getEndPoint().isLabelSet();
    	if(algo instanceof geogebra.kernel.AlgoConicPartCircumcircle)
    		return algo.getInput()[0].isLabelSet() && algo.getInput()[1].isLabelSet() &&
    		algo.getInput()[2].isLabelSet();	
    	return false;		
	} 
	
    public GeoPoint getPointParam(double param){
    	GeoPoint ret = new GeoPoint(cons);
    	this.pathChanged(ret);
    	ret.getPathParameter().setT(param);    
    	this.pathChanged(ret);
    	ret.updateCoords();        	
    	return ret;
    }
    
	
}
