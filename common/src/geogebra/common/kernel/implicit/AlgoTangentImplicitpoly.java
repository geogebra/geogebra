/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.kernel.implicit;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoPointOnPath;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 *	Algorithm to calculate all tangents to the implicit polynomial equation
 *	either going threw a given point or parallel to given line.
 */
public class AlgoTangentImplicitpoly extends AlgoElement {
	
	private GeoImplicitPoly p;
	private GeoPoint R;
	private GeoLine g;
	
	private GeoPoint[] ip; //tangent points.
	private OutputHandler<GeoLine> tangents;
	
	private boolean pointOnPath;
	private AlgoIntersectImplicitpolys algoIntersect;
	
	private String[] labels;
	

	/**
	 * @param c construction
	 * @param labels labels for output
	 * @param p implicit polynomial
	 */
	protected AlgoTangentImplicitpoly(Construction c, String[] labels,GeoImplicitPoly p) {
		super(c);
		this.labels=labels;
		this.p=p;

	}
	 
	
	/**
	 * To compute tangents to poly through given point
	 * @param c construction
	 * @param labels labels for output
	 * @param p implicit polynomial
	 * @param R point on tangent
	 */
	public AlgoTangentImplicitpoly(Construction c,String[] labels,GeoImplicitPoly p,GeoPoint R) {
		this(c,labels,p);
		this.R=R;
		
		pointOnPath=false;
		
		if (R.getParentAlgorithm()!=null){
			if (R.getParentAlgorithm() instanceof AlgoPointOnPath){
				AlgoPointOnPath a=(AlgoPointOnPath)R.getParentAlgorithm();
				if (a.getPath()==p){
					pointOnPath=true; //AlgoPointOnPath (on this curve) 
				}
			}
		}
		
		if (!pointOnPath){
			AlgoImplicitPolyTangentCurve algoTangentPoly=
				new AlgoImplicitPolyTangentCurve(c, p, R, null,false,false);
			
			GeoImplicitPoly tangentCurve=algoTangentPoly.getTangentCurve();
			algoIntersect = new AlgoIntersectImplicitpolys(cons, p,tangentCurve);
			cons.removeFromConstructionList(algoIntersect);
			ip = algoIntersect.getIntersectionPoints();
		}

		setInputOutput();
	}
	
	/**
	 * To compute tangents to poly in given direction
	 * @param c construction
	 * @param labels labels for output
	 * @param p implicit polynomial
	 * @param g line
	 */
	public AlgoTangentImplicitpoly(Construction c,String[] labels,GeoImplicitPoly p,GeoLine g) {
		this(c,labels,p);
		this.g=g;
		setInputOutput();
	}
	
	
	
	
	@Override
	protected void setInputOutput() {
		input=new GeoElement[2];
		input[1]=p;
		if (g!=null)
			input[0]=g;
		else 
			input[0]=R;
		tangents=new OutputHandler<GeoLine>(new elementFactory<GeoLine>() {
			public GeoLine newElement() {
				GeoLine g1=new GeoLine(getConstruction());
				g1.setParentAlgorithm(AlgoTangentImplicitpoly.this);
				return g1;
			}
		});
		tangents.setLabels(labels);
		setDependencies();
	}
	
	
    
	@Override
	public void compute() {
		// idea: find intersection points between given curve and
		// tangent curve
		// and construct lines through (x_p, y_p) and intersection points, 
		// where (x_p, y_p) is given point.
		
        if (!R.isDefined()) {
        	tangents.adjustOutputSize(0);
        	return;
        }   
		
        
        tangents.adjustOutputSize(0);
		
		int n=0;
		if(p.isOnPath(R))
		{
			tangents.adjustOutputSize(n+1);
			double dfdx = this.p.evalDiffXPolyAt(R.inhomX, R.inhomY);
			double dfdy = this.p.evalDiffYPolyAt(R.inhomX, R.inhomY);
			if (!Kernel.isEqual(dfdx,0,1E-5)||!Kernel.isEqual(dfdy,0,1E-5)){
				tangents.getElement(n).setCoords(dfdx, dfdy, 
						-dfdx*R.inhomX - dfdy*R.inhomY);
				n++;
			}
		}
		
		if(pointOnPath){
			return;
		}
		
		ip = algoIntersect.getIntersectionPoints();
		for(int i=0; i<ip.length; i++)
		{
			
			if(Kernel.isEqual(ip[i].inhomX, R.inhomX, 1E-2) 
					&& Kernel.isEqual(ip[i].inhomY, R.inhomY, 1E-2))
				continue;
			
			//normal vector does not exist, therefore tangent is not defined
			//We need to check if F1 :=dF/dx and F2 :=dF/dy are both zero when eval at ip[i]
			//The error of F1 is dF1/dx * err(x) + dF1/dy * err(y), where err(x) and err(y) satisfies
			//| (dF/dx) err(x) + (dF/dy) err(y) | < EPSILON
			//So |dF/dx|<= |dF1/dx * err(x) + dF1/dy * err(y)| <= Max(dF1/dx / dF/dx, dF1/dy / dF/dy) * EPSILON
			//A convenient necessary condition of this is  (dF/dx)^2 <= |dF1/dx| * EPSILON.
			//Not very reasonably, now we use (dF/dx)^2 <= EPSILON only, to avoid evaluation of dF1/dx 
			//TODO: have a more reasonable choice
			if(Kernel.isEqual(0, this.p.evalDiffXPolyAt(ip[i].inhomX, ip[i].inhomY), Kernel.EPSILON_SQRT)
					&& Kernel.isEqual(0, this.p.evalDiffXPolyAt(ip[i].inhomX, ip[i].inhomY), Kernel.EPSILON_SQRT))
				continue;
			
			tangents.adjustOutputSize(n+1);
			tangents.getElement(n).setCoords(ip[i].getY() - this.R.getY(), this.R.getX() - ip[i].getX(), 
				ip[i].getX() * this.R.getY() - this.R.getX() * ip[i].getY());
			ip[i].addIncidence(tangents.getElement(n));
			n++;
		}
		
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoTangentImplicitpoly;
	}

	@Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_TANGENTS;
    }

	
	/**
	 * @return resulting tangents
	 */
	public GeoLine[] getTangents() {
		return tangents.getOutput(new GeoLine[tangents.size()]);
	}
	
	/**
	 * @param labels set labels of tangents
	 */
	public void setLabels(String[] labels) {
        tangents.setLabels(labels);

        update();
    }

	/**
	 * @return tangent points
	 */
	public GeoPoint[] getTangentPoints() {
		return ip;
	}

	// TODO Consider locusequability
}
