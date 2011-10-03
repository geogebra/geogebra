/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * PolynomialUtils.java
 *
 * Created on 17.08.2011, 13:05
 */

package geogebra.kernel.implicit;

import geogebra.kernel.Kernel;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;

public class PolynomialUtils {
	
	/**
	 * calculates the quotient of p/d (no calculation of the remainder is done)
	 * @param cp
	 * @param cd
	 * @return quotient of cp/cd
	 */
	public static double[] polynomialDivision(double[] cp,double[] cd){
		double[] cq;
		cp=cp.clone();
		int degD=cd.length-1;
		while(degD>=0&&Kernel.isZero(cd[degD])){
			degD--;
		}
		if (degD<0){ // => division by zero
			throw new ArithmeticException("divide by zero polynomial");
		}
		if (cp.length-1<degD){ 
			return new double[]{0};
		}else{
			cq=new double[cp.length-degD];
		}
		double lcd=cd[degD];
		int k=cp.length-1;
		for (int i=cq.length-1;i>=0;i--){
			cq[i]=cp[k]/lcd;
			for (int j=0;j<=degD-1;j++){
				cp[j+i]=cp[j+i]-cq[i]*cd[j];
			}
			k--;
		}
		return cq;
	}
	
	/**
	 * calculates the quotient of p/d (no calculation of the remainder is done)
	 * @param p
	 * @param d
	 * @return quotient of p/d
	 */
	public static PolynomialFunction polynomialDivision(PolynomialFunction p, PolynomialFunction d){
		return new PolynomialFunction(polynomialDivision(p.getCoefficients(),d.getCoefficients()));
	}
	
	public static int getDegree(PolynomialFunction p){
		return getDegree(p.getCoefficients());
	}
	
	public static int getDegree(double[] c){
		for (int i=c.length-1;i>=0;i--){
			if (!Kernel.isZero(c[i]))
				return i;
		}
		return -1;
	}
	
	public static double getLeadingCoeff(double[] c){
		int d=getDegree(c);
		if (d>=0)
			return c[d];
		else
			return 0;
	}
	
	public static double getLeadingCoeff(PolynomialFunction p){
		return getDegree(p.getCoefficients());
	}
	
	public static double eval(double[] c,double x){
		if (c.length==0)
			return 0;
		double s=c[c.length-1];
		for (int i=c.length-2;i>=0;i--){
			s*=x;
			s+=c[i];
		}
		return s;
	}
	
	public static double[][] coeffMinDeg(double[][] coeff){
		double[][] newCoeffMinDeg=null;
		for (int i=coeff.length-1;i>=0;i--){
			for (int j=coeff[i].length-1;j>=0;j--){
				if (!Kernel.isZero(coeff[i][j])){
					if (newCoeffMinDeg==null){
						newCoeffMinDeg=new double[i+1][];
					}
					if (newCoeffMinDeg[i]==null){
						newCoeffMinDeg[i]=new double[j+1];
					}
					newCoeffMinDeg[i][j]=coeff[i][j];
				}
			}
			if (newCoeffMinDeg!=null&&newCoeffMinDeg[i]==null){
				newCoeffMinDeg[i]=new double[1];
				newCoeffMinDeg[i][0]=0;
			}
		}
		if (newCoeffMinDeg==null){
			newCoeffMinDeg=new double[1][1];
			newCoeffMinDeg[0][0]=0;
		}
		return newCoeffMinDeg;
	}
	
	/**
	 * 
	 * @param pair starting value for Newton's-Algorithm
	 * @param p1 
	 * @param line defined by line[0]+x*line[1]+y*line[2]=0
	 * @return whether a common root of the polynomial and the line was found
	 */
	public static boolean rootPolishing(double[] pair,GeoImplicitPoly p1,double[] line){
		return rootPolishing(pair, p1,null,line);
	}
	
	public static boolean rootPolishing(double[] pair,GeoImplicitPoly p1,GeoImplicitPoly p2){
		return rootPolishing(pair, p1, p2,null);
	}
	
	private static boolean rootPolishing(double[] pair,GeoImplicitPoly p1,GeoImplicitPoly p2,double[] line){
		double x=pair[0],y=pair[1];
		double p,q;
		if (p1==null){
			return false;
		}
		if (p2==null&&(line==null||line.length!=3)){
			return false;
		}
		p=p1.evalPolyAt(x, y);
		if (p2!=null)
			q=p2.evalPolyAt(x, y);
		else
			q=line[0]+x*line[1]+y*line[2];
		double lastErr=Double.MAX_VALUE;
		double err=Math.abs(p)+Math.abs(q);
		while(err<lastErr&&err>Kernel.STANDARD_PRECISION){
			double px,py;
			double qx,qy;
			px=p1.evalDiffXPolyAt(x, y);
			py=p1.evalDiffYPolyAt(x, y);
			if (p2!=null){
				qx=p2.evalDiffXPolyAt(x, y);
				qy=p2.evalDiffYPolyAt(x, y);
			}else{
				qx=line[1];
				qy=line[2];
			}
			double det=px*qy-py*qx;
			if (Kernel.isZero(det)){
				break;
			}
			x-=(p*qy-q*py)/det;
			y-=(q*px-p*qx)/det;
			lastErr=err;
			p=p1.evalPolyAt(x, y);
			if (p2!=null){
				q=p2.evalPolyAt(x, y);
			}else{
				q=line[0]+x*line[1]+y*line[2];
			}
			err=Math.abs(p)+Math.abs(q);
		}
		pair[0]=x;
		pair[1]=y;
		return err<Kernel.STANDARD_PRECISION;
	}

}
