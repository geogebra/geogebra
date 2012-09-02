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
 * Created on 28.07.2010, 13:20
 */
package geogebra.common.kernel.implicit;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.advanced.AlgoRoots;
import geogebra.common.kernel.algos.AlgoSimpleRootsPolynomial;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;

/**
 * Algorithm to intersect Implicit polynomials with either lines or polynomials
 */
public class AlgoIntersectImplicitpolyParametric extends
		AlgoSimpleRootsPolynomial {
	
	private PolynomialFunction tx;
	private PolynomialFunction ty;
	private GeoImplicitPoly p;
	private GeoLine l;
	private GeoFunction f;
	private GeoPoint[] tangentPoints;

	/**
	 * To compute intersection of polynomial and line
	 * @param c construction
	 * @param p polynomial
	 * @param l line
	 */
	public AlgoIntersectImplicitpolyParametric(Construction c,GeoImplicitPoly p,GeoLine l) {
		this(c,null,false,p,l);
	}
	
	/**
	 * To compute intersection of polynomial and function
	 * @param c construction
	 * @param p polynomial
	 * @param f function
	 */
	public AlgoIntersectImplicitpolyParametric(Construction c,GeoImplicitPoly p,GeoFunction f) {
		this(c,null,false,p,f);
	}

	/**
	 * To compute intersection of polynomial and line
	 * @param c construction
	 * @param labels labels for output
	 * @param setLabels true to set labels
	 * @param p polynomial
	 * @param l line
	 */
	public AlgoIntersectImplicitpolyParametric(Construction c, String[] labels,
			boolean setLabels, GeoImplicitPoly p, GeoLine l) {
		super(c,p,l);
		this.p=p;
		this.l=l;
		compute();
	}

	/**
	 * To compute intersection of polynomial and function
	 * @param c construction
	 * @param labels labels for output
	 * @param setLabels true to set labels
	 * @param p polynomial
	 * @param f function
	 */
	public AlgoIntersectImplicitpolyParametric(Construction c, String[] labels,
			boolean setLabels, GeoImplicitPoly p, GeoFunction f) {
		super(c,p,f);
		this.p=p;
		this.f=f;
		compute();
	}

	@Override
	protected double getYValue(double t) {
		return ty.value(t);
	}
	

	@Override
	protected double getXValue(double t) {
		return tx.value(t);
	}

	@Override
	public void compute() {

		if (!p.isDefined()){
			return;
		}
		
		double maxT;
		double minT;
		if (f!=null){
			if (!f.isDefined()){
				return;
			}
			
			if (!f.isPolynomialFunction(false)) {
				
				
				Kernel ker = cons.getKernel();
				
				ker.setSilentMode(true);
				
				GeoFunction paramEquation = new GeoFunction(cons, p, null, f);
				
				AlgoRoots algo = new AlgoRoots(cons, paramEquation, 
						new GeoNumeric(cons, f.getMinParameter()),
						new GeoNumeric(cons, f.getMaxParameter()));
				
				GeoPoint[] rootPoints = algo.getRootPoints();
				List<double[]> valPairs=new ArrayList<double[]>();
				for (int i=0;i<rootPoints.length;i++){
					double t = rootPoints[i].getX();
					valPairs.add(new double[]{t,f.evaluate(t)});
				}
				
				ker.setSilentMode(false);
				setPoints(valPairs);
				return;
			}
			tx=new PolynomialFunction(new double[]{0,1}); //x=t
			ty=new PolynomialFunction(f.getFunction().getNumericPolynomialDerivative(0).getCoeffs()); //y=f(t)
			maxT = f.getMaxParameter();
			minT = f.getMinParameter();
		}else if (l!=null){
			if (!l.isDefined()){
				points.adjustOutputSize(0);
				return;
			}
			//get parametrisation of line
			double startP[]=new double[2];
			l.getInhomPointOnLine(startP);
			tx=new PolynomialFunction(new double[]{startP[0],l.getY()}); //x=p1+t*r1
			ty=new PolynomialFunction(new double[]{startP[1],-l.getX()}); //y=p2+t*r2
			maxT = l.getMaxParameter();
			minT = l.getMinParameter();
			
			if (l.getParentAlgorithm() instanceof AlgoTangentImplicitpoly) {
				tangentPoints = ((AlgoTangentImplicitpoly)l.getParentAlgorithm()).getTangentPoints();
			}
		}else{
			return;
		}
		PolynomialFunction sum=null;
		PolynomialFunction zs=null;
		//Insert x and y (univariat)polynomials via the Horner-scheme
		double[][] coeff=p.getCoeff();
		if (coeff!=null)
			for (int i=coeff.length-1;i>=0;i--){
				zs=new PolynomialFunction(new double[]{coeff[i][coeff[i].length-1]});
				for (int j=coeff[i].length-2;j>=0;j--){
					zs=zs.multiply(ty).add(new PolynomialFunction(new double[]{coeff[i][j]}));//y*zs+coeff[i][j];
				}
				if (sum==null)
					sum=zs;
				else
					sum=sum.multiply(tx).add(zs);//sum*x+zs;
			}
		
		setRootsPolynomialWithinRange(sum,minT,maxT);
		mergeWithTangentPoints();
	}
	
	private void mergeWithTangentPoints() {
		
		if (tangentPoints == null
				|| tangentPoints.length == 0)
			return;
		
		
		
		//assumption: tangent points are far apart from each other such that dist(tangent1,tangent2) > epsilon.
		boolean addTangent[] = new boolean[tangentPoints.length];
		int orgSize = points.size();
		while (!points.getElement(orgSize-1).isDefined())
			--orgSize;
		
		int newSize = orgSize;
		double EPS2 = Kernel.EPSILON;  //TODO: have a better guess of the error
		
		for (int i = 0; i<tangentPoints.length; ++i) {
			if (tangentPoints[i].getIncidenceList()!=null
					&& tangentPoints[i].getIncidenceList().contains(l)) {
				addTangent[i] = true;
				for (int j = 0; j<orgSize; ++j) {
					if (points.getElement(j).distanceSqr(tangentPoints[i])<EPS2) {
						if (addTangent[i]) {
							points.getElement(j).setUndefined();
							--newSize;
						} else {
							addTangent[i] = false;
							points.getElement(i).setCoords(tangentPoints[j]);
						}
						
					}
				}
				if (addTangent[i])
					++newSize;
				
			} else {
				addTangent[i] = false;
			}
		}
		
		
		int definedCount = 0;
		for (int i=0; i<orgSize; ++i) {
			if (points.getElement(i).isDefined()) {
				if (definedCount!=i)
					points.getElement(definedCount).setCoords(points.getElement(i));
				++definedCount;
			}
		}
		
		points.adjustOutputSize(newSize);
		
		for (int i=0; i<tangentPoints.length; ++i) {
			if (addTangent[i]) {
				points.getElement(definedCount++).setCoords(tangentPoints[i]);
			}
		}
		
		if (setLabels)
			points.updateLabels();
		
	}

	@Override
	public Algos getClassName() {
        return Algos.AlgoIntersectImplicitpolyParametric;
    }
	
	@Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }
    
	
	
}
