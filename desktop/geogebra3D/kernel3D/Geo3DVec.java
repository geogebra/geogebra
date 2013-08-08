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

package geogebra3D.kernel3D;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.ListValue;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import geogebra.common.kernel.arithmetic3D.Vector3DValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.main.App;

import java.util.HashSet;

/** 
 * 
 * @author  Michael
 * adapted from GeoVec2D
 * @version 
 */
final public class Geo3DVec extends ValidExpression
implements Vector3DValue, geogebra.common.kernel.kernelND.Geo3DVec {        

    public double x = Double.NaN;
    public double y = Double.NaN;    
    public double z = Double.NaN;    
    
    private int mode; // POLAR or CARTESIAN   
    
    private Kernel kernel;
    
    /** Creates new GeoVec2D */
    public Geo3DVec(Kernel kernel) {
    	this.kernel = kernel;
    }
    
    /** Creates new GeoVec2D with coordinates (x,y)*/
    public Geo3DVec(Kernel kernel, double x, double y, double z) {
    	this(kernel);
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /** Creates new GeoVec2D with coordinates (a[0],a[1])*/
    public Geo3DVec(Kernel kernel, double [] a) {
    	this(kernel);
        x = a[0];
        y = a[1];
        z = a[2];
    }
    
    /** Copy constructor */
    public Geo3DVec(Geo3DVec v) {
    	this(v.kernel);
        x = v.x;
        y = v.y;
        z = v.z;
        mode = v.mode;
    }
    
	public ExpressionValue deepCopy(Kernel kernel) {
		return new Geo3DVec(this);
	}   
	
    public void resolveVariables(boolean forEquation) {     
    }
            
    /** Creates new GeoVec2D as vector between Points P and Q */
    public Geo3DVec(Kernel kernel, GeoPoint3D p, GeoPoint3D q) {   
    	this(kernel);    
        x = q.getX() - p.getX();
        y = q.getY() - p.getY();
        z = q.getZ() - p.getZ();
    }
   
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }
    public void setCoords(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public void setCoords(double [] a) {
        x = a[0];
        y = a[1];
        z = a[2];
    }
    
    public void setCoords(GeoVec3D v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }
    
  
    final public double getX() { return x; }
    final public double getY() { return y; }  
    final public double getZ() { return z; }  
    //final public double getR() {  return length(x, y); }
    //final public double getPhi() { return Math.atan2(y, x); }    
    
    final public double [] getCoords() {
        double [] res = { x, y, z };
        return res;
    }
    
    /** Calculates the eucilidian length of this 2D vector.
     * The result is sqrt(x^2  + y^2).
     */
    final public double length() {
        return length(x, y, z);
    } 
    
    /** Calculates the eucilidian length of this 2D vector.
     * The result is sqrt(a[0]^2  + a[1]^2).
     */
    final public static double length(double [] a) {
        return length(a[0], a[1], a[2]);
    } 
    
     /** Calculates the euclidian length sqrt(a^2  + b^2).     
     */
    final public static double length(double a, double b, double c) {                
        return Math.sqrt(a*a + b*b + c*c);

               
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
        return x * v.getX() + y * v.getY();
    }
    
    /** Yields true if the coordinates of this vector are equal to
     * those of vector v. 
     */
    final public boolean equals(GeoVec2D v) {                   
        return Kernel.isEqual(x, v.getX()) && Kernel.isEqual(y, v.getY());                   
    }
    
    /** Yields true if this vector and v are linear dependent 
     * This is done by calculating the determinant
     * of this vector an v: this = v <=> det(this, v) = nullvector.
     */
   // final public boolean linDep(GeoVec2D v) {
   //     // v = l* w  <=>  det(v, w) = o
   //     return kernel.isZero(det(this, v));                   
   // }
    
    /** calculates the determinant of u and v.
     * det(u,v) = u1*v2 - u2*v1
     */
    final public static double det(GeoVec2D u, GeoVec2D v) {
        return u.getX() * v.getY() - u.getY() * v.getX();
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
        x += v.getX();
        y += v.getY();
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
    final public void mirror(Coords Q) {           
        x = 2.0 * Q.getX() - x;
        y = 2.0 * Q.getY() - y;
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
    ///final public GeoVec2D add(GeoVec2D a) {
   ///     GeoVec2D res = new GeoVec2D(kernel, 0,0);
    //    add(this, a, res);
    //    return res;
    //}                
    
    /** c = a + b */
    final public static void add(Geo3DVec a, Geo3DVec b, Geo3DVec c) {                                       
        c.x = a.x + b.x;
        c.y = a.y + b.y;
        c.z = a.z + b.z;
    }
    
    /** c = a + b */
    final public static void add(Geo3DVec a, GeoVec2D b, Geo3DVec c) {                                       
        c.x = a.x + b.getX();
        c.y = a.y + b.getY();
        c.z = a.z;
    } 
    
    /** c = a + b */
    final public static void add(GeoVec2D a, Geo3DVec b, Geo3DVec c) {                                       
        c.x = a.getX() + b.x;
        c.y = a.getY() + b.y;
        c.z = b.z;
    } 
    
    /** c = Vector (Cross) Product of a and b */
    final public static void vectorProduct(Geo3DVec a, Geo3DVec b, Geo3DVec c) {    
    	// tempX/Y needed because a and c can be the same variable
    	double tempX = a.y * b.z - a.z * b.y;
    	double tempY = - a.x * b.z + a.z * b.x;
    	c.z = a.x * b.y - a.y * b.x;
        c.x = tempX;
        c.y = tempY;
    }
    
    /** (xc,yc) = (xa + b , yx)  ie complex + real for complex nos
     * or (xc,yc) = (xa + b , yx + b) for Points/Vectors
     * */
    final public static void add(GeoVec2D a, NumberValue b, GeoVec2D c) {    
    	
    	if (a.getMode() == Kernel.COORD_COMPLEX) {  	
	        c.setX(a.getX() + b.getDouble());
	        c.setY(a.getY());
          	c.setMode(Kernel.COORD_COMPLEX);
          	} else {
            c.setX(a.getX() + b.getDouble());
            c.setY(a.getY() + b.getDouble());   		
    	}
    }
    
    /** vector + 2D list (to give another vector) 
     * */
    final public static void add(GeoVec2D a, ListValue b, GeoVec2D c) {        	    	    	
    	MyList list = b.getMyList();    	
    	if (list.size() != 2) {
    		c.setX(Double.NaN);
    		c.setY(Double.NaN);
    		return;
    	}
    	
    	double enX = list.getListElement(0).evaluateDouble();
    	double enY = list.getListElement(1).evaluateDouble();
    	
    	c.setX(a.getX() + enX);
    	c.setY(a.getY() + enY);
    }
    
    /* vector - 2D list (to give another vector) 
     * */
    final public static void sub(GeoVec2D a, ListValue b, GeoVec2D c, boolean reverse) {    
    	
    	MyList list = b.getMyList();    	
    	if (list.size() != 2) {
    		c.setX(Double.NaN);
    		c.setY(Double.NaN);
    		return;
    	}
    	
    	double enX = list.getListElement(0).evaluateDouble();
    	double enY = list.getListElement(1).evaluateDouble();
    	

    	
    	if (reverse) {
	    	c.setX(a.getX() - enX);
	    	c.setY(a.getY() - enY);
    	} else {
	    	c.setX(enX - a.getX());
	    	c.setY(enY - a.getY());
    	}
    }
    
    /** (xc,yc) = (b - xa, -yx)  ie real - complex 
     * or (xc,yc) = (b - xa, b - yx)  for Vectors/Points
     * */
    final public static void sub(NumberValue b, GeoVec2D a, GeoVec2D c) {                                       
    	if (a.getMode() == Kernel.COORD_COMPLEX) {  	
            c.setX(b.getDouble() - a.getX());
            c.setY(-a.getY());
          	c.setMode(Kernel.COORD_COMPLEX);
    	} else {
            c.setX(b.getDouble() - a.getX());
            c.setY(b.getDouble() - a.getY());
    	}
    }
    
    /** (xc,yc) = (xa - b , yx)  ie complex - real 
     * or (xc,yc) = (xa - b , yx - b)   for Vectors/Points
     * */
    final public static void sub(GeoVec2D a, NumberValue b, GeoVec2D c) {                                       
    	if (a.getMode() == Kernel.COORD_COMPLEX) {  	
            c.setX(a.getX() - b.getDouble());
            c.setY(a.getY());
          	c.setMode(Kernel.COORD_COMPLEX);
    	} else {
            c.setX(a.getX() - b.getDouble());
            c.setY(a.getY() - b.getDouble());
    	}
    }
    
     /** returns this - a */
    //final public GeoVec2D sub(GeoVec2D a) {
    //    GeoVec2D res = new GeoVec2D(kernel, 0,0);
    //    sub(this, a, res);
    //    return res;
    //}
    
    /** c = a - b */
    final public static void sub(Geo3DVec a, Geo3DVec b, Geo3DVec c) {
        c.x = a.x - b.x;
        c.y = a.y - b.y;
        c.z = a.z - b.z;
    }  
    
    /** c = a - b */
    final public static void sub(Geo3DVec a, GeoVec2D b, Geo3DVec c) {
        c.x = a.x - b.getX();
        c.y = a.y - b.getY();
        c.z = a.z;
    }   
    
    /** c = a - b */
    final public static void sub(GeoVec2D a, Geo3DVec b, Geo3DVec c) {
        c.x = a.getX() - b.x;
        c.y = a.getY() - b.y;
        c.z = -b.z;
    }    
        
    final public void mult(double b) {
        x = b*x;
        y = b*y;
    }
    
    /** c = a * b */
    final public static void mult(Geo3DVec a, double b, Geo3DVec c) {
        c.x = a.x * b;
        c.y = a.y * b;        
        c.z = a.z * b;        
    }    
   

    final public static void inner(GeoVec2D a, GeoVec2D b, double c) {
        c = a.getX() * b.getX() + a.getY() * b.getY();        
    }       
    
    final public static void inner(Geo3DVec a, Geo3DVec b, MyDouble c) {
        c.set(a.x * b.x + a.y * b.y + a.z * b.z);        
    }           
    
    /** c = a / b */
    final public static void div(Geo3DVec a, double b, Geo3DVec c) {
        c.x = a.x / b;
        c.y = a.y / b;
        c.z = a.z / b;
    }        
    
    final public boolean isDefined() {		
		return !(Double.isNaN(x) || Double.isNaN( y));
	}
    
    @Override
	final public String toString(StringTemplate tpl) {          
		sbToString.setLength(0);
		sbToString.append('(');
		sbToString.append(kernel.format(x,tpl));
		sbToString.append(", ");
		sbToString.append(kernel.format(y,tpl));
		sbToString.append(')');         
        return sbToString.toString();
    }         
	private StringBuilder sbToString = new StringBuilder(50);
    
    /**
     * interface VectorValue implementation
     */           
    final public Geo3DVec getVector() {
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

    final public HashSet<GeoElement> getVariables() { return null; }
    
    final public void setMode(int mode) {
        this.mode = mode;
    }

	@Override
	final public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}  
	
	public String toLaTeXString(boolean symbolic,StringTemplate tpl) {
		return toString(tpl);
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
	 
	 final public boolean contains(ExpressionValue ev) {
		 return ev == this;
	 }
	 
	 /** multiplies 2D vector by a 2x2 matrix
	  * 
	  * @param list 2x2 matrix
	  */
	 public void multiplyMatrix(MyList list)
	 {
			if (list.getMatrixCols() != 2 || list.getMatrixRows() != 2) return;
		 
			double a,b,c,d,x1,y1;
			
			a = MyList.getCell(list,0,0).evaluateDouble();
			b = MyList.getCell(list,1,0).evaluateDouble();
			c = MyList.getCell(list,0,1).evaluateDouble();
			d = MyList.getCell(list,1,1).evaluateDouble();
	 
			x1 = a*x + b*y;
			y1 = c*x + d*y;
			x=x1;
			y=y1;
			return;
	 }
	 
	 /** multiplies 2D vector by a 3x3 affine matrix
	  *  a b c
	  *  d e f
	  *  g h i
	  * @param list 3x3 matrix
	  * @param rt GeoVec3D (as ExpressionValue) to get homogeneous coords from
	  */
	 public void multiplyMatrixAffine(MyList list, ExpressionValue rt)
	 {
			if (list.getMatrixCols() != 3 || list.getMatrixRows() != 3) return;
		 
			double a,b,c,d,e,f,g,h,i,xx = x, yy = y, zz = z;
			
			if (rt instanceof GeoVector3D) {
				GeoVector3D p = (GeoVector3D)rt;
				xx = p.getX();
				yy = p.getY();
				zz = p.getZ();
			} 
			
			a = MyList.getCell(list,0,0).evaluateDouble();
			b = MyList.getCell(list,1,0).evaluateDouble();
			c = MyList.getCell(list,2,0).evaluateDouble();
			d = MyList.getCell(list,0,1).evaluateDouble();
			e = MyList.getCell(list,1,1).evaluateDouble();
			f = MyList.getCell(list,2,1).evaluateDouble();
			g = MyList.getCell(list,0,2).evaluateDouble();
			h = MyList.getCell(list,1,2).evaluateDouble();
			i = MyList.getCell(list,2,2).evaluateDouble();
	 
			x = a * xx + b * yy + c * zz;
			y = d * xx + e * yy + f * zz;
			z = g * xx + h * yy + i * zz;
			return;
	 }
		public void setZero() {
			x=0;
			y=0;
			z=0;
		}

		public boolean isVector3DValue() {
			return true;
		}

		public static MyVec3DNode get3DVecNode() {
			// TODO Auto-generated method stub
			return null;
		}

		public static Coords get3DVector() {
			// TODO Auto-generated method stub
			return null;
		}

		public double[] getPointAsDouble() {
			return new double [] { getX(),getY(),getZ()};
		}

		public Geo3DVec get3DVec() {
			return this;
		}
		
		public String toOutputValueString(StringTemplate tpl) {
			return toValueString(tpl);
		}
		
		public Kernel getKernel() {
			return kernel;
		}

		public boolean isEqual(geogebra.common.kernel.kernelND.Geo3DVec vec) {
			Geo3DVec v= (Geo3DVec) vec;
			return Kernel.isEqual(x, v.x) && Kernel.isEqual(y, v.y) && Kernel.isEqual(z, v.z);
		}

		/**
		 * multiplies 3D vector/point by a 3x3 matrix a b c d e f g h i
		 * 
		 * @param list
		 *            3x3 matrix
		 * @param rt
		 *            Vector3DValue (as ExpressionValue) to get coords from
		 */
		public void multiplyMatrix(MyList list, ExpressionValue rt) {
			if (list.getMatrixCols() != 3 || list.getMatrixRows() != 3)
				return;

			double a, b, c, d, e, f, g, h, i, z1, xx = x, yy = y, zz = 1;

			if (rt.isVector3DValue()) {
				geogebra.common.kernel.kernelND.Geo3DVec v = ((Vector3DValue) rt).get3DVec();
				xx = v.getX();
				yy = v.getY();
				zz = v.getZ();

			} else {
				App.debug("error in Geo3DVec");
			}

			a = MyList.getCell(list, 0, 0).evaluateDouble();
			b = MyList.getCell(list, 1, 0).evaluateDouble();
			c = MyList.getCell(list, 2, 0).evaluateDouble();
			d = MyList.getCell(list, 0, 1).evaluateDouble();
			e = MyList.getCell(list, 1, 1).evaluateDouble();
			f = MyList.getCell(list, 2, 1).evaluateDouble();
			g = MyList.getCell(list, 0, 2).evaluateDouble();
			h = MyList.getCell(list, 1, 2).evaluateDouble();
			i = MyList.getCell(list, 2, 2).evaluateDouble();

			x = a * xx + b * yy + c * zz;
			y = d * xx + e * yy + f * zz;
			z = g * xx + h * yy + i * zz;

			return;
		}

}
