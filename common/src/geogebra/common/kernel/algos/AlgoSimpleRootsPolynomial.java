/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoSimpleRootsPolynomial.java
 *
 * Created on 27.07.2010, 17:41
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.EquationSolverInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;

public abstract class AlgoSimpleRootsPolynomial extends AlgoIntersect {

	protected boolean setLabels;
    protected EquationSolverInterface eqnSolver;
    protected GeoElement[] geos;
    protected OutputHandler<GeoPoint> points;
	
	public AlgoSimpleRootsPolynomial(Construction c) {
		super(c);
		eqnSolver= cons.getKernel().getEquationSolver();
		points = new OutputHandler<GeoPoint>(new elementFactory<GeoPoint>() {
					public GeoPoint newElement() {
						GeoPoint p=new GeoPoint(cons);
						//p.setCoords(0, 0, 1);
						p.setUndefined();
						p.setParentAlgorithm(AlgoSimpleRootsPolynomial.this);
						return p;
					}
		});
	}
	
	public AlgoSimpleRootsPolynomial(Construction c,GeoElement... geos) {
		this(c);
		this.geos=new GeoElement[geos.length];
		for (int i=0;i<geos.length;i++){
			this.geos[i]=geos[i];
		}
		setInputOutput();
	}
	
	
	/**
	 * @param pf assigns a PolynomialFunction to this Algorithm which roots lead to one or more output Points
	 */
	public void setRootsPolynomial(PolynomialFunction pf){
		doCalc(pf);
	}
	
	public void setRootsPolynomialWithinRange(PolynomialFunction pf, double min, double max) {
		doCalc(pf, min , max );
	}

	@Override
	public	GeoPoint[] getIntersectionPoints() {
		return points.getOutput(new GeoPoint[0]);
	}

	@Override
	protected
	GeoPoint[] getLastDefinedIntersectionPoints() {
		return null;
	}

	@Override
	protected void setInputOutput() {
		input=geos;
		setDependencies();
	}
	
	/**
	 * @param roots array with the coefficients of the polynomial<br/>
	 * the roots of the polynomial are assigned to the first n elements of <b>roots</b>
	 * @param eqnSolver 
	 * @return number of distinct roots
	 */
	public static int getRoots(double[] roots,EquationSolverInterface eqnSolver){
		int nrRealRoots=eqnSolver.polynomialRoots(roots,false);
//		StringBuilder sb=new StringBuilder();
//		for (int i=0;i<nrRealRoots;i++){
//			if (i>0)
//				sb.append(',');
//			sb.append(roots[i]);
//		}
//		Application.debug("roots->"+sb);
		if (nrRealRoots>1){
			int c=0;
			Arrays.sort(roots,0,nrRealRoots);
			double last=roots[0];
			for (int i=1;i<nrRealRoots;i++){
				if (roots[i]-last<=Kernel.MIN_PRECISION){
					c++;
				}else{
					last=roots[i];
					if (c>0)
						roots[i-c]=roots[i];
				}
			}
			nrRealRoots-=c;
		}
		return nrRealRoots;
	}

	protected void doCalc(PolynomialFunction rootsPoly) {
		double roots[]=rootsPoly.getCoefficients();
		int nrRealRoots=0;
		if (roots.length>1)
			nrRealRoots=getRoots(roots,eqnSolver);
		makePoints(roots,nrRealRoots);
	}
	
	protected void doCalc(PolynomialFunction rootsPoly, double min, double max) {
		double roots[]=rootsPoly.getCoefficients();
		int nrRealRoots=0;
		if (roots.length>1)
			nrRealRoots=getRoots(roots,eqnSolver);
		
		for (int i=0; i<nrRealRoots; ++i) {
			if (Kernel.isGreater(roots[i], max, Kernel.STANDARD_PRECISION) || 
					Kernel.isGreater(min, roots[i], Kernel.STANDARD_PRECISION))
				roots[i] = Double.NaN;
		}
		makePoints(roots,nrRealRoots);
	}
	
	private static double distancePairSq(double[] p1,double[] p2){
		return (p1[0]-p2[0])*(p1[0]-p2[0])+(p1[1]-p2[1])*(p1[1]-p2[1]);
	}

	private void makePoints(double[] roots, int nrRealRoots) {
		List<double[]> valPairs=new ArrayList<double[]>();
		int len;
		for (int i=0;i<nrRealRoots;i++){
			len=getNrPoints(roots[i]);
			for (int j=0;j<len;j++){
				double[] pair=getXYPair(roots[i],j);
				for (int k=0;k<valPairs.size();k++){
					if (distancePairSq(pair, valPairs.get(k))<Kernel.STANDARD_PRECISION){
						pair=null;
						break;
					}
				}
				if (pair!=null)
					valPairs.add(pair);
			}
		}
		setPoints(valPairs);
	}
	
	 public void setLabels(String[] labels){
		 points.setLabels(labels);
		 update();
	 }
	 
	 protected void setPoints(List<double[]> valPairs){
		points.adjustOutputSize(valPairs.size());
		for (int i=0;i<valPairs.size();i++){
			points.getElement(i).setCoords(valPairs.get(i)[0], valPairs.get(i)[1], 1);
		}
			
		if (setLabels)
			points.updateLabels();
	 }
	 
	/**
	 * @param t root of PolynomialFunction
	 * @return number of corresponding outputPoints 
	 */
	protected int getNrPoints(double t){
		return 1;
	}
	
	/**
	 * @param t root of PolynomialFunction
	 * @param idx
	 * @return Y-value corresponding to t and idx.
	 */
	protected double getYValue(double t,int idx){
		return getYValue(t);
	}
	
	/**
	 * @param t root of PolynomialFunction
	 * @return the corresponding Y-value
	 */
	protected abstract double getYValue(double t);
	
	/**
	 * @param t root of PolynomialFunction
	 * @return the corresponding X-value
	 */
	protected double getXValue(double t){
		return t;
	}
	
	/**
	 * @param t root of PolynomialFunction
	 * @param idx
	 * @return X-value corresponding to t and idx.
	 */
	protected double getXValue(double t,int idx){
		return getXValue(t);
	}
	
	protected double[] getXYPair(double t,int idx){
		return new double[]{getXValue(t,idx),getYValue(t,idx)};
	}

	@Override
	public Commands getClassName() {
		return Commands.Roots;
	}
	
}
