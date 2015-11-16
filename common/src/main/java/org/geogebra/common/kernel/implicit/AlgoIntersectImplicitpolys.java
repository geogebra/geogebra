/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoIntersectImplictpolys.java
 *
 * Created on 04.08.2010, 23:12
 */

package org.geogebra.common.kernel.implicit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationSolverInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoSimpleRootsPolynomial;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.util.Cloner;


/**
 *	Algorithm to intersect two implicit polynomial equations<br />
 *	output: GeoPoints if finitely many intersection points.
 */
public class AlgoIntersectImplicitpolys extends AlgoSimpleRootsPolynomial {
	
	private GeoImplicit p1;
	private GeoImplicit p2;
	
	private GeoConic c1;
	private List<double[]> valPairs;
	
	private static final int PolyX=0;
	private static final int PolyY=1;
	
	private int univarType;
	
	private List<GeoPoint> hints;

	/**
	 * To compute intersection of polynomial and conic
	 * @param c construction
	 * @param p1 polynomial
	 * @param c1 conic
	 */
	public AlgoIntersectImplicitpolys(Construction c, GeoImplicit p1,
			GeoConic c1) {
		this(c,null,false,p1,c1);
	}
	/**
	 * To compute intersection of polynomial and conic
	 * @param c construction
	 * @param labels labels for results
	 * @param setLabels true to set labels
	 * @param p1 polynomial
	 * @param c1 conic
	 */
	public AlgoIntersectImplicitpolys(Construction c, String[] labels, boolean setLabels,
 GeoImplicit p1, GeoConic c1) {
		super(c, p1.toGeoElement(), c1);
		this.p1=p1;
		this.c1=c1;
        initForNearToRelationship();
        compute();               
	}

	/**
	 * To compute intersection of two polynomials
	 * @param c construction
	 * @param p1 first polynomial
	 * @param p2 second polynomial
	 */
	public AlgoIntersectImplicitpolys(Construction c, GeoImplicit p1,
			GeoImplicit p2) {
		this(c,null,false,p1,p2);
	}


	/**
	 * To compute intersection of two polynomials
	 * @param c construction
	 * @param labels labels for results
	 * @param setLabels true to set labels
	 * @param p1 first polynomial
	 * @param p2 second polynomial
	 */
	public AlgoIntersectImplicitpolys(Construction c, String[] labels,
			boolean setLabels, GeoImplicit p1, GeoImplicit p2) {
		super(c, p1.toGeoElement(), p2.toGeoElement());
		this.p1=p1;
		this.p2=p2;
        initForNearToRelationship();
        compute();
	}
	
//	protected boolean rootPolishing(double[] pair){
//		double x=pair[0],y=pair[1];
//		double p,q;
//		p=p1.evalPolyAt(x, y);
//		q=p2.evalPolyAt(x, y);
//		double lastErr=Double.MAX_VALUE;
//		double err=Math.abs(p)+Math.abs(q);
//		while(err<lastErr&&err>Kernel.STANDARD_PRECISION){
//			double px,py;
//			double qx,qy;
//			px=p1.evalDiffXPolyAt(x, y);
//			py=p1.evalDiffYPolyAt(x, y);
//			qx=p2.evalDiffXPolyAt(x, y);
//			qy=p2.evalDiffYPolyAt(x, y);
//			double det=px*qy-py*qx;
//			if (AbstractKernel.isZero(det)){
//				break;
//			}
//			x-=(p*qy-q*py)/det;
//			y-=(q*px-p*qx)/det;
//			lastErr=err;
//			p=p1.evalPolyAt(x, y);
//			q=p2.evalPolyAt(x, y);
//			err=Math.abs(p)+Math.abs(q);
//		}
//		pair[0]=x;
//		pair[1]=y;
//		return err<Kernel.STANDARD_PRECISION;
//	}

	@Override
	protected double getYValue(double t) {
		//will not be used
		return 0;
	}
	
	@Override
	public void compute() {
		if (c1!=null){
			p2=new GeoImplicitPoly(c1);
		}
		
		if (valPairs==null){
			valPairs=new LinkedList<double[]>();
		}else{
			valPairs.clear();
		}
		
		/*
		 * New approach: calculating determinant of Sylvester-matrix to get resolvent
		 * 
		 */
		
//		Application.debug("p1="+p1);
//		Application.debug("p2="+p2);
		
		GeoImplicit a = p1, b = p2;
		if (p1.getDegX()<p2.getDegX()){
			a=p2;
			b=p1;
		}
		
		int m=a.getDegX();
		int n=b.getDegX();
		if (n < 0 || m < 0) {
			points.adjustOutputSize(0);
			return;
		}
		if (n == 0) {
			computeY(a, b);
			return;
		}

		//calculate the reduced Sylvester matrix. Complexity will be O(mnpq + m^2nq^2 + n^3pq)
		//where p=a.getDegY(), q=b.getDegY() 
		//we should minimize m^2 n q^2 by choosing to use polyX or polyY univarType.
		
//		int q = a.getDegY();
		PolynomialFunction[][] mat=new PolynomialFunction[n][n];
		PolynomialFunction[] aNew = new PolynomialFunction[m+n];
		PolynomialFunction[] bPolys = new PolynomialFunction[n+1];
		
		for (int i=0; i<=n; ++i)
			bPolys[i] = new PolynomialFunction(b.getCoeff()[i]);
		for (int i=0; i<n-1; ++i)
			aNew[i] = new PolynomialFunction(new double[]{0});
		for (int i=n-1; i<n+m; ++i)
			aNew[i] = new PolynomialFunction(a.getCoeff()[i-n+1]);
		
		int leadIndex = n+m-1;
		//Note: leadIndex of (n+1+t)-th row is equal to X-degree of b, + t. Use
		//this row to help eliminate aNew[leadIndex].
		while (leadIndex>= 2*n) {
			if ( !(aNew[leadIndex].degree() == 0  && aNew[leadIndex].getCoefficients()[0] == 0) ) {
				for (int j = n-1; j<leadIndex-n; ++j)
					aNew[j] = aNew[j].multiply(bPolys[n]);
				for (int j = leadIndex-n; j<leadIndex; ++j)
					aNew[j] = aNew[j].multiply(bPolys[n]).subtract(
							bPolys[j-leadIndex+n].multiply(aNew[leadIndex]));
			}
			--leadIndex;
		}
		while (leadIndex>= n) {
			if ( !(aNew[leadIndex].degree() == 0  && aNew[leadIndex].getCoefficients()[0] == 0) ) {
				for (int j = leadIndex-n; j<leadIndex; ++j)
					aNew[j] = aNew[j].multiply(bPolys[n]).subtract(
							bPolys[j-leadIndex+n].multiply(aNew[leadIndex]));
			}
			
			for (int j=0; j<n; ++j)
				mat[2*n-1-leadIndex][j] = new PolynomialFunction(aNew[leadIndex-n+j].getCoefficients());
			
			--leadIndex;
		}
		

		
		//avoid too large coefficients
		//test case: Intersect[-5 x^4+ x^2+ y^2 = 0, -20 x^3+2 x^2+2 x+2 y^2+4 y = 0]
		//without reducing coefficients, we get three intersection points: 
		// (0.00000185192649, -0.000000925965389), (0.475635148394481, 0.172245588226639), (2.338809137914722, -12.005665890026151)
		//after reducing coefficients, we have one more: the tangent point (0.99999997592913, 1.999999891681086)
		
		for (int i=0; i<n; ++i) {
			
				double largestCoeff = 0;
				double reduceFactor = 1;
				for (int j=0; j<n; ++j) {
					for (int k=0; k<mat[i][j].getCoefficients().length; ++k) {
						largestCoeff = Math.max(Math.abs(mat[i][j].getCoefficients()[k]), largestCoeff);
					}
				}
				while (largestCoeff >  10) {
					reduceFactor *= 0.1;
					largestCoeff *= 0.1;
				}
				
				if (reduceFactor!=1) {
					for (int j=0; j<n; ++j) {
						mat[i][j] = mat[i][j].multiply(new PolynomialFunction(new double[] {reduceFactor}));
					}
				}
		}
	
		
		
		
		//Calculate Sylvester matrix by definition. Complexity will be O((m+n)^3 * pq)
		//where p=a.getDegY(), q=b.getDegY() 
		/*
		PolynomialFunction[][] mat=new PolynomialFunction[m+n][m+n];
		for (int i = 0; i<n; ++i) {
			for (int j = 0; j<i; ++j)
				mat[i][j] = new PolynomialFunction(new double[]{0});
			for (int j = i; j<= i+m; ++j)
				mat[i][j] = new PolynomialFunction(a.getCoeff()[j-i]);
			for (int j = i+m+1; j<n+m; ++j)
				mat[i][j] = new PolynomialFunction(new double[]{0});
		}
		for (int i = n; i<m+n; ++i) {
			for (int j = 0; j<i-n; ++j)
				mat[i][j] = new PolynomialFunction(new double[]{0});
			for (int j = i-n; j<= i; ++j)
				mat[i][j] = new PolynomialFunction(b.getCoeff()[j-i+n]);
			for (int j = i+1; j<n+m; ++j)
				mat[i][j] = new PolynomialFunction(new double[]{0});
		}
		
		*/
		
		//old code
		/*PolynomialFunction[][] mat=new PolynomialFunction[n][n];
		for (int i=0;i<n;i++){
			for (int j=0;j<n;j++){
				mat[i][j]=new PolynomialFunction(new double[]{0});
				for (int k=Math.max(0, i-j);k<=Math.min(i, m+i-j);k++){
					PolynomialFunction p=new PolynomialFunction(b.getCoeff()[k]);
					mat[i][j]=mat[i][j].add(p.multiply(new PolynomialFunction(a.getCoeff()[m+i-k-j])));
				}
				for (int k=Math.max(0, i+m-j-n);k<=Math.min(i, m+i-j);k++){
					PolynomialFunction p=new PolynomialFunction(a.getCoeff()[k]);
					mat[i][j]=mat[i][j].subtract(p.multiply(new PolynomialFunction(b.getCoeff()[m+i-k-j])));
				}
			}
		}*/
		
//		Application.debug(Arrays.deepToString(mat));
		
		//Gauss-Bareiss for calculating the determinant
		
		PolynomialFunction c=new PolynomialFunction(new double[]{1});
		PolynomialFunction det=null;
		for (int k=0;k<n-1;k++){
			int r=0;
			double glc=0; //greatest leading coefficient
			for (int i=k;i<n;i++){
				double lc=PolynomialUtils.getLeadingCoeff(mat[i][k]);
				if (!Kernel.isZero(lc)){
					if (Math.abs(lc)>Math.abs(glc)){
						glc=lc;
						r=i;
					}
				}
			}
			if (Kernel.isZero(glc)){
				det=new PolynomialFunction(new double[]{0});
				break;
			}else if (r>k){
				for (int j=k;j<n;j++){
					//exchange functions
					PolynomialFunction temp=mat[r][j];
					mat[r][j]=mat[k][j];
					mat[k][j]=temp;
				}
			}
			for (int i=k+1;i<n;i++){
				for (int j=k+1;j<n;j++){
					PolynomialFunction t1=mat[i][j].multiply(mat[k][k]);
					PolynomialFunction t2=mat[i][k].multiply(mat[k][j]);
					PolynomialFunction t=t1.subtract(t2);
					mat[i][j]=PolynomialUtils.polynomialDivision(t, c);
				}
			}
			c=mat[k][k];
		}
		if (det==null)
			det=mat[n-1][n-1];
//		Application.debug("resultante = "+det);
		
		univarType=PolyY;
		double roots[]=det.getCoefficients();
//		roots[0]-=0.001;
		int nrRealRoots=0;
		if (roots.length>1)
			nrRealRoots=getNearRoots(roots,eqnSolver,1E-1);//getRoots(roots,eqnSolver);
		
		double[][] coeff;
		double[] newCoeff;
		if (univarType==PolyX){
			if (p1.getDegY()<p2.getDegY()){
				coeff=p1.getCoeff();
				newCoeff=new double[p1.getDegY()+1];
			}else{
				coeff=p2.getCoeff();
				newCoeff=new double[p2.getDegY()+1];
			}
			
		}else{
			if (p1.getDegX()<p2.getDegX()){
				coeff=p1.getCoeff();
				newCoeff=new double[p1.getDegX()+1];
			}else{
				coeff=p2.getCoeff();
				newCoeff=new double[p2.getDegX()+1];
			}
		}
		
		for (int k=0;k<nrRealRoots;k++){
			double t=roots[k];
			if (univarType==PolyX){
				for (int j=0;j<newCoeff.length;j++){
					newCoeff[j]=0;
				}
				for (int i=coeff.length-1;i>=0;i--){
					for (int j=0;j<coeff[i].length;j++){
						newCoeff[j]=newCoeff[j]*t+coeff[i][j];
					}
					for (int j=coeff[i].length;j<newCoeff.length;j++){
						newCoeff[j]=newCoeff[j]*t;
					}
				}
			}else{
				for (int i=0;i<coeff.length;i++){
					newCoeff[i]=0;
					for (int j=coeff[i].length-1;j>=0;j--){
						newCoeff[i]=newCoeff[i]*t+coeff[i][j];
					}
				}
			}
			int nr=getNearRoots(newCoeff,eqnSolver,1E-1);//getRoots(newCoeff,eqnSolver);
			for (int i=0;i<nr;i++){
				double[] pair=new double[2];
				if (univarType==PolyX){
					pair[0]=t;
					pair[1]=newCoeff[i];
				}else{
					pair[0]=newCoeff[i];
					pair[1]=t;
				}
			
				if (PolynomialUtils.rootPolishing(pair,p1,p2))
					insert(pair);				
			}
		}
		if (hints!=null){
			for (int i=0;i<hints.size();i++){
				double[] pair=new double[2];
				GeoPoint g=hints.get(i);
				if (g.isDefined()&&!Kernel.isZero(g.getZ())){
					pair[0]=g.getX()/g.getZ();
					pair[1]=g.getY()/g.getZ();
				}
			}
		}
		
		setPoints(valPairs);

	}

	/**
	 * Computation for the special case when one polynomial is constant in x
	 * 
	 * @param a
	 *            arbitrary implicit polynomial
	 * @param b
	 *            implicit polynomial with y terms only
	 */
	private void computeY(GeoImplicit a, GeoImplicit b) {
		if (a.getDegX() == 0) {
			valPairs.add(new double[] { Double.NaN, Double.NaN });
			setPoints(valPairs);
			return;

		}
		double roots[] = Cloner.clone(b.getCoeff()[0]);
		// roots[0]-=0.001;
		int nrRealRoots = 0;
		if (roots.length > 1)
			nrRealRoots = getNearRoots(roots, eqnSolver, 1E-5);
		for (int i = 0; i < nrRealRoots; i++) {
			PolynomialFunction tx = new PolynomialFunction(new double[] {
					roots[i], 0 });
			PolynomialFunction ty = new PolynomialFunction(
					new double[] { 0, 1 });
			double[] res = AlgoIntersectImplicitpolyPolyLine.lineIntersect(
					a.getCoeff(), tx, ty).getCoefficients();
			int xRoots = getNearRoots(res, eqnSolver, 1E-5);
			for (int j = 0; j < xRoots; j++) {
				valPairs.add(new double[] { res[j], roots[i] });
			}
		}
		setPoints(valPairs);
		
	}
	private static int getNearRoots(double[] roots,EquationSolverInterface solver, double epsilon){
		PolynomialFunction poly=new PolynomialFunction(roots);
		double[] rootsDerivative=poly.polynomialDerivative().getCoefficients();
		
		int nrRoots=getRoots(roots, solver);
		int nrDeRoots=getRoots(rootsDerivative,solver);
		for (int i=0;i<nrDeRoots;i++){
			if (Kernel.isEqual(poly.value(rootsDerivative[i]),0,epsilon)){
				if (nrRoots<roots.length){
					roots[nrRoots++]=rootsDerivative[i];
				}
			}
		}
		if (nrRoots==0){
			//a wild guess, test if the root of the n-1 derivative is a root of the original poly as well
			//works in case of a polynomial with one root of really high multiplicity.
			double[] c=poly.getCoefficients();
			int n=c.length-1;
			if (n > 0) {
			double x=-c[n-1]/n/c[n];
				if (Kernel.isEqual(poly.value(x), 0)) {
					roots[0] = x;
					return 1;
				}
			}
		}
		if (nrRoots==0){
			PolynomialFunction derivative=poly.polynomialDerivative();
			double x=0;
			double err=Math.abs(poly.value(x));
			double lastErr=err*2;
			while(err<lastErr&&err>Kernel.STANDARD_PRECISION){
				double devVal=derivative.value(x);
				if (!Kernel.isZero(devVal))
					x=x-poly.value(x)/devVal;
				else
					break;
				lastErr=err;
				err=Math.abs(poly.value(x));
			}
			if (Kernel.isEqual(poly.value(x),0,epsilon)){
				roots[0]=x;
				return 1;
			}
		}
		Arrays.sort(roots,0,nrRoots);
		return nrRoots;
	}
	
//	public static int getNearRoots2(double[] roots,EquationSolver solver,double epsilon){
//		int degree=PolynomialUtils.getDegree(roots);
//		double lc=roots[degree];
//		int status=(((degree&1)==1)^(lc>0)?0:5); //
//		
//		double[] minusEps=roots.clone();
//		double[] plusEps=roots.clone();
//		plusEps[0]+=epsilon;
//		minusEps[0]-=epsilon;
//		int nrMRoots=getRoots(minusEps,solver);
//		int nrPRoots=getRoots(plusEps,solver);
//		int nrRoots=getRoots(roots,solver);
//		
////		if (nrMRoots>1){
////			Arrays.sort(minusEps, 0, nrMRoots);
////		}
////		if (nrRoots>1){
////			Arrays.sort(minusEps, 0, nrRoots);
////		}
////		if (nrPRoots>1){
////			Arrays.sort(plusEps, 0, nrPRoots);
////		}
//		
//		// we use here, that a polynomial of degree n has n+1 coefficients but at most n roots.
//		minusEps[nrMRoots]=Double.POSITIVE_INFINITY;
//		plusEps[nrPRoots]=Double.POSITIVE_INFINITY;
//		roots[nrRoots]=Double.POSITIVE_INFINITY;
//		
//		int mI=0;
//		int pI=0;
//		int i=0;
//		int nrNearRoots=0;
//		while(mI<nrMRoots||pI<nrPRoots||i<nrRoots){
//			if (status==0){
//				if (minusEps[mI]<roots[i]&&minusEps[mI]<plusEps[pI]){
//					mI++;
//					status=1;
//				}else{
//					Application.debug(String.format("problem in status %d, plusEps=%f,roots=%f,minEps=%f", status,minusEps[mI],roots[i],plusEps[pI]));
//					return nrRoots;
//				}
//			}else if (status==1){
//				if (minusEps[mI]<plusEps[pI]||roots[i]<plusEps[pI]){
//					if (minusEps[mI]<roots[i]){
//						//nearRoot
//						roots[nrRoots+1+nrNearRoots]=(minusEps[mI]-minusEps[mI-1])/2; //assume "near Root" is in the middle
//						nrNearRoots++;
//						mI++;
//						status=0;
//					}else{
//						//real Root
//						i++;
//						status=3;
//					}
//				}else{
//					Application.debug(String.format("problem in status %d, plusEps=%f,roots=%f,minEps=%f", status,minusEps[mI],roots[i],plusEps[pI]));
//					return nrRoots;
//				}
//			}else if (status==2){
//				if (minusEps[mI]<plusEps[pI]||roots[i]<plusEps[pI]){
//					if (minusEps[mI]<roots[i]){
//						mI++;
//						status=0;
//					}else{
//						//real Root
//						i++;
//						status=3;
//					}
//				}
//				else{
//					Application.debug(String.format("problem in status %d, plusEps=%f,roots=%f,minEps=%f", status,minusEps[mI],roots[i],plusEps[pI]));
//					return nrRoots;
//				}
//			}else if (status==3){
//				if (plusEps[pI]<minusEps[mI]||roots[i]<minusEps[mI]){
//					if (plusEps[pI]<roots[i]){
//						pI++;
//						status=5;
//					}else{
//						//real Root
//						i++;
//						status=2;
//					}
//				}
//				else{
//					Application.debug(String.format("problem in status %d, plusEps=%f,roots=%f,minEps=%f", status,minusEps[mI],roots[i],plusEps[pI]));
//					return nrRoots;
//				}
//			}else if (status==4){
//				if (plusEps[pI]<minusEps[mI]||roots[i]<minusEps[mI]){
//					if (plusEps[pI]<roots[i]){
//						//nearRoot
//						roots[nrRoots+nrNearRoots]=(plusEps[pI]-plusEps[pI-1])/2; //assume "near Root" is in the middle
//						nrNearRoots++;
//						pI++;
//						status=5;
//					}else{
//						//real Root
//						i++;
//						status=2;
//					}
//				}else{
//					Application.debug(String.format("problem in status %d, plusEps=%f,roots=%f,minEps=%f", status,minusEps[mI],roots[i],plusEps[pI]));
//					return nrRoots;
//				}
//			}else if (status==5){
//				if (plusEps[pI]<roots[i]&&plusEps[pI]<minusEps[mI]){
//					pI++;
//					status=4;
//				}else{
//					Application.debug(String.format("problem in status %d, plusEps=%f,roots=%f,minEps=%f", status,minusEps[mI],roots[i],plusEps[pI]));
//					return nrRoots;
//				}
//			}
//		}
//		Arrays.sort(roots,0,nrRoots+nrNearRoots+1);
//		return nrRoots+nrNearRoots;
//	}

	private void insert(double[] pair) {
		ListIterator<double[]> it=valPairs.listIterator();
		double eps=1E-3; //find good value...
		while(it.hasNext()){
			double[] p=it.next();
			if (Kernel.isGreater(p[0],pair[0],eps)){
				it.previous();
				break;
			}
			if (Kernel.isEqual(p[0],pair[0],eps)){
				if (Kernel.isGreater(p[1],pair[1],eps)){
					it.previous();
					break;
				}
				if (Kernel.isEqual(p[1], pair[1],eps))
					return; //do not add
			}
		}
		it.add(pair);
	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}
	
	@Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }
    
	/**
	 * adds a point which will always be tested if it's a solution
	 * @param point point to be always tested
	 */
	public void addSolutionHint(GeoPoint point){
		if (hints==null){
			hints=new ArrayList<GeoPoint>();
		}
		hints.add(point);
	}

}
