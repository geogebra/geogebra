/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.kernel.implicit;

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;

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
	
	private AlgoIntersectImplicitpolys algoIntersect;
	
	private String[] labels;
	

	protected AlgoTangentImplicitpoly(Construction c) {
		super(c);
	}
	
	protected AlgoTangentImplicitpoly(Construction c, String[] labels,GeoImplicitPoly p) {
		this(c);
		this.labels=labels;
		this.p=p;

	}
	 
	
	public AlgoTangentImplicitpoly(Construction c,String[] labels,GeoImplicitPoly p,GeoPoint R) {
		this(c,labels,p);
		this.R=R;
		
		AlgoImplicitPolyTangentCurve algoTangentPoly=
			new AlgoImplicitPolyTangentCurve(c, p, R, null,false,false);
		
		GeoImplicitPoly tangentCurve=algoTangentPoly.getTangentCurve();
		algoIntersect = new AlgoIntersectImplicitpolys(cons, p,tangentCurve);
		cons.removeFromConstructionList(algoIntersect);
		ip = algoIntersect.getIntersectionPoints();
		
		setInputOutput();
	}
	
	public AlgoTangentImplicitpoly(Construction c,String[] labels,GeoImplicitPoly p,GeoLine g) {
		this(c,labels,p);
		this.g=g;
		setInputOutput();
	}
	
	public AlgoTangentImplicitpoly(Construction c,GeoImplicitPoly p,GeoPoint R) {
		this(c,null,p,R);
	}
	
	public AlgoTangentImplicitpoly(Construction c,GeoImplicitPoly p,GeoLine g) {
		this(c,null,p,g);
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
				GeoLine g=new GeoLine(getConstruction());
				g.setParentAlgorithm(AlgoTangentImplicitpoly.this);
				return g;
			}
		});
		tangents.setLabels(labels);
		setDependencies();
	}
	
	
    
	@Override
	protected void compute() {
		// idea: find intersection points between given curve and
		// tangent curve
		// and construct lines through (x_p, y_p) and intersection points, 
		// where (x_p, y_p) is given point.
		
        if (!R.isDefined()) {
        	tangents.adjustOutputSize(0);
        	return;
        }   
		
        ip = algoIntersect.getIntersectionPoints();
        
        tangents.adjustOutputSize(0);
		
		int n=0;
		if(p.isOnPath(R))
		{
			tangents.adjustOutputSize(n+1);
			double dfdx = this.p.evalDiffXPolyAt(R.inhomX, R.inhomY);
			double dfdy = this.p.evalDiffYPolyAt(R.inhomX, R.inhomY);
			tangents.getElement(n).setCoords(dfdx, dfdy, 
					-dfdx*R.inhomX - dfdy*R.inhomY);
			n++;
		}
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
	public String getClassName() {
		return "AlgoTangentImplicitpoly";
	}

	@Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_TANGENTS;
    }

	
	public GeoLine[] getTangents() {
		return tangents.getOutput(new GeoLine[tangents.size()]);
	}
	
	public void setLabels(String[] labels) {
        tangents.setLabels(labels);

        update();
    }

	public GeoPoint[] getTangentPoints() {
		return ip;
	}
}
