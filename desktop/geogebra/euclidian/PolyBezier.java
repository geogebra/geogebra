/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Polyline.java
 *
 * Created on 16. November 2001, 09:26
 */

package geogebra.euclidian;

import geogebra.common.euclidian.draw.DrawConic;

import java.awt.geom.GeneralPath;
import java.util.Iterator;
import java.util.List;

/**
 * adapted from polyline
 * @author  Michael Borcherds
 * @version 
 */

public class PolyBezier {   
    
    int n;
        
    private int max_capacity = DrawConic.MAX_PLOT_POINTS;
	double [] x, y; 	
    public GeneralPath gp = new GeneralPath();
    
    // Creates new Polyline for n vertices 
    public PolyBezier(int n) {
           setNumberOfPoints(n);  
    }
    
    public PolyBezier(List<geogebra.common.awt.GPoint> al) {
    	setNumberOfPoints(al.size());
    
    	int i=0;
    	Iterator<geogebra.common.awt.GPoint> it = al.iterator();
    	while (it.hasNext()) {
    		geogebra.common.awt.GPoint p = it.next();
    		x[i] = p.getX();
    		y[i] = p.getY();
    		i++;
    	}
    	
    	buildGeneralPath();
    }
    
    public void setPoints(int n, double [] x, double [] y) {
        this.n = n;
        this.x = x;
        this.y = y;
    }
    
    void setNumberOfPoints(int n) {  			
			this.n = n;
			if (n > max_capacity || x == null) {			
				max_capacity = n;
				x = new double[max_capacity]; 
				y = new double[max_capacity];		    							    
			} 		    			
    }
    
    /** builds a general path of a polyline from points (x[0], y[0]) 
     * to (x[n-1], y[n-1]) 
     */    
    final public void buildGeneralPath() {

	    double u[] = new double[n+1];
	    double X2[] = new double[n+1];
	    double Y2[] = new double[n+1];
	    double Xb[] = new double[4];
	    double Yb[] = new double[4];
	    for (int i=0 ; i<=n-1 ; i++) u[i]=i; // dummy x-coordinates
	    
	    double xp1=1e32d, xpn=1e32d; //"natural" end condition
	    double yp1=1e32d, ypn=1e32d; //"natural" end condition
//	    double xp1 = x[2]-x[1], xpn=x[n]-x[n-1];
//	    double yp1 = y[2]-y[1], ypn=y[n]-y[n-1];
	    
	    X2=spline(u, x, n-1, xp1, xpn); // x-coords
	    Y2=spline(u, y, n-1, yp1, ypn); // y-coords
	    
        gp.reset();                       
		gp.moveTo((float) x[0], (float) y[0]);	 // starting point
//		gp.lineTo((float) x[1], (float) y[1]);	 
//		gp.lineTo((float) x[2], (float) y[2]);	 
	    for(int i = 0; i < n-1; i++ )
	    {
	    	Xb=tobezier(x,X2,i);
	    	Yb=tobezier(y,Y2,i);
	    	if (i%2==10) gp.moveTo((float)Xb[3], (float)Yb[3]); else
			gp.curveTo((float)Xb[1],(float)Yb[1],
					(float)Xb[2],(float)Yb[2],
					(float)Xb[3],(float)Yb[3]);
	    	 
	    	
	    }

    }  
    public void addline(double x,double y) {
    	if ( Math.abs(x) < Float.MAX_VALUE &&
 				 Math.abs(y) < Float.MAX_VALUE)
    		gp.lineTo((float)x,(float)y);
    	
    	
    }
    
    public static double[] tobezier(double xa[], double x2a[], int k)
    {
  	  double U[] = new double[4];
  	  double x0 = xa[k];
  	  double x1 = xa[k+1];
  	  double s0=x2a[k];
  	  double s1=x2a[k+1];
  	  U[0] = x0;
  	  U[3] = x1;
  	  U[1] = (2d*x0 + x1 - (2d*s0 + s1)/6d)/3d;
  	  U[2] = (x0 + 2d*x1 - (s0 + 2d*s1)/6d)/3d;
  	  return U;
  }


    public static double[] spline(double x[], double y[], int n, double yp1, double ypn)
    {
    double y2[] = new double[n+1];
    double u[] = new double[n+1];
    double sig,p,un,qn;
    
    int i0,i1;
    i0=0;
    i1=i0+1;
    if (yp1>1e30d)
    {
    y2[i0]=0d;
    u[i0]=0d;
    }
    else
    {
    y2[i0]=-0.5d;
    u[i0]=(3d/(x[i1]-x[i0]))*((y[i1]-y[i0])/(x[i1]-x[i0])-yp1);
    }
    for (int i = i1 ; i<= n-1 ; i++)
    {
    sig = (x[i]-x[i-1])/(x[i+1]-x[i-1]);
    p = sig *y2[i-1]+2d;
    y2[i] =(sig -1d)/p;
    u[i] = (6d*((y[i+1]-y[i])/(x[i+1]-x[i])-(y[i]-y[i-1])/(x[i]-x[i-1]))/(x[i+1]-x[i-1])-sig*u[i-1])/p;
    }
    
    if (ypn>1e30d)
    {
    qn = 0d;
    un = 0d;
    }
    else
    {
    qn = 0.5d;
    un = (3d/(x[n]-x[n-1]))*(ypn-(y[n]-y[n-1])/(x[n]-x[n-1]));
    }
    
    y2[n]=(un-qn*u[n-1])/(qn*y2[n-1]+1d);
    
    
    for (int k = n-1 ; k>=i0 ; k--)
  	  {
  	    y2[k] = y2[k]*y2[k+1]+u[k];
  	  }
    
    return y2;
  }

    
    public static double[] findCoefs( double a[], int n )
    {
      int i;
      double b[] = new double[n+1];
      double r[] = new double[n+1];
      double c[] = new double[n+1];

      b[0] = 2 * ( a[1] - a[0] ) / 3.0;
      for( i = 1; i < n; i++ )
      {
    	  b[i] = a[i+1] - a[i-1];
      }
        b[n] = 2 * ( a[n] - a[n-1] ) / 3.0;

      r[n] = 1;
      r[n-1] = 1;
      for( i = n-2; i >= 0; i-- )
        r[i] = 4 * r[i+1] - r[i+2];
      for( i = 1; i<=n; i+=2 )
        r[i] = -r[i];

      c[0] = 0.0;
      for( i = n; i>=0; i-- )
        c[0] += r[i] * b[i];
      c[0] /= (r[0] + r[1]);
      c[1] = b[0] - c[0];
      for( i = 1; i < n; i++ )
        c[i+1] = b[i] - 4 * c[i] - c[i-1]; 
      return c;
    }

}
