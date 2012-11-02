/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoVec3D.java
 *
 * Created on 31. August 2001, 11:22
 */

package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.kernelND.CoordStyle;

/**
 *
 * @author  Markus
 */
public abstract class GeoVec3D extends GeoElement 
implements Traceable, CoordStyle {
    /** x coordinate*/
    public double x =  Double.NaN;
    /** y coordinate*/
    public double y =  Double.NaN;
    /** z coordinate*/
    public double z =  Double.NaN;
    
	private boolean trace;		 
	/**
	 * For backward compatibility
	 */
	public boolean hasUpdatePrevilege = false;
    
    /**
     * @param c construction
     */
    public GeoVec3D(Construction c) {
    	super(c);
	
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		//http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings
    }  
    
    /** Creates new GeoVec3D with coordinates (x,y,z) and label 
     * @param c construction
     * @param x x-coord
     * @param y y-coord
     * @param z z-coord*/
    public GeoVec3D(Construction c, double x, double y, double z) {    	
    	this(c);    	
       setCoords(x, y, z);
    }                 
    
    /** Copy constructor 
     * @param c construction
     * @param v vector to copy*/
    public GeoVec3D(Construction c, GeoVec3D v) {   
    	this(c); 	
        set(v);
    }
    
//    public GeoElement copy() {
//        return new GeoVec3D(this.cons, this);        
//    }
    
    @Override
	public boolean isDefined() {
    	return (!(Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)));        
    }
    
    @Override
	public void setUndefined() {     
    	setCoords(Double.NaN, Double.NaN, Double.NaN);   
    	update(); //TODO hide undefined elements in algebraView
    }       
    
    @Override
	protected boolean showInEuclidianView() {     
        return isDefined();
    }
    
    @Override
	public boolean showInAlgebraView() {
       // return true;
	   //return isDefined();
    	return true;
    }        
    
    @Override
	public void set(GeoElement geo) {    
        GeoVec3D v = (GeoVec3D) geo;        
        setCoords(v.x, v.y, v.z);        
    }         
    
	/**
	 * @param x x-coord
	 * @param y y-coord
	 * @param z z-coord
	 */
	public abstract void setCoords(double x, double y, double z);
	/**
	 * Set coords from source vector
	 * @param v source vector
	 */
	public abstract void setCoords(GeoVec3D v) ;
   
    /**
     * @return x-coord
     */
    final public double getX() { return x; }
    /**
     * @return y-coord
     */
    final public double getY() { return y; }
    /**
     * @return z-coord
     */
    final public double getZ() { return z; } 
    /**
     * @param ret array to store coords
     */
    final public void getCoords(double[] ret) {
        ret[0] = x;
        ret[1] = y;
        ret[2] = z;        
    }             
    /**
     * @return this vector as coords
     */
    final public Coords getCoords() {
    	Coords coords = new Coords(3);
    	coords.setX(x);
    	coords.setY(y);
    	coords.setZ(z);
    	return coords;        
    }
    
    /** 
     * Writes x and y to the array res.
     * @param res array to store x and y
     */
    public void getInhomCoords(double [] res) {       
        res[0] = x;
        res[1] = y;                                
    }
    
    // POLAR or CARTESIAN mode    
    /**
     * @return true if using POLAR style
     */
    final public boolean isPolar() { return toStringMode == Kernel.COORD_POLAR; }
    /** 
     * @return currently used coordstyle
     */
    public int getMode() { return toStringMode;  }
    /**
     * Sets the coord style
     * @param mode new coord style
     */
    public void setMode(int mode ) {
        toStringMode = mode;
    }        
    
    /**
     * Changes coord style to POLAR
     */
    public void setPolar() { toStringMode = Kernel.COORD_POLAR; }
    /**
     * Changes coord style to CARTESIAN
     */
    public void setCartesian() { toStringMode = Kernel.COORD_CARTESIAN; }
    /**
     * Changes coord style to COMPLEX
     */
    public void setComplex() { toStringMode = Kernel.COORD_COMPLEX; }  
    /**
     * Changes coord style to CARTESIAN 3D
     */
    public void setCartesian3D() { toStringMode = Kernel.COORD_CARTESIAN_3D; }
       
	@Override
	public boolean isTraceable() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}
    
    //G.Sturr 2010-5-14: no longer needed
	/*
	public void setSpreadsheetTrace(boolean spreadsheetTrace) {
		this.spreadsheetTrace = spreadsheetTrace;
		
		if (spreadsheetTrace) resetTraceColumns();
	}
	 
	

	public boolean getSpreadsheetTrace() {
		return spreadsheetTrace;
	}
	
	*/
	//END G.Sturr
	
	
    /** Yields true if this vector and v are linear dependent 
     * This is done by calculating the cross product
     * of this vector an v: this lin.dep. v <=> this.cross(v) = nullvector.
     * @param v other vector
     * @return true if this and other vector are linear dependent
     */
    final public boolean linDep(GeoVec3D v) {
        // v lin.dep this  <=>  cross(v,w) = o            
        return Kernel.isEqual(y * v.z, z * v.y)
			&& Kernel.isEqual(z * v.x, x * v.z) 
			&& Kernel.isEqual(x * v.y, y * v.x);       
    }
    
    /**
     * @return tue if all coords are zero
     */
    final public boolean isZero() {
        return Kernel.isZero(x) && Kernel.isZero(y) && Kernel.isZero(z);
    }
    
     /** Calculates the cross product of this vector and vector v.
     * The result ist returned as a new GeoVec3D.
     */
//    final public GeoVec3D cross(GeoVec3D v) {         
//       GeoVec3D res = new GeoVec3D(v.cons);
//       cross(this, v, res);
//       return res;
//    }
    
    /** Calculates the cross product of vectors u and v.
     * The result is stored in w.
     * @param u vector u
     * @param v vector v
     * @param w vector to store u x v
     */
    final public static void cross(GeoVec3D u, GeoVec3D v, GeoVec3D w) {                
        w.setCoords( u.y * v.z - u.z * v.y, 
        					 u.z * v.x - u.x * v.z,  
        					 u.x * v.y - u.y * v.x );              
    }  
    
    
    /** Calculates the cross product of vectors u and v.
     * @param u vector u
     * @param v vector v
     * @return the cross product of vectors u and v.
     */
    final public static Coords cross(GeoVec3D u, GeoVec3D v){
    	Coords ret = new Coords(3);
    	ret.setX(u.y * v.z - u.z * v.y);
    	ret.setY(u.z * v.x - u.x * v.z);
    	ret.setZ(u.x * v.y - u.y * v.x);
    	
    	return ret;
    	
    }
    
    /** Calculates the line through the points A and B.
     * The result is stored in g.
     * @param A first point
     * @param B second point
     * @param g line to store the result
     */
    final public static void lineThroughPoints(GeoPoint A, GeoPoint B, GeoLine g) {
    	// note: this could be done simply using cross(A, B, g)
    	// but we want to avoid large coefficients in the line
    	// and we want AB to be the direction vector of the line
    	
    	if (!(A.isDefined() && B.isDefined())) {
    		g.setUndefined();
    		return;
    	}
    	
    	if (A.isInfinite()) {// A is direction
    		if (B.isInfinite()) { 
				// g is undefined
			    g.setUndefined();
			} else { 
				// through point B
				g.setCoords(A.getY() , 
		    			    -A.getX(),
						    A.getX() * B.getInhomY() - A.getY() * B.getInhomX());
			}
    	}
    	else { // through point A
			if (B.isInfinite()) { 
				// B is direction
			    g.setCoords(-B.getY(), 
	    			        B.getX(),
					        A.getInhomX() * B.getY() - A.getInhomY() * B.getX());
			} else { 
				// through point B
				g.setCoords(A.getInhomY() - B.getInhomY(), 
		    			   B.getInhomX() - A.getInhomX(),
						   A.getInhomX() * B.getInhomY() - A.getInhomY() * B.getInhomX());
			}
    	}            
    }  
    
    /** Calculates the line through the points A and B.
     * The result is stored in g.
     * @param A first point
     * @param B second point
     * @param g line to store result
     */
    final public static void lineThroughPointsCoords(Coords A, Coords B, GeoLine g) {
    	// note: this could be done simply using cross(A, B, g)
    	// but we want to avoid large coefficients in the line
    	// and we want AB to be the direction vector of the line
    	if (!(A.getRows()==3 && A.getColumns()==1 && 
    			B.getRows()==3 && B.getColumns()==1)) {
    		g.setUndefined();
    		return;
    	}
    	
    	if (!(A.isFinite() && B.isFinite())) {
    		g.setUndefined();
    		return;
    	}
    	
    	if (Kernel.isZero(A.getZ())) {// A is direction
    		if (Kernel.isZero(B.getZ())) { 
				// g is undefined
			    g.setUndefined();
			} else { 
				// through point B
				Coords BInhom = B.getInhomCoords();
				g.setCoords(A.getY() , 
						-A.getX(),
						A.getX() * BInhom.getY() - A.getY() * BInhom.getX());
			}
    	}
    	else { // through point A
			if (Kernel.isZero(B.getZ())) { 
				// B is direction
				Coords AInhom = A.getInhomCoords();
				g.setCoords(B.getY() , 
						-B.getX(),
						B.getX() * AInhom.getY() - B.getY() * AInhom.getX());
			} else { 
				// through point B
				Coords AInhom = A.getInhomCoords();
				Coords BInhom = B.getInhomCoords();
				g.setCoords(AInhom.getY() - BInhom.getY(), 
						BInhom.getX() - AInhom.getX(),
						AInhom.getX() * BInhom.getY() - AInhom.getY() * BInhom.getX());
			}
    	}            
    }  
    
    
    /** Calculates the line through the point A with direction v.
     * The result is stored in g.
     * @param A start point
     * @param v direction vector
     * @param g line to store result
     */
    final public static void lineThroughPointVector(GeoPoint A, GeoVec3D v, GeoLine g) {
    	// note: this could be done simply using cross(A, v, g)
    	// but we want to avoid large coefficients in the line
    	// and we want v to be the direction vector of the line
    	
    	if (A.isInfinite()) {// A is direction
			g.setUndefined();
    	}
    	else { // through point A
			// v is direction
		    g.setCoords(-v.y, 
    			        v.x,
				        A.getInhomX() * v.y - A.getInhomY() * v.x);
    	}        
    }  
    
    /** Calculates the cross product of vectors u and v.
     * The result is stored in w.
     * @param u vector u
     * @param vx x(v)
     * @param vy y(v)
     * @param vz z(v)
     * @param w vector to store u * v
     */
    final public static void cross(GeoVec3D u, 
                                   double vx, double vy, double vz, GeoVec3D w) {
		w.setCoords( u.y * vz - u.z * vy, 
							 u.z * vx - u.x * vz,  
							 u.x * vy - u.y * vx );                                 
    }
    
    /** Calculates the cross product of vectors u and v.
     * The result is stored in w.
     * @param ux x(u)
     * @param uy y(u)
     * @param uz z(u)
     * @param vx x(v)
     * @param vy y(v)
     * @param vz z(v)
     * @param w vector to store u*v
     */
    final public static void cross(double ux, double uy, double uz, 
                                   double vx, double vy, double vz, GeoVec3D w) {
		w.setCoords( uy * vz - uz * vy, 
							 uz * vx - ux * vz,  
							 ux * vy - uy * vx );                                 
    }
    
     /** Calculates the inner product of this vector and vector v.
      * @param v other vector
      * @return inner product
     */
    final public double inner(GeoVec3D v) {
        return x * v.x + y * v.y + z * v.z;
    }
    
    /** Changes orientation of this vector. v is changed to -v.    
     */
    final public void changeSign() {
        setCoords(-x, -y, -z);        
    }
    
    /** returns -v
     */
//    final public GeoVec3D getNegVec() {
//        return new GeoVec3D(cons, -x, -y, -z);        
//    }
    
    /** returns this + a */
//    final public GeoVec3D add(GeoVec3D a) {
//        GeoVec3D res = new GeoVec3D(cons);
//        add(this, a, res);
//        return res;
//    }    
        
    /** c = a + b 
     * @param a vector a
     * @param b vector b
     * @param c vector to store a+b
     **/
    final public static void add(GeoVec3D a, GeoVec3D b, GeoVec3D c) {                
        c.setCoords(a.x + b.x, a.y + b.y, a.z + b.z);    
    }
    
     /** returns this - a */
//    final public GeoVec3D sub(GeoVec3D a) {
//        GeoVec3D res = new GeoVec3D(cons);
//        sub(this, a, res);
//        return res;
//    }
    
    /** c = a - b 
     * @param a vector a
     * @param b vector b
     * @param c vector to store a-b*/
    final public static void sub(GeoVec3D a, GeoVec3D b, GeoVec3D c) {
		c.setCoords(a.x - b.x, a.y - b.y, a.z - b.z);         
    }       
    
    @Override
	public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append('(');
		sbToString.append(x);
		sbToString.append(", ");
		sbToString.append(y);
		sbToString.append(", ");
		sbToString.append(z);
		sbToString.append(')');
        return sbToString.toString();
    }
	private StringBuilder sbToString = new StringBuilder(50);
	
    /**
     * returns all class-specific xml tags for saveXML
     * Geogebra File Format
     */
	@Override
	protected void getXMLtags(StringBuilder sb) {
        super.getXMLtags(sb);
        
        sb.append("\t<coords");
        sb.append(" x=\""); sb.append(x); sb.append("\"");
        sb.append(" y=\""); sb.append(y); sb.append("\"");
        sb.append(" z=\""); sb.append(z); sb.append("\"");        
        sb.append("/>\n");

    }

	@Override
	public void getXMLtagsMinimal(StringBuilder sb,StringTemplate tpl) {
		sb.append(regrFormat(x) + " " + regrFormat(y) + " " + regrFormat(z));
	}
	
	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	public boolean isVectorValue() {
		return false;
	}

	@Override
	public boolean isPolynomialInstance() {
		return false;
	}   
	
	@Override
	public void setZero() {
		x=0;
		y=0;
		z=0;
	}
	
	/**
	 * @param phi angle of rotation
	 */
	protected void rotateXY(NumberValue phi){
		double ph = phi.getDouble();
        double cos = Math.cos(ph);
        double sin = Math.sin(ph);
        
        double x0 = x * cos - y * sin;
        y = x * sin + y * cos;
        x = x0; 
	}
	
	/**
     * mirror transform with angle phi
     *  [ cos(phi)       sin(phi)   ]
     *  [ sin(phi)      -cos(phi)   ]  
	 * @param phi parameter of mirror transform
     */
    protected final void mirrorXY(double phi) {
        double cos = Math.cos(phi);
        double sin = Math.sin(phi);
                
        double x0 = x * cos + y * sin;
        y = x * sin - y * cos;
        x = x0;        
    }
	
    @Override
	public boolean hasCoords() {
		return true;
	}
}
