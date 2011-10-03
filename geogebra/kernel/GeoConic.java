/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoConic.java
 *
 * Created on 10. September 2001, 08:52
 */

package geogebra.kernel;

import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.main.Application;
import geogebra.util.MyMath;

import java.util.ArrayList;

/**
 * Conics in 2D
 */
public class GeoConic extends GeoConicND
implements Path, Region, Traceable, ConicMirrorable, Transformable, 
Translateable, PointRotateable, Mirrorable, Dilateable, LineProperties, MatrixTransformable
{
	
	private static final long serialVersionUID = 1L;


	/* 
	 *               ( A[0]  A[3]    A[4] )
	 *      matrix = ( A[3]  A[1]    A[5] )
	 *               ( A[4]  A[5]    A[2] )
	 */


	

	

	/**
	 * Creates a conic
	 * @param c construction
	 */
	public GeoConic(Construction c) {
		super(c,2);	
	}		


	
	/** 
	 * Creates new GeoConic with Coordinate System for 3D 
	 * @param c construction
	 * @param label label
	 * @param coeffs coefficients
	 */
	protected GeoConic(Construction c, String label, double[] coeffs) {

		this(c);
		setCoeffs(coeffs);
		setLabel(label);		
	}	
	
	
	/**
	 * Creates copy of conic in construction of conic
	 * @param conic conic to be copied
	 */
	public GeoConic(GeoConic conic) {
		this(conic.cons);
		set(conic);
	}
	
	public GeoConic(Construction c, String label, GeoList coeffList) {
		this(c);
		
		int numberCount = 0;
		for (int i = 0; i<coeffList.size(); ++i) {
			if (coeffList.get(i).isGeoNumeric())
				++numberCount;
		}
		
		if (numberCount == 6) {
			this.setCoeffs(((GeoNumeric)coeffList.get(0)).getDouble(),
					((GeoNumeric)coeffList.get(1)).getDouble(),
					((GeoNumeric)coeffList.get(2)).getDouble(),
					((GeoNumeric)coeffList.get(3)).getDouble(),
					((GeoNumeric)coeffList.get(4)).getDouble(),
					((GeoNumeric)coeffList.get(5)).getDouble());
		} else {
			this.setUndefined();
		}
		
		setLabel(label);
	}



	public String getClassName() {
		return "GeoConic";
	}
	
    public int getGeoClassType() {
    	return GEO_CLASS_CONIC;
    }
	


	public GeoElement copy() {
		return new GeoConic(this);
	}
	

	
	public boolean isTraceable() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}
	
	
	
	
	/**
	 * makes this conic a circle with midpoint M through Point P
	 */
	final public void setCircle(GeoPoint M, GeoPoint P) {
		defined = M.isDefined() && P.isDefined() && !P.isInfinite();
		if (!defined) {			
			return;
		}

		if (M.isInfinite()) {
			// midpoint at infinity -> parallelLines
			// one through P, the other through infinite point M 
			/*
			b.x = P.inhomX;
			b.y = P.inhomY;
			*/
			double[] coords = new double[3];
			P.getCoords(coords);
			setMidpoint(coords);
			// M is normalvector of double line            
			eigenvecX = -M.y;
			eigenvecY = M.x;
			setEigenvectors();
			halfAxes[0] = Double.POSITIVE_INFINITY;
			halfAxes[1] = Double.POSITIVE_INFINITY;
			mu[0] = 0.0; // line at infinity is not drawn
			parallelLines(mu);
			// set line at infinity 0 = 1
			lines[1].x = Double.NaN;
			lines[1].y = Double.NaN;
			lines[1].z = Double.NaN;
			// set degenerate matrix
			matrix[0] = 0.0d;
			matrix[1] = 0.0d;
			matrix[2] = lines[0].z;
			matrix[3] = 0.0d;
			matrix[4] = lines[0].x / 2.0;
			matrix[5] = lines[0].y / 2.0;
		} else {
			setCircleMatrix(M, M.distance(P));
		}
		setAffineTransform();
	}
	
	/**
	 * Return angle of rotation from x-axis to the major axis of ellipse
	 * @return angle between x-axis and major axis of ellipse
	 */
	double getPhi()
	{
		if(matrix[3] == 0)
			if(matrix[0] < matrix[1])
				return 0.0;
			else
				return 0.5*Math.PI;
		else
			if(matrix[0] <= matrix[1])
				return 0.25*Math.PI - 0.5*Math.atan((matrix[0] - matrix[1])/(2*matrix[3]));
			else
				return 0.75*Math.PI - 0.5*Math.atan((matrix[0] - matrix[1])/(2*matrix[3]));
	}
    
	/**
	 * Return n points on conic 
	 * @param n number of points 
	 * @return Array list of points
	 */
	public ArrayList<GeoPoint> getPointsOnConic(int n)
	{
		GeoCurveCartesian curve = new GeoCurveCartesian(cons);
		this.toGeoCurveCartesian(curve);
		
		double startInterval = -Math.PI, endInterval = Math.PI;
		
		if(this.type == CONIC_HYPERBOLA)
		{
			startInterval = -Math.PI/2;
			endInterval = Math.PI/2;
		}
		if(this.type == CONIC_PARABOLA)
		{
			startInterval = -1;
			endInterval = 1;
		}
		
		return curve.getPointsOnCurve(n, startInterval, endInterval);
	}
	
	/**
	 * Invert circle or line in circle
	 * @version 2010-01-21
	 * @author Michael Borcherds 
	 * @param c Circle used as mirror
	 */	
	    final public void mirror(GeoConic c) {
	    	if (c.isCircle() && this.isCircle() )
	    	{ // Mirror point in circle
	    		double r1 =  c.getHalfAxes()[0];
	    		GeoVec2D midpoint1=c.getTranslationVector();
	    		double x1=midpoint1.x;
	    		double y1=midpoint1.y;
	    		
	    		double r2 =  getHalfAxes()[0];
	    		GeoVec2D midpoint2=getTranslationVector();
	    		double x2=midpoint2.x;
	    		double y2=midpoint2.y;
	    		
	    		// distance between centres
	    		double p = Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	    		
	    		// does circle being inverted pass through center of the other?
	    		if (Kernel.isEqual(p, r2)) {	    			
	    			double dx=x2-x1;
	    			double dy=y2-y1;
	    			//(x3,y3) is reflection of reflection of (x1+2dx,x1+2dy) an thus lies on the line
	    			double k=r1*r1/2/r2/r2;
	    			double x3=x1+k*dx;
	    			double y3=y1+k*dy;	    				    				    			
	    				    			
	    			matrix[4] = dx*0.5;
	    			matrix[5] = dy*0.5;
	    			matrix[2] = -dx*x3-dy*y3;
	    			matrix[0] = 0;
	    			matrix[1] = 0;
	    			matrix[3] = 0;
	    			//we update the eigenvectors etc.
	    			this.setMatrix(matrix);
	    			//classification yields CONIC_DOUBLE_LINE, we want a single line
	    			type = GeoConic.CONIC_LINE;
	    			return;
	    		}
	    		
	    		double x = r1*r1 / (p - r2);
	    		double y = r1*r1 / (p + r2);
	    		
	    		// radius of new circle
	    		double r3 = Math.abs(y - x) / 2.0;
	    		double centerX = x1 + (x2-x1) * (Math.min(x,y)+r3) / p; 
	    		double centerY = y1 + (y2-y1) * (Math.min(x,y)+r3) / p; 
	    		
	    		//double sf=r1*r1/((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
	            //setCoords( x1+sf*(x2-x1), y1+sf*(y2-y1) ,1.0);
	    		GeoPoint temp = new GeoPoint(cons,null,centerX,centerY,1.0);
	    		setCircleMatrix(temp, r3);
	    		temp.removeOrSetUndefinedIfHasFixedDescendent();
	    	}
	    	else if (c.isCircle() && (this.getType() == GeoConic.CONIC_LINE || this.getType() == GeoConic.CONIC_PARALLEL_LINES))
	    	{ // Mirror point in circle
	    		
	    			
	    			if (c.getType()==GeoConic.CONIC_CIRCLE)
	    	    	{ // Mirror point in circle
	    	    		double r =  c.getHalfAxes()[0];
	    	    		GeoVec2D midpoint=c.getTranslationVector();
	    	    		double a=midpoint.x;
	    	    		double b=midpoint.y;
	    	    		double lx = (getLines()[0]).x;
	    	    		double ly = (getLines()[0]).y;
	    	    		double lz = (getLines()[0]).z;
	    	    		double perpY,perpX;
	    	    		
	    	    		if(lx == 0){
	    	    			perpX = a;
	    	    			perpY = -lz/ly;
	    	    		}else{
	    	    			perpY = -(lx*ly*a-lx*lx*b+ly*lz)/(lx*lx+ly*ly);
	    	    			perpX = (-lz-ly*perpY)/lx;
	    	    		
	    	    		}
	    				double dist2 = ((perpX-a)*(perpX-a)+(perpY-b)*(perpY-b));
	    	    		//if line goes through center, we keep it
	    	    		if(!Kernel.isZero(dist2)){
	    	    		double sf=r*r/dist2;
	    	            //GeoPoint p =new GeoPoint(cons,null,a+sf*(perpX-a), b+sf*(perpY-b) ,1.0);
	    	            GeoPoint m =new GeoPoint(cons);
	    	            m.setCoords(a+sf*(perpX-a)/2, b+sf*(perpY-b)/2 ,1.0);
	    	            setSphereND(m,sf/2*Math.sqrt(((perpX-a)*(perpX-a)+(perpY-b)*(perpY-b))));
	    	    		}else type = GeoConic.CONIC_LINE;
	    	    	}
	    	    	else
	    	    	{
	    	    		setUndefined();
	    	    	}		
	    		}
	    		
	    		
	    		
	    	
			setAffineTransform();
			//updateDegenerates(); // for degenerate conics
	    }
	    
	/**
	 * mirror this conic at point Q
	 */
	final public void mirror(GeoPoint Q) {
		double qx = Q.inhomX;
		double qy = Q.inhomY;

		matrix[2] =
			4.0
				* (qy * qy * matrix[1]
					+ qx * (qx * matrix[0] + 2.0 * qy * matrix[3] + matrix[4])
					+ qy * matrix[5])
				+ matrix[2];
		matrix[4] = -2.0 * (qx * matrix[0] + qy * matrix[3]) - matrix[4];
		matrix[5] = -2.0 * (qx * matrix[3] + qy * matrix[1]) - matrix[5];

		// change eigenvectors' orientation
		eigenvec[0].mult(-1.0);
		eigenvec[1].mult(-1.0);
		
		// mirror translation vector b
		b.mirror(Q);	
		setMidpoint(new double[] {b.x,b.y});
		

		setAffineTransform();
		updateDegenerates(); // for degenerate conics
	}

	/**
	 * mirror this point at line g 
	 */
	final public void mirror(GeoLine g) {
		// Y = S(phi).(X - Q) + Q
		// where Q is a point on g, S(phi) is the mirror transform
		// and phi/2 is the line's slope angle

		// get arbitrary point of line
		double qx, qy;
		if (Math.abs(g.x) > Math.abs(g.y)) {
			qx = -g.z / g.x;
			qy = 0.0d;
		} else {
			qx = 0.0d;
			qy = -g.z / g.y;
		}

		// translate -Q
		doTranslate(-qx, -qy);
		// do mirror transform
		mirror(2.0 * Math.atan2(-g.x, g.y));
		// translate +Q
		doTranslate(qx, qy);
	
		setAffineTransform();
		updateDegenerates(); // for degenerate conics		
	}

	/**
	* mirror transform with angle phi
	* [ cos    sin     0 ]
	* [ sin    -cos    0 ]
	* [ 0      0       1 ]
	*/
	final private void mirror(double phi) {
		// set rotated matrix
		double sum = matrix[0] + matrix[1];
		double diff = matrix[0] - matrix[1];
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);
		// cos(2 phi) = cos(phi)\u00b2 - sin(phi)\u00b2 = (cos + sin)*(cos - sin)
		double cos2 = (cos + sin) * (cos - sin);
		// cos(2 phi) = 2 cos sin
		double sin2 = 2.0 * cos * sin;

		double temp = diff * cos2 + 2.0 * matrix[3] * sin2;
		double A0 = (sum + temp) / 2.0;
		double A1 = (sum - temp) / 2.0;
		double A3 = -matrix[3] * cos2 + diff * cos * sin;
		double A4 = matrix[4] * cos + matrix[5] * sin;
		matrix[5] = -matrix[5] * cos + matrix[4] * sin;
		matrix[0] = A0;
		matrix[1] = A1;
		matrix[3] = A3;
		matrix[4] = A4;

		// avoid classification: make changes by hand
		eigenvec[0].mirror(phi);
		eigenvec[1].mirror(phi);
					
		b.mirror(phi);	
		setMidpoint(new double[] {b.x,b.y});
	}



    ////////////////////////////////////////
    // FOR DRAWING IN 3D
    ////////////////////////////////////////

    public Coords getEigenvec3D(int i){
    	Coords ret = new Coords(4);
    	ret.set(getEigenvec(i));
    	return ret;
    }

    public boolean hasDrawable3D() {
    	return true;
    }


	public Coords getLabelPosition(){
		return new Coords(0, 0, 0, 1);
	}
	

	 public Coords getDirection3D(int i){
		 return new Coords(lines[i].y, -lines[i].x,0,0);
		 
	 }

	 public Coords getOrigin3D(int i){

		 return new Coords(startPoints[i].x, startPoints[i].y, 0, 1);

	 }


	 final public boolean isCasEvaluableObject() {
		 return true;
	 }
	 @Override
	 protected char getLabelDelimiter(){
		 return ':';
	 }

	 
	 private CoordSys coordSys = CoordSys.Identity3D();
	 
	 public CoordSys getCoordSys(){
		 return coordSys;
	 }


	 
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		// TODO Auto-generated method stub
		
		double[][] b = MyMath.adjoint(a00,a01,a02,a10,a11,a12,a20,a21,a22);
		/* 
		 *               ( A[0]  A[3]    A[4] )
		 *      matrix = ( A[3]  A[1]    A[5] )
		 *               ( A[4]  A[5]    A[2] )
		 *      P=matrix*B
		 */
		double p00 = matrix[0]*b[0][0]+matrix[3]*b[0][1]+matrix[4]*b[0][2];
		double p01 = matrix[0]*b[1][0]+matrix[3]*b[1][1]+matrix[4]*b[1][2];
		double p02 = matrix[0]*b[2][0]+matrix[3]*b[2][1]+matrix[4]*b[2][2];
		double p10 = matrix[3]*b[0][0]+matrix[1]*b[0][1]+matrix[5]*b[0][2];
		double p11 = matrix[3]*b[1][0]+matrix[1]*b[1][1]+matrix[5]*b[1][2];
		double p12 = matrix[3]*b[2][0]+matrix[1]*b[2][1]+matrix[5]*b[2][2];
		double p20 = matrix[4]*b[0][0]+matrix[5]*b[0][1]+matrix[2]*b[0][2];
		double p21 = matrix[4]*b[1][0]+matrix[5]*b[1][1]+matrix[2]*b[1][2];
		double p22 = matrix[4]*b[2][0]+matrix[5]*b[2][1]+matrix[2]*b[2][2];
		
		matrix[0] = b[0][0]*p00+b[0][1]*p10+b[0][2]*p20;
		matrix[3] = b[0][0]*p01+b[0][1]*p11+b[0][2]*p21;
		matrix[4] = b[0][0]*p02+b[0][1]*p12+b[0][2]*p22;
		matrix[1] = b[1][0]*p01+b[1][1]*p11+b[1][2]*p21;
		matrix[5] = b[1][0]*p02+b[1][1]*p12+b[1][2]*p22;
		matrix[2] = b[2][0]*p02+b[2][1]*p12+b[2][2]*p22;
	this.classifyConic(false);
	
	
	}
	
	public boolean isFillable() {
		return type != GeoConic.CONIC_LINE;
	}

	/* 
	 *               ( A[0]  A[3]    A[4] )
	 *      matrix = ( A[3]  A[1]    A[5] )
	 *               ( A[4]  A[5]    A[2] )
	 */

	public void setCoeffs(ExpressionValue[][] coeff) {
		Application.debug(coeff.length);
		if(coeff.length == 1){
			matrix[2] = evalCoeff(coeff[0][0]);
			if(coeff[0].length > 1)
				matrix[5] = evalCoeff(coeff[0][1])/2;
			if(coeff[0].length > 2)
				matrix[1] = evalCoeff(coeff[0][2]);
			return;
		}
		// TODO Auto-generated method stub
		if(coeff.length > 2)
			matrix[0] = evalCoeff(coeff[2][0]);
		else
			matrix[0] = 0;
		if(coeff[0].length > 2)
			matrix[1] = evalCoeff(coeff[0][2]);
		else
			matrix[1] = 0;
		
		matrix[2] = evalCoeff(coeff[0][0]);
		
		matrix[3] = evalCoeff(coeff[1][1])/2;
		
		matrix[4] = evalCoeff(coeff[1][0])/2;
		
		matrix[5] = evalCoeff(coeff[0][1])/2;
		
		classifyConic(false);
		if(coeff.length == 2 && coeff[0].length ==2 && 
				Kernel.isZero(evalCoeff(coeff[1][1]))){
			type = CONIC_LINE;
			Application.debug(matrix[4]+","+matrix[5]+","+matrix[2]);
		}
		Application.debug(this);
	}
	
	private double evalCoeff(ExpressionValue ev){
		if(ev!=null){
			return ((NumberValue)ev.evaluate()).getDouble();
		}
		return 0;
	}


}
