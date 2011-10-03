/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoVec2D.java
 *
 * Created on 31. August 2001, 11:34
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.ListValue;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.MyList;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.kernel.arithmetic.VectorValue;
import geogebra.util.Unicode;

import java.util.HashSet;

import org.apache.commons.math.complex.Complex;

/** 
 * 
 * @author  Markus
 * @version 
 */
final public class GeoVec2D extends ValidExpression implements MatrixTransformable, VectorValue {        

    public double x = Double.NaN;
    public double y = Double.NaN;    
    
    private int mode; // POLAR or CARTESIAN  
    
    private Kernel kernel;
    
    
    /** Creates new GeoVec2D */
    public GeoVec2D(Kernel kernel) {
    	this.kernel = kernel;
    }
    
    /** Creates new GeoVec2D with coordinates (x,y)*/
    public GeoVec2D(Kernel kernel, double x, double y) {
    	this(kernel);
        this.x = x;
        this.y = y;
    }
    
    /** Creates new GeoVec2D with coordinates (a[0],a[1])*/
    public GeoVec2D(Kernel kernel, double [] a) {
    	this(kernel);
        x = a[0];
        y = a[1];
    }
    
    /** Copy constructor */
    public GeoVec2D(GeoVec2D v) {
    	this(v.kernel);
        x = v.x;
        y = v.y;
        mode = v.mode;
    }
    
    
    public boolean isImaginaryUnit() {
    	return mode == Kernel.COORD_COMPLEX && x == 0 && y == 1;
    }
    
	public ExpressionValue deepCopy(Kernel kernel) {
		return new GeoVec2D(this);
	}   
	
    public void resolveVariables() {     
    }
            
    /** Creates new GeoVec2D as vector between Points P and Q */
    public GeoVec2D(Kernel kernel, GeoPoint p, GeoPoint q) {   
    	this(kernel);    
        x = q.x - p.x;
        y = q.y - p.y;
    }
   
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setCoords(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public void setCoords(double [] a) {
        x = a[0];
        y = a[1];
    }
    
    public void setCoords(GeoVec2D v) {
        x = v.x;
        y = v.y;
    }
    
    public void setPolarCoords(double r, double phi) {              
        x = r * Math.cos( phi );
        y = r * Math.sin( phi );  
    } 
   
    final public double getX() { return x; }
    final public double getY() { return y; }  
    final public double getR() {  return length(x, y); }
    final public double getPhi() { return Math.atan2(y, x); }    
    
    final public double [] getCoords() {
        double [] res = { x, y };
        return res;
    }
    
    /** Calculates the eucilidian length of this 2D vector.
     * The result is sqrt(x^2  + y^2).
     */
    final public double length() {
        return length(x, y);
    } 
    
    /** Calculates the eucilidian length of this 2D vector.
     * The result is sqrt(a[0]^2  + a[1]^2).
     */
    final public static double length(double [] a) {
        return length(a[0], a[1]);
    } 
    
     /** Calculates the euclidian length sqrt(a^2  + b^2).     
     */
    final public static double length(double a, double b) {                
        //return Math.sqrt(a*a + b*b);
        
        /*
         * The Algorithm ist taken
         * from Numerical Recipes, Appendix C, p.949 (Cabs) and
         * avoids overflows.         
         */
        double res;        
        double x = Math.abs(a);
        double y = Math.abs(b);
        
        if ( x == 0 ) 
            res = y;
        else if ( y == 0 )
            res = x;
        else if ( x > y ) {
            double temp = y / x;
            res  = x * Math.sqrt(1.0 + temp * temp);
        } else {
            double temp = x / y;
            res  = y * Math.sqrt(1.0 + temp * temp);
        }
        return res;
               
    } 
    
    /** Changes this vector to a vector with the same direction 
     * and orientation, but length 1.
     */
    final public void makeUnitVector() {
        double len = this.length();
        x = x / len;
        y = y / len;
    }
    
    /** Returns a new vector with the same direction 
     * and orientation, but length 1.
     */
    final public GeoVec2D getUnitVector() {
        double len = this.length();
        return new GeoVec2D(kernel,  x / len, y / len );
    }
    
    /** Returns the coordinates of a vector with the same direction 
     * and orientation, but length 1.
     */
    final public double[] getUnitCoords() {
        double len = this.length();
        double [] res = { x / len, y / len };
        return res;
    }

     /** Calculates the inner product of this vector and vector v.
     */
    final public double inner(GeoVec2D v) {
        return x * v.x + y * v.y;
    }
    
    /** Yields true if the coordinates of this vector are equal to
     * those of vector v. 
     */
    final public boolean equals(GeoVec2D v) {                   
        return kernel.isEqual(x, v.x) && kernel.isEqual(y, v.y);                   
    }
    
    /** Yields true if this vector and v are linear dependent 
     * This is done by calculating the determinant
     * of this vector an v: this = v <=> det(this, v) = nullvector.
     */
    final public boolean linDep(GeoVec2D v) {
        // v = l* w  <=>  det(v, w) = o
        return kernel.isZero(det(this, v));                   
    }
    
    /** calculates the determinant of u and v.
     * det(u,v) = u1*v2 - u2*v1
     */
    final public static double det(GeoVec2D u, GeoVec2D v) {
        return u.x * v.y - u.y * v.x;
        /*
        // symmetric operation
        // det(u,v) = -det(v,u)
        if (u.objectID < v.objectID) {
            return u.x * v.y - u.y * v.x;
        } else {
            return -(v.x * u.y - v.y * u.x);
        }*/
    }
    
    /**
     * translate this vector by vector v
     */
    final public void translate(GeoVec2D v) {
        x += v.x;
        y += v.y;
    }
    
    /**
     * rotate this vector by angle phi
     */
    final public void rotate(double phi) {
        double cos = Math.cos(phi);
        double sin = Math.sin(phi);
        
        double x0 = x * cos - y * sin;
        y = x * sin + y * cos;
        x = x0;        
    }  
    
    /**
     * mirror this point at point Q
     */
    final public void mirror(GeoPoint Q) {           
        x = 2.0 * Q.inhomX - x;
        y = 2.0 * Q.inhomY - y;
    }
    
    /**
     * mirror transform with angle phi
     *  [ cos(phi)       sin(phi)   ]
     *  [ sin(phi)      -cos(phi)   ]  
     */
    final public void mirror(double phi) {
        double cos = Math.cos(phi);
        double sin = Math.sin(phi);
                
        double x0 = x * cos + y * sin;
        y = x * sin - y * cos;
        x = x0;        
    }
    
     /** returns this + a */
    final public GeoVec2D add(GeoVec2D a) {
        GeoVec2D res = new GeoVec2D(kernel, 0,0);
        add(this, a, res);
        return res;
    }                
    
    /** c = a + b */
    final public static void add(GeoVec2D a, GeoVec2D b, GeoVec2D c) {                                       
        c.x = a.x + b.x;
        c.y = a.y + b.y;
        if (a.getMode() == Kernel.COORD_COMPLEX || b.getMode() ==Kernel.COORD_COMPLEX)
        	c.setMode(Kernel.COORD_COMPLEX);
    }
    
    /** (xc,yc) = (xa + b , yx)  ie complex + real for complex nos
     * or (xc,yc) = (xa + b , yx + b) for Points/Vectors
     * */
    final public static void add(GeoVec2D a, NumberValue b, GeoVec2D c) {    
    	
    	if (a.getMode() == Kernel.COORD_COMPLEX) {  	
	        c.x = a.x + b.getDouble();
	        c.y = a.y;
          	c.setMode(Kernel.COORD_COMPLEX);
          	} else {
            c.x = a.x + b.getDouble();
            c.y = a.y + b.getDouble();   		
    	}
    }
    
    /** vector + 2D list (to give another vector) 
     * */
    final public static void add(GeoVec2D a, ListValue b, GeoVec2D c) {        	    	    	
    	MyList list = b.getMyList();    	
    	if (list.size() != 2) {
    		c.x = Double.NaN;
    		c.y = Double.NaN;
    		return;
    	}
    	
    	ExpressionValue enX = list.getListElement(0).evaluate();
    	ExpressionValue enY = list.getListElement(1).evaluate();
    	
    	if (!enX.isNumberValue() || !enY.isNumberValue()) {
    		c.x = Double.NaN;
    		c.y = Double.NaN;
    		return;    		
    	}
    	
    	c.x = a.x + ((NumberValue)enX).getDouble();
    	c.y = a.y + ((NumberValue)enY).getDouble();
    }
    
    /* vector - 2D list (to give another vector) 
     * */
    final public static void sub(GeoVec2D a, ListValue b, GeoVec2D c, boolean reverse) {    
    	
    	MyList list = b.getMyList();    	
    	if (list.size() != 2) {
    		c.x = Double.NaN;
    		c.y = Double.NaN;
    		return;
    	}
    	
    	ExpressionValue enX = list.getListElement(0).evaluate();
    	ExpressionValue enY = list.getListElement(1).evaluate();
    	
    	if (!enX.isNumberValue() || !enY.isNumberValue()) {
    		c.x = Double.NaN;
    		c.y = Double.NaN;
    		return;    		
    	}
    	
    	if (reverse) {
	    	c.x = a.x - ((NumberValue)enX).getDouble();
	    	c.y = a.y - ((NumberValue)enY).getDouble();
    	} else {
	    	c.x = ((NumberValue)enX).getDouble() - a.x;
	    	c.y = ((NumberValue)enY).getDouble() - a.y;
    	}
    }
    
    /** (xc,yc) = (b - xa, -yx)  ie real - complex 
     * or (xc,yc) = (b - xa, b - yx)  for Vectors/Points
     * */
    final public static void sub(NumberValue b, GeoVec2D a, GeoVec2D c) {                                       
    	if (a.getMode() == Kernel.COORD_COMPLEX) {  	
            c.x = b.getDouble() - a.x;
            c.y = -a.y;
          	c.setMode(Kernel.COORD_COMPLEX);
    	} else {
            c.x = b.getDouble() - a.x;
            c.y = b.getDouble() - a.y;
    	}
    }
    
    /** (xc,yc) = (xa - b , yx)  ie complex - real 
     * or (xc,yc) = (xa - b , yx - b)   for Vectors/Points
     * */
    final public static void sub(GeoVec2D a, NumberValue b, GeoVec2D c) {                                       
    	if (a.getMode() == Kernel.COORD_COMPLEX) {  	
            c.x = a.x - b.getDouble();
            c.y = a.y;
          	c.setMode(Kernel.COORD_COMPLEX);
    	} else {
            c.x = a.x - b.getDouble();
            c.y = a.y - b.getDouble();
    	}
    }
    
     /** returns this - a */
    final public GeoVec2D sub(GeoVec2D a) {
        GeoVec2D res = new GeoVec2D(kernel, 0,0);
        sub(this, a, res);
        return res;
    }
    
    /** c = a - b */
    final public static void sub(GeoVec2D a, GeoVec2D b, GeoVec2D c) {
        c.x = a.x - b.x;
        c.y = a.y - b.y;
        if (a.getMode() == Kernel.COORD_COMPLEX || b.getMode() ==Kernel.COORD_COMPLEX)
        	c.setMode(Kernel.COORD_COMPLEX);
    }       
        
    final public void mult(double b) {
        x = b*x;
        y = b*y;
    }
    
    /** c = a * b */
    final public static void mult(GeoVec2D a, double b, GeoVec2D c) {
        c.x = a.x * b;
        c.y = a.y * b;        
    }    
   
    /** c = a / b Michael Borcherds 2007-12-09 
     * 
     * */
    final public static void complexDivide(GeoVec2D a, GeoVec2D b, GeoVec2D c) {                                       
    	// NB temporary variables *crucial*: a and c can be the same variable
    	//double x1=a.x,y1=a.y,x2=b.x,y2=b.y;
    	// complex division
      //c.x = (x1 * x2 + y1 * y2)/(x2 * x2 + y2 * b.y);
      //c.y = (y1 * x2 - x1 * y2)/(x2 * x2 + y2 * b.y);
      
      Complex out = new Complex(a.x, a.y);     
      out = out.divide(new Complex(b.x, b.y));
      c.x = out.getReal();
      c.y = out.getImaginary();
    	c.setMode(Kernel.COORD_COMPLEX);
      
    }
    
    /** c = a / b Michael Borcherds 2008-08-12 
     * 
     * */
    final public static void complexDivide(NumberValue a, GeoVec2D b, GeoVec2D c) {                                       
    	// NB temporary variables *crucial*: a and c can be the same variable
    	//double x1=a.getDouble(), x2 = b.x, y2 = b.y;
    	// complex division
      //c.x = (x1 * x2 )/(x2 * x2 + y2 * b.y);
      //c.y = ( - x1 * y2)/(x2 * x2 + y2 * b.y);

    
        Complex out = new Complex(a.getDouble(), 0);     
        out = out.divide(new Complex(b.x, b.y));
        c.x = out.getReal();
        c.y = out.getImaginary();
    	c.setMode(Kernel.COORD_COMPLEX);
}
    
    /** c = a * b Michael Borcherds 2007-12-09 */
    final public static void complexMultiply(GeoVec2D a, GeoVec2D b, GeoVec2D c) {                                       
    	// NB temporary variables *crucial*: a and c can be the same variable
    	//double x1=a.x,y1=a.y,x2=b.x,y2=b.y;
    	//  do multiply
      //c.x = (x1 * x2 - y1 * y2);
      //c.y = (y2 * x1 + x2 * y1);
    	
    	
      Complex out = new Complex(a.x, a.y);     
      out = out.multiply(new Complex(b.x, b.y));
      c.x = out.getReal();
      c.y = out.getImaginary();

    	c.setMode(Kernel.COORD_COMPLEX);
    }

    /** c = a ^ b Michael Borcherds 2009-03-10 */
    final public static void complexPower(GeoVec2D a, NumberValue b, GeoVec2D c) {                                       
        Complex out = new Complex(a.x, a.y);     
        out = out.pow(new Complex(b.getDouble(), 0));
        c.x = out.getReal();
        c.y = out.getImaginary();
      	c.setMode(Kernel.COORD_COMPLEX);
    }

    /** c = sqrt(a) Michael Borcherds 2010-02-07 */
    final public static void complexSqrt(GeoVec2D a, GeoVec2D c) {                                       
        Complex out = new Complex(a.x, a.y);     
        out = out.sqrt();
        c.x = out.getReal();
        c.y = out.getImaginary();
      	c.setMode(Kernel.COORD_COMPLEX);
    }

    /** c = sqrt(a) Michael Borcherds 2010-02-07 */
    final public static void complexCbrt(GeoVec2D a, GeoVec2D c) {                                       
        Complex out = new Complex(a.x, a.y);     
        out = out.pow(new Complex(1/3d, 0));
        c.x = out.getReal();
        c.y = out.getImaginary();
      	c.setMode(Kernel.COORD_COMPLEX);
    }

    /** c = sqrt(a) Michael Borcherds 2010-02-07 */
    final public static void complexConjugate(GeoVec2D a, GeoVec2D c) {                                       
        Complex out = new Complex(a.x, a.y);     
        out = out.conjugate();
        c.x = out.getReal();
        c.y = out.getImaginary();
      	c.setMode(Kernel.COORD_COMPLEX);
    }

    /** c = sqrt(a) Michael Borcherds 2010-02-07 */
    final public static double arg(GeoVec2D a) {                                       
        return Math.atan2(a.y, a.x);
    }

    /** c = a ^ b Michael Borcherds 2009-03-10 */
    final public static void complexPower(NumberValue a, GeoVec2D b, GeoVec2D c) {                                       
        Complex out = new Complex(a.getDouble(), 0);     
        out = out.pow(new Complex(b.x, b.y));
        c.x = out.getReal();
        c.y = out.getImaginary();
      	c.setMode(Kernel.COORD_COMPLEX);
    }

    /** c = e ^ a Michael Borcherds 2009-03-10 */
    final public static void complexExp(GeoVec2D a, GeoVec2D c) {                                       
        Complex out = new Complex(a.x, a.y);     
        out = out.exp();
        c.x = out.getReal();
        c.y = out.getImaginary();
      	c.setMode(Kernel.COORD_COMPLEX);
    }

    /** c = natural log(a) Michael Borcherds 2009-03-10 */
    final public static void complexLog(GeoVec2D a, GeoVec2D c) {                                       
        Complex out = new Complex(a.x, a.y);     
        out = out.log();
        c.x = out.getReal();
        c.y = out.getImaginary();
      	c.setMode(Kernel.COORD_COMPLEX);
    }

    /** c = abs(a) Michael Borcherds 2009-03-10 */
    final public static double complexAbs(GeoVec2D a) {                                       
        Complex out = new Complex(a.x, a.y);     

        return out.abs();
    }

    /** c = a ^ b Michael Borcherds 2009-03-14 */
    final public static void complexPower(GeoVec2D a, GeoVec2D b, GeoVec2D c) {                                       
        Complex out = new Complex(a.x, a.y);     
        out = out.pow(new Complex(b.x, b.y));
        c.x = out.getReal();
        c.y = out.getImaginary();
      	c.setMode(Kernel.COORD_COMPLEX);
    }

    /** c = a * b Michael Borcherds 2007-12-09 */
    final public static void complexMultiply(GeoVec2D a, NumberValue b, GeoVec2D c) {                                       
    	// NB temporary variables *crucial*: a and c can be the same variable
    	//double x1=a.x,y1=a.y,x2=b.getDouble();
    	//  do multiply
      //c.x = (x1 * x2);
      //c.y = (x2 * y1);
        Complex out = new Complex(a.x, a.y);     
        out = out.multiply(new Complex(b.getDouble(), 0));
        c.x = out.getReal();
        c.y = out.getImaginary();

    	c.setMode(Kernel.COORD_COMPLEX);
    }

    /*
     * see also GeoVec3D.vectorProduct()
     */
    final public static void vectorProduct(GeoVec2D a, GeoVec2D b, MyDouble c) {
        c.set(a.x * b.y - a.y * b.x);        
    }           
    
    
    final public static void inner(GeoVec2D a, GeoVec2D b, MyDouble c) {
        c.set(a.x * b.x + a.y * b.y);        
    }           
    
    /** c = a / b */
    final public static void div(GeoVec2D a, double b, GeoVec2D c) {
        c.x = a.x / b;
        c.y = a.y / b;
    }        
    
    final public boolean isDefined() {		
		return !(Double.isNaN(x) || Double.isNaN( y));
	}
    
    final public String toString() {
    	if (isImaginaryUnit()){
    		switch (kernel.getCASPrintForm()){
    			case ExpressionNode.STRING_TYPE_MPREDUCE:
    				return "i";
    				
    			default:
//    			case ExpressionNode.STRING_TYPE_GEOGEBRA:
//    			case ExpressionNode.STRING_TYPE_GEOGEBRA_XML:
//    			case ExpressionNode.STRING_TYPE_LATEX:
    				return Unicode.IMAGINARY;
    		}
    	}
    	
		sbToString.setLength(0);
		sbToString.append('(');
		sbToString.append(kernel.format(x));
		sbToString.append(", ");
		sbToString.append(kernel.format(y));
		sbToString.append(')');         
        return sbToString.toString();
    }         
	private StringBuilder sbToString = new StringBuilder(50);
    
    /**
     * interface VectorValue implementation
     */           
    final public GeoVec2D getVector() {
    	if (this.isImaginaryUnit()) 
    		return new GeoVec2D(this);
    	else 
    		return this;
    }        
        
    final public boolean isConstant() {
        return true;
    }
    
    final public boolean isLeaf() {
        return true;
    }             
        
    final public int getMode() {
        return  mode;
    }        
    
    final public ExpressionValue evaluate() { 
    	return getVector(); 
    }
    
    final public HashSet<GeoElement> getVariables() { return null; }
    
    final public void setMode(int mode) {
        this.mode = mode;
    }

	final public String toValueString() {
		return toString();
	}  
	
	public String toLaTeXString(boolean symbolic) {
		return toString();
	}    
    
    
    // abstract methods of GeoElement
    /*
    final public GeoElement copy() {
        return new GeoVec2D(this);
    }
    
    final public void set(GeoElement geo) {
        GeoVec2D v = (GeoVec2D) geo;
        this.x = v.x;
        this.y = v.y;
    }
    
    final public boolean isDefined() {
        return true;
    }
     */
     
	 final public boolean isNumberValue() {
		 return false;
	 }

	 final public boolean isVectorValue() {
		 return true;
	 }
	 
    final public boolean isBooleanValue() {
        return false;
    }

	 final public boolean isPolynomialInstance() {
		 return false;
	 }   
	 
	 final public boolean isTextValue() {
		 return false;
	 }
	 
	 final public boolean isExpressionNode() {
		 return false;
	 }	 	 
	 
    public boolean isListValue() {
        return false;
    }
	 
	 final public boolean contains(ExpressionValue ev) {
		 return ev == this;
	 }
	 
	 /** multiplies 2D vector by a 2x2 matrix
	  * 
	  * @param 2x2 matrix
	  */
	 public void multiplyMatrix(MyList list)
	 {
			if (list.getMatrixCols() != 2 || list.getMatrixRows() != 2) return;
		 
			double a,b,c,d,x1,y1;
			
			a = ((NumberValue)(MyList.getCell(list,0,0).evaluate())).getDouble();
			b = ((NumberValue)(MyList.getCell(list,1,0).evaluate())).getDouble();
			c = ((NumberValue)(MyList.getCell(list,0,1).evaluate())).getDouble();
			d = ((NumberValue)(MyList.getCell(list,1,1).evaluate())).getDouble();
	 
			matrixTransform(a,b,c,d);
	 }
	 public void matrixTransform(double a,double b,double c,double d) {
		 
			Double x1 = a*x + b*y;
			Double y1 = c*x + d*y;

			x=x1; 
			y=y1;		
	 }
	 public boolean isMatrixTransformable(){
		 return true;
	 }
	 public GeoElement toGeoElement(){
		 return null;
	 }
	 /** multiplies 2D vector by a 3x3 affine matrix
	  *  a b c
	  *  d e f
	  *  g h i
	  * @param 3x3 matrix
	  * @param GeoVec3D (as ExpressionValue) to get homogeneous coords from
	  */
	 public void multiplyMatrixAffine(MyList list, ExpressionValue rt)
	 {
			if (list.getMatrixCols() != 3 || list.getMatrixRows() != 3) return;
		 
			double a,b,c,d,e,f,g,h,i,x1,y1,z1,xx = x, yy = y, zz = 1;
			
			// use homogeneous coordinates if available
			if ((rt instanceof GeoPoint) || (rt instanceof GeoLine)) {
				GeoVec3D p = (GeoVec3D)rt;
				xx = p.x;
				yy = p.y;
				zz = p.z;
			} 
			
			a = ((NumberValue)(MyList.getCell(list,0,0).evaluate())).getDouble();
			b = ((NumberValue)(MyList.getCell(list,1,0).evaluate())).getDouble();
			c = ((NumberValue)(MyList.getCell(list,2,0).evaluate())).getDouble();
			d = ((NumberValue)(MyList.getCell(list,0,1).evaluate())).getDouble();
			e = ((NumberValue)(MyList.getCell(list,1,1).evaluate())).getDouble();
			f = ((NumberValue)(MyList.getCell(list,2,1).evaluate())).getDouble();
			g = ((NumberValue)(MyList.getCell(list,0,2).evaluate())).getDouble();
			h = ((NumberValue)(MyList.getCell(list,1,2).evaluate())).getDouble();
			i = ((NumberValue)(MyList.getCell(list,2,2).evaluate())).getDouble();
			
			x1 = a * xx + b * yy + c * zz;
			y1 = d * xx + e * yy + f * zz;
			z1 = g * xx + h * yy + i * zz;
			
			x=x1 / z1;
			y=y1 / z1;
			return;
	 }
		public void setZero() {
			x=0;
			y=0;
		}

		public boolean isVector3DValue() {
			// TODO Auto-generated method stub
			return false;
		}
		
		public String toOutputValueString() {
			return toValueString();
		}

		public void matrixTransform(double a00, double a01, double a02,
				double a10, double a11, double a12, double a20, double a21,
				double a22) {
			
			double	xx = x;
			double	yy = y;
			double	zz = 1;
			
	 
			double x1 = a00 * xx + a01 * yy + a02 * zz;
			double y1 = a10 * xx + a11 * yy + a12 * zz;
			double z1 = a20 * xx + a21 * yy + a22 * zz;
			x=x1 / z1;
			y=y1 / z1;
			return;
			
		}
		
		public Kernel getKernel() {
			return kernel;
		}

		
}
