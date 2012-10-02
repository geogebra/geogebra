package geogebra.common.kernel.statistics;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.QRDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;

/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/** 
 *<pre>
 *<h3>RegressionMath:</h3>
 *
 * RegressionMath is a library of sums, determinants and parameter calculations
 * used by the FitXxx[GeoList]:GeoFunction commands.
 *  
 * Might be problems if callers are running in separate threads.
 * Is this a problem?
 * 
 * 
 * @author Hans-Petter Ulven
 * @version 20.02.10
 * Start  24.04.08
 * Update 15.11.08:
 * 		public det22(...),...,det44(...) for use in FitSin() and FitLogistic()
 * Update 27.01.09:
 *      doPolyN():boolean
 *      getPar():double[]
 *      to serve the extending of FitPoly[...] to degree>=5
 *      Matrix operations based on Jama.
 * Update 20.02.2010:
 * 		Changed from JaMa to Apache matrix library
 *      See doPolyN()  
 *      Got rid of r=corrcoff(), not used. Exists as separate command now.   
 *
 *<ul><b>--- Interface: ---</b>
 *<li>RegressionMath(GeoList)
 *<li>det33(...), det44(...)	determinants. (Faster than Gauss for n<5)
 *<li>doLinReg(),doQuadReg(),doCubicReg(),doQuartRet()
 *<li>doExpReg(),doLogReg(),doPowReg()
 *<li>getSigmaX(),getSigmaX2(),[getSigmaX3()..getSigmaX5()
 *<li>getSigmaY(),[getSigmaY2(),getSigmaXY(),getSigmaX2Y(),getSigmaX3Y(),getSigmaX4Y()]
 *<li>getP1(),getP2(),getP3(),getP4(),getP5()   //Parameters for regression function
 *<li>getR()    //regression coeffisient. (When users get regression, they will certainly ask for this...)
 *</ul>
 */

public final  class RegressionMath {

    //private final  boolean    DEBUG   =   false;       //24.04.08: false for release
    public final static int         LINEAR  =   1,
                                    QUAD    =   2,
                                    CUBIC   =   3,
                                    QUART   =   4,
                                    EXP     =   5,
                                    LOG     =   6,
                                    POW     =   7;
    
    

    /// --- Properties --- ///
    private   boolean error   =   false;
    //private   int     regtype =   LINEAR;                     //Default
    private   double  //r,                                      //Reg-coeff
                            p1,p2,p3,p4,p5,                         //Parameters
                            sigmax,sigmax2,sigmax3,sigmax4,         //Sums of x,x^2,...
                            sigmax5,sigmax6,sigmax7,sigmax8,
                            sigmay,sigmay2,
                            sigmaxy,sigmax2y,sigmax3y,sigmax4y;
    private   GeoList geolist;           
    private   double[] xlist;
    private   double[] ylist;
    private   int     size;

    //27.01.09:
    private	  	double[][]	marray,yarray;					//For (M_T*M)*Par=(M_T*Y)
    private		double[]	pararray;						//Parameter array
    
    /// --- Interface --- ///
    /** Constructor not needed */
    public RegressionMath() {  //private: Safety measure to avoid wrong use
    }//Constructor
    
    public  final double getP1()   {return p1; }
    public  final double getP2()   {return p2; }    
    public  final double getP3()   {return p3; }    
    public  final double getP4()   {return p4; }    
    public  final double getP5()   {return p5; }    
    //public  final double getR()    {return r;  }
    public  final double getSigmaX() {return sigmax;}
    public  final double getSigmaX2() {return sigmax2;}
    public  final double getSigmaY() {return sigmay;}
    public  final double getSigmaY2() {return sigmay2;}
    public  final double getSigmaXy() {return sigmaxy;}   
    
    //27.01.09:
    /** Returns array with calculated parameters */
    public final double[] getPar(){return pararray;}
    
    /** Does the Polynom regression for degree > 4*/
    public final boolean doPolyN(GeoList gl,int degree) {
    	error=false;
    	geolist=gl;
        size=geolist.size();
        getPoints();        //getPoints from geolist
        if(error) {return false;}       	
    	try{
    		/* Old Jama version:
    		long time=System.currentTimeMillis();
    		makeMatrixArrays(degree);			//make marray and yarray
    		Matrix M=new Matrix(marray);
    		Matrix Y=new Matrix(yarray);
    		Matrix Par=M.solve(Y);				//Par.print(3,3);
    		pararray=Par.getRowPackedCopy();
    		System.out.println(System.currentTimeMillis()-time);
    		*/
    		makeMatrixArrays(degree);			//make marray and yarray
    	    RealMatrix M=new Array2DRowRealMatrix(marray,false);
    	    DecompositionSolver solver= new QRDecompositionImpl(M).getSolver();
    	    //time=System.currentTimeMillis();
   	        RealMatrix Y=new Array2DRowRealMatrix(yarray,false);
   	        RealMatrix P=solver.solve(Y);

   	        pararray=P.getColumn(0);
   	        
   	        //System.out.println(System.currentTimeMillis()-time);
    		//diff(pararray,par);
		} catch (Throwable t) {
			App.debug(t.toString());
			error=true;    		
    	}//try-catch.  ToDo: A bit more fine-grained error-handling...
    	return !error;
    }//doPolyN()
    
    public  final boolean doLinear(GeoList gl) {
        error=false;
        geolist=gl;
        size=geolist.size();
        getPoints();        //getPoints from geolist
        if(error) {return false;}   
        doSums(LINEAR);     //calculate neccessary sigmas
        if(error) {return false;}
        
        double n=det22(1.0d*size,sigmax,sigmax,sigmax2);
        if(Math.abs(n-0.0d)<1.0E-15d) {
            return false;
        }
		p1=det22(sigmay,sigmax,sigmaxy,sigmax2)/n;
		p2=det22(size,sigmay,sigmax,sigmaxy)/n;
		//r=corrCoeff();
		return true;
    }//doLinearReg(GeoList)
    
    public  final boolean doQuad(GeoList gl) {
        error=false;
        geolist=gl;
        size=geolist.size();
        getPoints();        //getPoints from geolist
        if(error) {return false;}
        doSums(QUAD);     //calculate neccessary sigmas
        if(error) {return false;}
        
        double n=det33(1.0d*size,   sigmax, sigmax2, 
                       sigmax,     sigmax2, sigmax3,
                       sigmax2,     sigmax3,sigmax4);
        
        if(Math.abs(n-0.0d)<1.0E-15d) {
            return false;
        }
		p1=det33(
		    sigmay,       sigmax,   sigmax2,
		    sigmaxy,      sigmax2,  sigmax3,
		    sigmax2y,     sigmax3,  sigmax4
		)/n;
		p2=det33(
		    1.0d*size,       sigmay,     sigmax2,
		    sigmax,     sigmaxy,    sigmax3,
		    sigmax2,    sigmax2y,   sigmax4
		)/n;
		p3=det33(
		    1.0d*size,       sigmax,     sigmay,
		    sigmax,     sigmax2,    sigmaxy,
		    sigmax2,    sigmax3,    sigmax2y
		)/n;
		//r=0.0d; // Not useful
		return true;
    }//doQuad(Geolist)
    
    public  final boolean doCubic(GeoList gl) {
        error=false;
        geolist=gl;
        size=geolist.size();
        getPoints();        //getPoints from geolist
        if(error) {return false;}
        doSums(CUBIC);     //calculate neccessary sigmas
        if(error) {return false;}
        
        double n=det44(1.0d*size,   sigmax,     sigmax2,    sigmax3, 
                       sigmax,      sigmax2,    sigmax3,    sigmax4,
                       sigmax2,     sigmax3,    sigmax4,    sigmax5,
                       sigmax3,     sigmax4,    sigmax5,    sigmax6);
        
        if(Math.abs(n-0.0d)<1.0E-15d) {
            return false;
        }
		p1=det44(
		    sigmay,           sigmax,       sigmax2,      sigmax3,
		    sigmaxy,          sigmax2,      sigmax3,      sigmax4,
		    sigmax2y,         sigmax3,      sigmax4,      sigmax5,
		    sigmax3y,         sigmax4,      sigmax5,      sigmax6
		)/n;
		p2=det44(
		    size,           sigmay,       sigmax2,      sigmax3,
		    sigmax,           sigmaxy,      sigmax3,      sigmax4,
		    sigmax2,          sigmax2y,     sigmax4,      sigmax5,
		    sigmax3,          sigmax3y,     sigmax5,      sigmax6
		)/n;
		p3=det44(
		    size,           sigmax,       sigmay,       sigmax3,
		    sigmax,           sigmax2,      sigmaxy,      sigmax4,
		    sigmax2,          sigmax3,      sigmax2y,     sigmax5,
		    sigmax3,          sigmax4,      sigmax3y,     sigmax6
		)/n;
		p4=det44(
		    size,           sigmax,       sigmax2,      sigmay,
		    sigmax,           sigmax2,      sigmax3,      sigmaxy,
		    sigmax2,          sigmax3,      sigmax4,      sigmax2y,
		    sigmax3,          sigmax4,      sigmax5,      sigmax3y
		)/n;
		//r=0.0d; // Not useful
		return true;
    }//doCubic(Geolist)
    
    public  final boolean doQuart(GeoList gl) {
        error=false;
        geolist=gl;
        size=geolist.size();
        getPoints();        //getPoints from geolist
        if(error) {return false;}
        doSums(QUART);     //calculate neccessary sigmas
        if(error) {return false;}
        
        double n=det55(1.0d*size,   sigmax,     sigmax2,    sigmax3,    sigmax4,
                       sigmax,      sigmax2,    sigmax3,    sigmax4,    sigmax5,
                       sigmax2,     sigmax3,    sigmax4,    sigmax5,    sigmax6,
                       sigmax3,     sigmax4,    sigmax5,    sigmax6,    sigmax7,
                       sigmax4,     sigmax5,    sigmax6,    sigmax7,    sigmax8); 
        if(Math.abs(n-0.0d)<1.0E-15d) {
            return false;
        }
		p1=det55(
		           sigmay,       sigmax,     sigmax2,    sigmax3,    sigmax4,
		           sigmaxy,      sigmax2,    sigmax3,    sigmax4,    sigmax5,
		           sigmax2y,     sigmax3,    sigmax4,    sigmax5,    sigmax6,
		           sigmax3y,     sigmax4,    sigmax5,    sigmax6,    sigmax7,
		           sigmax4y,     sigmax5,    sigmax6,    sigmax7,    sigmax8
		)/n;
		p2=det55(
		           1.0d*size,   sigmay,      sigmax2,    sigmax3,    sigmax4,
		           sigmax,      sigmaxy,     sigmax3,    sigmax4,    sigmax5,
		           sigmax2,     sigmax2y,    sigmax4,    sigmax5,    sigmax6,
		           sigmax3,     sigmax3y,    sigmax5,    sigmax6,    sigmax7,
		           sigmax4,     sigmax4y,    sigmax6,    sigmax7,    sigmax8             
		)/n;
		p3=det55(
		           1.0d*size,   sigmax,     sigmay,      sigmax3,    sigmax4,
		           sigmax,      sigmax2,    sigmaxy,     sigmax4,    sigmax5,
		           sigmax2,     sigmax3,    sigmax2y,    sigmax5,    sigmax6,
		           sigmax3,     sigmax4,    sigmax3y,    sigmax6,    sigmax7,
		           sigmax4,     sigmax5,    sigmax4y,    sigmax7,    sigmax8
		)/n;
		p4=det55(
		           1.0d*size,   sigmax,     sigmax2,    sigmay,      sigmax4,
		           sigmax,      sigmax2,    sigmax3,    sigmaxy,     sigmax5,
		           sigmax2,     sigmax3,    sigmax4,    sigmax2y,    sigmax6,
		           sigmax3,     sigmax4,    sigmax5,    sigmax3y,    sigmax7,
		           sigmax4,     sigmax5,    sigmax6,    sigmax4y,    sigmax8
		)/n;
		p5=det55(
		           1.0d*size,   sigmax,     sigmax2,    sigmax3,    sigmay,
		           sigmax,      sigmax2,    sigmax3,    sigmax4,    sigmaxy,
		           sigmax2,     sigmax3,    sigmax4,    sigmax5,    sigmax2y,
		           sigmax3,     sigmax4,    sigmax5,    sigmax6,    sigmax3y,
		           sigmax4,     sigmax5,    sigmax6,    sigmax7,    sigmax4y 
		)/n;
		//r=0.0d; // Not useful
		return true;
    }//doQuart(Geolist)
    
    public  final boolean doExp(GeoList gl) {
        error=false;
        geolist=gl;
        double y=0.0d;
        size=geolist.size();
        getPoints();        //getPoints from geolist
        if(error) {return false;}
        //Transform y->ln(y):
        for(int i=0;i<size;i++) {
            y=ylist[i];
            if(y<0.0d) { //log(minus)!
                return false;
            }
			ylist[i]=Math.log(ylist[i]);
        }//for all y
        doSums(LINEAR);     //calculate neccessary sigmas
        if(error) {return false;}
        
        double n=det22(1.0d*size,sigmax,sigmax,sigmax2);
        if(Math.abs(n-0.0d)<1.0E-15d) {
            return false;
        }
		p1=det22(sigmay,sigmax,sigmaxy,sigmax2)/n;
		p2=det22(size,sigmay,sigmax,sigmaxy)/n;
		//transform back:
		p1=Math.exp(p1);
		//r=corrCoeff();
		return true;
    }//doExp(GeoList)    

    public  final boolean doLog(GeoList gl) {
        error=false;
        geolist=gl;
        double x=0.0d;
        size=geolist.size();
        getPoints();        //getPoints from geolist
        if(error) {return false;}
        //Transform x->ln(x):
        for(int i=0;i<size;i++) {
            x=xlist[i];
            if(x<0.0d) { //log(minus)!
                return false;
            }
			xlist[i]=Math.log(xlist[i]);
        }//for all x
        doSums(LINEAR);     //calculate neccessary sigmas
        if(error) {return false;}
        
        double n=det22(1.0d*size,sigmax,sigmax,sigmax2);
        if(Math.abs(n-0.0d)<1.0E-15d) {
            return false;
        }
		p1=det22(sigmay,sigmax,sigmaxy,sigmax2)/n;
		p2=det22(size,sigmay,sigmax,sigmaxy)/n;
		//No transformation of p1 or p2 neccessary
		//r=corrCoeff();
		return true;
    }//doLog(GeoList)    

    public  final boolean doPow(GeoList gl) {
        error=false;
        geolist=gl;
        double x,y;
        size=geolist.size();
        getPoints();        //getPoints from geolist
        if(error) {return false;}
        //Transform y->ln(y) and x->ln(x):
        for(int i=0;i<size;i++) {
            x=xlist[i];y=ylist[i];
            if( (y<0.0d)||(x<0.0d) ) { //log(minus)!
                return false;
            }
			xlist[i]=Math.log(xlist[i]);
			ylist[i]=Math.log(ylist[i]);
        }//for all x
        doSums(LINEAR);     //calculate neccessary sigmas
        if(error) {return false;}
        
        double n=det22(1.0d*size,sigmax,sigmax,sigmax2);
        if(Math.abs(n-0.0d)<1.0E-15d) {
            return false;
        }
		p1=det22(sigmay,sigmax,sigmaxy,sigmax2)/n;
		p2=det22(size,sigmay,sigmax,sigmaxy)/n;
		//Transform back:
		p1=Math.exp(p1);
		//r=corrCoeff();
		return true;
    }//doPow(GeoList)    
    
    
    public   final static double det22(	//15.11.08: public for FitSin and FitLogisticc
    			double a11,    double a12,     
                double a21,    double a22){
            return a11*a22-a21*a12;
    }//det22()
    
    public  final static double det33(    //15.11.08: public for FitSin and FitLogisticc
    		double a11,    double a12,     double a13, 
            double a21,    double a22,     double a23,
            double a31,    double a32,     double a33) {
        return a11*(a22*a33-a32*a23)-a12*(a21*a33-a31*a23)+a13*(a21*a32-a31*a22);
    }//det33()
    
    public  final static double det44(  //15.11.08: public for FitSin and FitLogisticc
        double a11,     double a12,     double a13,     double a14,
        double a21,     double a22,     double a23,     double a24,
        double a31,     double a32,     double a33,     double a34,
        double a41,     double a42,     double a43,     double a44
    ){
        return
        a11*a22*a33*a44 - a11*a22*a34*a43 - a11*a23*a32*a44 + a11*a23*a42*a34 +
        a11*a32*a24*a43 - a11*a24*a33*a42 - a12*a21*a33*a44 + a12*a21*a34*a43 +
        a12*a31*a23*a44 - a12*a31*a24*a43 - a12*a23*a41*a34 + a12*a41*a24*a33 +
        a21*a13*a32*a44 - a21*a13*a42*a34 - a21*a14*a32*a43 + a21*a14*a33*a42 -
        a13*a22*a31*a44 + a13*a22*a41*a34 + a13*a31*a24*a42 - a13*a32*a41*a24 +
        a22*a31*a14*a43 - a22*a14*a41*a33 - a31*a14*a23*a42 + a14*a23*a32*a41;
    }//det44()
    
    public  final static double det55(		//15.11.08: public for FitSin and FitLogisticc
        double a11,     double a12,     double a13,     double a14,     double a15,
        double a21,     double a22,     double a23,     double a24,     double a25,
        double a31,     double a32,     double a33,     double a34,     double a35,
        double a41,     double a42,     double a43,     double a44,     double a45,
        double a51,     double a52,     double a53,     double a54,     double a55
                                      ){
        return
            a11*a22*a33*a44*a55 - a11*a22*a33*a45*a54 - a11*a22*a34*a43*a55 + a11*a22*a34*a53*a45 + a11*a22*a43*a35*a54 - 
            a11*a22*a35*a44*a53 - a11*a23*a32*a44*a55 + a11*a23*a32*a45*a54 + a11*a23*a42*a34*a55 - a11*a23*a42*a35*a54 - 
            a11*a23*a34*a52*a45 + a11*a23*a52*a35*a44 + a11*a32*a24*a43*a55 - a11*a32*a24*a53*a45 - a11*a32*a25*a43*a54 +
            a11*a32*a25*a44*a53 - a11*a24*a33*a42*a55 + a11*a24*a33*a52*a45 + a11*a24*a42*a35*a53 - a11*a24*a43*a52*a35 + 
            a11*a33*a42*a25*a54 - a11*a33*a25*a52*a44 - a11*a42*a25*a34*a53 + a11*a25*a34*a43*a52 - a12*a21*a33*a44*a55 +
            a12*a21*a33*a45*a54 + a12*a21*a34*a43*a55 - a12*a21*a34*a53*a45 - a12*a21*a43*a35*a54 + a12*a21*a35*a44*a53 +
            a12*a31*a23*a44*a55 - a12*a31*a23*a45*a54 - a12*a31*a24*a43*a55 + a12*a31*a24*a53*a45 + a12*a31*a25*a43*a54 -   
            a12*a31*a25*a44*a53 - a12*a23*a41*a34*a55 + a12*a23*a41*a35*a54 + a12*a23*a51*a34*a45 - a12*a23*a51*a35*a44 +
            a12*a41*a24*a33*a55 - a12*a41*a24*a35*a53 - a12*a41*a33*a25*a54 + a12*a41*a25*a34*a53 - a12*a24*a33*a51*a45 +
            a12*a24*a51*a43*a35 + a12*a33*a51*a25*a44 - a12*a51*a25*a34*a43 + a21*a13*a32*a44*a55 - a21*a13*a32*a45*a54 -   
            a21*a13*a42*a34*a55 + a21*a13*a42*a35*a54 + a21*a13*a34*a52*a45 - a21*a13*a52*a35*a44 - a21*a14*a32*a43*a55 +
            a21*a14*a32*a53*a45 + a21*a14*a33*a42*a55 - a21*a14*a33*a52*a45 - a21*a14*a42*a35*a53 + a21*a14*a43*a52*a35 +
            a21*a32*a15*a43*a54 - a21*a32*a15*a44*a53 - a21*a15*a33*a42*a54 + a21*a15*a33*a52*a44 + a21*a15*a42*a34*a53 -   
            a21*a15*a34*a43*a52 - a13*a22*a31*a44*a55 + a13*a22*a31*a45*a54 + a13*a22*a41*a34*a55 - a13*a22*a41*a35*a54 -  
            a13*a22*a51*a34*a45 + a13*a22*a51*a35*a44 + a13*a31*a24*a42*a55 - a13*a31*a24*a52*a45 - a13*a31*a42*a25*a54 +
            a13*a31*a25*a52*a44 - a13*a32*a41*a24*a55 + a13*a32*a41*a25*a54 + a13*a32*a24*a51*a45 - a13*a32*a51*a25*a44 + 
            a13*a41*a24*a52*a35 - a13*a41*a25*a34*a52 - a13*a24*a42*a51*a35 + a13*a42*a51*a25*a34 + a22*a31*a14*a43*a55 -  
            a22*a31*a14*a53*a45 - a22*a31*a15*a43*a54 + a22*a31*a15*a44*a53 - a22*a14*a41*a33*a55 + a22*a14*a41*a35*a53 +
            a22*a14*a33*a51*a45 - a22*a14*a51*a43*a35 + a22*a41*a15*a33*a54 - a22*a41*a15*a34*a53 - a22*a15*a33*a51*a44 + 
            a22*a15*a51*a34*a43 - a31*a14*a23*a42*a55 + a31*a14*a23*a52*a45 + a31*a14*a42*a25*a53 - a31*a14*a25*a43*a52 +
            a31*a23*a15*a42*a54 - a31*a23*a15*a52*a44 - a31*a15*a24*a42*a53 + a31*a15*a24*a43*a52 + a14*a23*a32*a41*a55 - 
            a14*a23*a32*a51*a45 - a14*a23*a41*a52*a35 + a14*a23*a42*a51*a35 - a14*a32*a41*a25*a53 + a14*a32*a51*a25*a43 +
            a14*a41*a33*a25*a52 - a14*a33*a42*a51*a25 - a23*a32*a41*a15*a54 + a23*a32*a15*a51*a44 + a23*a41*a15*a34*a52 - 
            a23*a15*a42*a51*a34 + a32*a41*a15*a24*a53 - a32*a15*a24*a51*a43 - a41*a15*a24*a33*a52 + a15*a24*a33*a42*a51;
    }//det55

    /// --- Private --- ///

    /* Do whatever sums neccessary */
    private final  void doSums(int degree) {   //do whatever sums neccessary
        double x,y,xx,xy;
        sigmax=sigmax2=sigmax3=sigmax4=sigmax5=sigmax6=sigmax7=sigmax8=
        sigmaxy=sigmax2y=sigmax3y=sigmax4y=
        sigmay=sigmay2=0.0d;
        for(int i=0;i<size;i++){
            x=xlist[i]; y=ylist[i];    
            xx=x*x; xy=x*y; //save some calculations
            switch(degree){     //fall-through-switch
                case 4:     sigmax4y+=x*xx*xy;    sigmax7+=xx*xx*xx*x; sigmax8+=xx*xx*xx*xx;
                case 3:     sigmax3y+=xx*xy;      sigmax5+=xx*xx*x; sigmax6+=xx*xx*xx;
                case 2:     sigmax2y+=xx*y;       sigmax3+=xx*x;    sigmax4+=xx*xx;
                case 1:     sigmay2+=y*y;         //r only for linear
                default:    sigmax+=x;            sigmax2+=xx; 
                            sigmaxy+=xy;          sigmay+=y;  
            }//switch
        }//for
    }//doSums(degree)

    /* Get points to local array */
    private final  void getPoints(){
        //double x,y;
        double xy[]=new double[2];
        GeoElement geoelement;
        //GeoPoint    geopoint;
        xlist=new double[size];    ylist=new double[size];
        for(int i=0;i<size;i++){
            geoelement=geolist.get(i);
            if(geoelement.isGeoPoint()) {
                ((GeoPoint)geoelement).getInhomCoords(xy);
                xlist[i]=xy[0]; ylist[i]=xy[1];
            }else{
                error=true;
                xlist[i]=0.0d;  ylist[i]=0.0d;
            }//if
        }//for all points    
    }//getPoints()
    
    //Make M with 1,x,x^2,... , and Y with y1,y2,.. for all datapoints
    private final void makeMatrixArrays(int degree){
    	marray=new double[size][degree+1];
    	yarray=new double[size][1];					//column matrix
    	for(int i=0;i<size;i++){
    		//Y:
    		yarray[i][0]=ylist[i];
    		//M:
    		for(int j=0;j<(degree+1);j++){
    			marray[i][j]=Math.pow(xlist[i],j);
    		}//for j (all degrees =columns in marray)
    	}//for i (all datapoints = rows in marray, cols in yarray)
    	
    }//makeMatrixArrays()
    
    /// --- DEBUG --- ///        !!! Remember to comment out calls before distribution !!!

/* //---SNIP START---
    
    // Debug of 2D array
    public static void aprint(double[][] a){
        int cols=a.length;
        int rows=a[0].length;
        for(int r=0;r<rows;r++){
            for(int c=0;c<cols;c++){
                System.out.print(a[c][r]+" ");
            }//
            System.out.println();
        }//
    }//aprint()
    
    //debug of 1D array
    public static void aprint(double[] a){
        for(int i=0;i<a.length;i++){
            System.out.println(a[i]+"  ");
        }
    }//
    
    // Differences between Jama and Apache Solution
    // Outside GeoGebra the diffs are <E-14
    // In GeoGebra other rounding results in diffs <E-10
    // Jama is twice as fast if small datasets and degrees,
    // Apache about the same if things get big...
    public static void diff(double[] a, double[] b){
        int size=Math.min(a.length,b.length);
        double dif=0.0d;
        for(int i=0;i<size;i++){
            dif=a[i]-b[i];
            if(dif>1.0E-10){
            	//System.out.println("TOO BIG DIFFERENCE???");}
                System.out.println(Math.abs(dif));
            }//if too big diff
        }//for all data
    }//diff
    
*/ //--- SNIP END
}// class RegressionMath


