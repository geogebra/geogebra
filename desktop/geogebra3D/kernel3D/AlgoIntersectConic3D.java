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

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoIntersectLineConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicND;




/**
 *
 * @author  mathieu
 * 
 */
public abstract class AlgoIntersectConic3D extends AlgoIntersect3D {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected GeoElement firstGeo; //input
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
     * @param c
     */
    AlgoIntersectConic3D(Construction cons, GeoElement firstGeo, GeoConicND c) {
        super(cons);
        
        
        this.firstGeo = firstGeo;
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
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = firstGeo;
        input[1] = c;
        
        setOutput(P);            
        noUndefinedPointsInAlgebraView();
        setDependencies(); // done by AlgoElement
    }    
    

    @Override
	protected final GeoPoint3D [] getIntersectionPoints() {
        return P;
    }
    
    @Override
	protected GeoPoint3D [] getLastDefinedIntersectionPoints() {
        return D;
    }
    
    
    
    /**
     * 
     * @return conic input
     */
    GeoConicND getConic() { return c; }
    
    
    @Override
	public final String toString(StringTemplate tpl) {
    	return loc.getPlain("IntersectionPointOfAB",c.getLabel(tpl),firstGeo.getLabel(tpl));
    }

    
    /**
     * 
     * @return start point for first geo
     */
    protected abstract Coords getFirstGeoStartInhomCoords();
    
    
    /**
     * 
     * @return direction for first geo
     */
    protected abstract Coords getFirstGeoDirectionInD3();
       
    /**
     * 
     * @param p point coords
     * @return true if coords are in the first geo as limited path
     */
    protected abstract boolean getFirstGeoRespectLimitedPath(Coords p);
    

	@Override
	public void compute() {
		
		CoordSys cs = c.getCoordSys();
		Coords o = getFirstGeoStartInhomCoords();
		Coords d = getFirstGeoDirectionInD3();
		
		//project line on conic coord sys
		Coords dp = cs.getNormalProjection(d)[1];
		if (!Kernel.isZero(dp.getZ())){	//line intersect conic coord sys
			Coords[] p = o.projectPlaneThruV(cs.getMatrixOrthonormal(), d);
			Coords p2d = new Coords(3);
			p2d.setX(p[1].getX());p2d.setY(p[1].getY());p2d.setZ(p[1].getW());
			// check if intersect point is on conic
			if (c.isOnFullConic(p2d, Kernel.MIN_PRECISION) 
					&& getFirstGeoRespectLimitedPath(p[0]))
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
				checkIsOnFirstGeo(P[0]);
				P[1].setCoords(cs.getPoint(points2d[1].x, points2d[1].y), false);
				checkIsOnFirstGeo(P[1]);
			}
		}
				
	}
	
	/**
	 * if p is really on first geo
	 * @param p point
	 */
	protected abstract void checkIsOnFirstGeo(GeoPoint3D p);
	
	
	
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
	
    @Override
	public final void initForNearToRelationship() {   
    	//TODO
    }
    
    /**
     * 
     * @return first geo
     */
    protected GeoElement getFirtGeo() { return firstGeo; }
    
}
