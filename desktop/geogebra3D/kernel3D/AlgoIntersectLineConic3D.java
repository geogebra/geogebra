/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoTangentLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra3D.kernel3D;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.kernel.AlgoIntersectAbstract;
import geogebra.kernel.AlgoIntersectLineConic;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.AlgoIntersectND;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoIntersectLineConic3D extends AlgoIntersect3D {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected GeoLineND g;  // input
	protected GeoConicND c;  // input
    protected GeoPoint3D [] P;     // output  
    private GeoPoint3D [] D; 
    
    /** 2d description of g when included in conic coord sys */
    private GeoLine g2d;
    /** 2d points created by using AlgoIntersectLineConic.intersectLineConic */
    private GeoPoint[] points2d;
        
    /**
     * 
     * @param cons
     * @param label
     * @param g
     * @param c
     */
    AlgoIntersectLineConic3D(Construction cons, String label, GeoLineND g, GeoConicND c) {
        this(cons, g,c);
        GeoElement.setLabels(label, P);            
    }
    
    /**
     * 
     * @param cons
     * @param labels
     * @param g
     * @param c
     */
    AlgoIntersectLineConic3D(Construction cons, String [] labels, GeoLineND g, GeoConicND c) {
        this(cons, g,c);
        GeoElement.setLabels(labels, P);            
    }
    
    public String getClassName() {
        return "AlgoIntersectLineConic";
    }

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }

    
    /**
     * 
     * @param cons
     * @param g
     * @param c
     */
    AlgoIntersectLineConic3D(Construction cons, GeoLineND g, GeoConicND c) {
        super(cons);
        this.g = g;
        this.c = c;                
        

        P  = new GeoPoint3D[2];
        D  = new GeoPoint3D[2];
        
        //helper algo
        g2d = new GeoLine(cons);
        points2d = new GeoPoint[2];
               
        for (int i=0; i < 2; i++) {
            P[i] = new GeoPoint3D(cons);                  
            D[i] = new GeoPoint3D(cons);    
            points2d[i] = new GeoPoint(cons);   
        }
        
        setInputOutput(); // for AlgoElement
        

                
        compute();                      
    }   
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement) g;
        input[1] = c;
        
        output = P;            
        noUndefinedPointsInAlgebraView();
        setDependencies(); // done by AlgoElement
    }    
    

    protected final GeoPoint3D [] getIntersectionPoints() {
        return P;
    }
    
    protected GeoPoint3D [] getLastDefinedIntersectionPoints() {
        return D;
    }
    
    
    /**
     * 
     * @return line input
     */
    GeoLineND getLine() { return g; }
    
    /**
     * 
     * @return conic input
     */
    GeoConicND getConic() { return c; }
    
    
    public final String toString() {
    	return app.getPlain("IntersectionPointOfAB",c.getLabel(),((GeoElement) g).getLabel());
    }

    
    

	protected void compute() {
		
		CoordSys cs = c.getCoordSys();
		Coords o = g.getStartInhomCoords();
		Coords d = g.getDirectionInD3();
		
		//project line on conic coord sys
		Coords dp = cs.getNormalProjection(d)[1];
		if (!Kernel.isZero(dp.getZ())){	//line intersect conic coord sys
			Coords[] p = o.projectPlaneThruV(cs.getMatrixOrthonormal(), d);
			Coords p2d = new Coords(3);
			p2d.setX(p[1].getX());p2d.setY(p[1].getY());p2d.setZ(p[1].getW());
			// check if intersect point is on conic
			if (c.isOnFullConic(p2d, Kernel.MIN_PRECISION) 
					&& g.respectLimitedPath(p[0], Kernel.EPSILON))
				P[0].setCoords(p[0], false);
			else
				setPointsUndefined();
		}else{//line parallel to conic coord sys
			Coords op = cs.getNormalProjection(o)[1];
			if (!Kernel.isZero(op.getZ())){//line not included
				setPointsUndefined(); //TODO infinite points ?
			}else{//line included
				g2d.setCoords(dp.getY(), -dp.getX(), -dp.getY()*op.getX() +dp.getX()*op.getY());
				AlgoIntersectLineConic.intersectLineConic(g2d, c, points2d);
				//Application.debug(points2d[0]+"\n"+points2d[1]);
				P[0].setCoords(cs.getPoint(points2d[0].x, points2d[0].y), false);
				checkIsOnLine(P[0]);
				P[1].setCoords(cs.getPoint(points2d[1].x, points2d[1].y), false);
				checkIsOnLine(P[1]);
			}
		}
				
	}
	
	private void checkIsOnLine(GeoPoint3D p){
		if (!p.isDefined())
			return;
		if (!g.respectLimitedPath(p.getCoords(),Kernel.MIN_PRECISION))
			p.setUndefined();
	}
	
	
	
	private void setPointsUndefined(){
        for (int i=0; i < 2; i++) 
            P[i].setUndefined();                  
        
	}
	
    /**
     * Returns the index in output[] of the intersection point
     * that is closest to the coordinates (xRW, yRW)
     * TODO: move to an interface
     */
    /*int getClosestPointIndex(double xRW, double yRW, CoordMatrix4x4 mat) {
        GeoPoint3D[] P = getIntersectionPoints();
        double x, y, lengthSqr, mindist = Double.POSITIVE_INFINITY;
        int minIndex = 0;
        for (int i = 0; i < P.length; i++) {
        	Coords toSceneInhomCoords = mat.mul(P[i].getCoords().getCoordsLast1()).getInhomCoords();
        	x = (toSceneInhomCoords.getX() - xRW);
            y = (toSceneInhomCoords.getY() - yRW);
            lengthSqr = x * x + y * y;
            if (lengthSqr < mindist) {
                mindist = lengthSqr;
                minIndex = i;
            }
        }

        return minIndex;
    }*/
	
    protected final void initForNearToRelationship() {   
    	//TODO
    }
}
