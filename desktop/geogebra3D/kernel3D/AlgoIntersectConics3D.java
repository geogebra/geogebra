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
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.CoordMatrixUtil;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoIntersectConics;
import geogebra.common.kernel.algos.AlgoIntersectLineConic;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;




/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoIntersectConics3D extends AlgoIntersect3D {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private GeoConicND A, B;  // input
    private GeoPoint3D [] P, D;     // output  
    
    /** 2d line description of intersection of the two coord sys when exists */
    private GeoLine l2d;
    /** 2d conic description of A and B when B included in A coord sys */
    private GeoConic A2d, B2d;
    /** 2d points created by using AlgoIntersectLineConic.intersectLineConic */
    private GeoPoint[] points2d;
    /** 2d intersect conics helper algo */
    private AlgoIntersectConics algo2d;
    
        
    /** matrix so that (x y 0 z) = AUGMENT_DIM * (x y z) */
    final static private CoordMatrix AUGMENT_DIM = new CoordMatrix(4, 3, 
    		new double[] {
    		1,0,0,0,
    		0,1,0,0,
    		0,0,0,1
    }
    );
    
    /** matrix so that (x y z) = REDUCE_DIM * (x y 0 z) */
    final static private CoordMatrix REDUCE_DIM = AUGMENT_DIM.transposeCopy();
    
    /**
     * 
     * @param cons
     * @param label
     * @param A 
     * @param B 
     */
    AlgoIntersectConics3D(Construction cons, String label, GeoConicND A, GeoConicND B) {
        this(cons, A,B);
        GeoElement.setLabels(label, P);            
    }
    
    /**
     * 
     * @param cons
     * @param labels
     * @param A 
     * @param B 
     */
    AlgoIntersectConics3D(Construction cons, String [] labels, GeoConicND A, GeoConicND B) {
        this(cons, A,B);
        GeoElement.setLabels(labels, P);            
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
     * 
     * @param cons construction
     */
    AlgoIntersectConics3D(Construction cons) {
        super(cons);
        
        
        //helper algo
        l2d = new GeoLine(cons);
        A2d = new GeoConic(cons);
        B2d = new GeoConic(cons);
        algo2d = new AlgoIntersectConics(cons);
        points2d = new GeoPoint[4];
    }
        

    /**
     * 
     * @param cons construction
     * @param A first conic
     * @param B second conic
     */
    AlgoIntersectConics3D(Construction cons, GeoConicND A, GeoConicND B) {
    	
    	this(cons);

    	P  = new GeoPoint3D[4];
    	D  = new GeoPoint3D[4];

        for (int i=0; i < 4; i++) {
            P[i] = new GeoPoint3D(cons); 
            D[i] = new GeoPoint3D(cons);    
            points2d[i] = new GeoPoint(cons);   
        }
        
        
        this.A = A;
        this.B = B;                

        
        setInputOutput(); // for AlgoElement
        

                
        compute();                      
    }   
    
    // for AlgoElement
    @Override
	public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = A;
        input[1] = B;
        
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
    
    @Override
	protected void setCoords(GeoPointND destination, GeoPointND source){
    	destination.setCoords(source.getCoordsInD(3),false);
    }
    

    /**
     * 
     * @return first conic input
     */
    GeoConicND getA() { return A; }
    
    
    /**
     * 
     * @return second conic input
     */
    GeoConicND getB() { return B; }

    
    @Override
	public final String toString(StringTemplate tpl) {
    	return loc.getPlain("IntersectionPointOfAB",A.getLabel(tpl),B.getLabel(tpl));
    }

    
    

	@Override
	public void compute() {
		intersectConics3D(A, B, P);
	}
		
	/**
	 * calc intersection points between A, B
	 * @param A first conic
	 * @param B second conic
	 * @param P intersection points
	 */
	public final void intersectConics3D(GeoConicND A, GeoConicND B, GeoPoint3D[] P){
		
		CoordSys csA = A.getCoordSys();
		CoordSys csB = B.getCoordSys();
		
		//check if coord sys are incident
		Coords cross = csA.getNormal().crossProduct(csB.getNormal());
		if (!cross.equalsForKernel(0,  Kernel.MIN_PRECISION)){	//line intersection
			Coords[] intersection = CoordMatrixUtil.intersectPlanes(A.getCoordSys().getMatrixOrthonormal(), B.getCoordSys().getMatrixOrthonormal());
			Coords op = csA.getNormalProjection(intersection[0])[1];
			Coords dp = csA.getNormalProjection(intersection[1])[1];		
			l2d.setCoords(dp.getY(), -dp.getX(), -dp.getY()*op.getX() +dp.getX()*op.getY());
			AlgoIntersectLineConic.intersectLineConic(l2d, A, points2d,Kernel.STANDARD_PRECISION);
			//Application.debug(points2d[0]+"\n"+points2d[1]);
			
			P[0].setCoords(csA.getPoint(points2d[0].x, points2d[0].y), false);
			checkIsOnConic(B, P[0]);
			P[1].setCoords(csA.getPoint(points2d[1].x, points2d[1].y), false);
			checkIsOnConic(B, P[1]);
			
			if (!P[0].isDefined() && P[1].isDefined()){
				P[0].setCoords(P[1].getCoords(), false);
				P[1].setUndefined();
			}
			
			P[2].setUndefined();P[3].setUndefined();
			/*
			Coords[] p = o.projectPlaneThruV(cs.getMatrixOrthonormal(), d);
			Coords p2d = new Coords(3);
			p2d.setX(p[1].getX());p2d.setY(p[1].getY());p2d.setZ(p[1].getW());
			// check if intersect point is on conic
			if (c.isOnFullConic(p2d, AbstractKernel.MIN_PRECISION))
				P[0].setCoords(p[0], false);
			else
				setPointsUndefined();
				*/
		}else{//line parallel to conic coord sys
			Coords op = csA.getNormalProjection(csB.getOrigin())[1];
			if (!Kernel.isZero(op.getZ())){//coord sys strictly parallel
				setPointsUndefined(P); //TODO infinite points ?
			}else{//coord sys included
				setPointsUndefined(P);
				
				CoordMatrix BtoA = 
					REDUCE_DIM.mul(
							csB.getMatrixOrthonormal().inverse().mul(csA.getMatrixOrthonormal())).mul(
									AUGMENT_DIM);
				//Application.debug(BtoA);
				
				CoordMatrix sB = B.getSymetricMatrix();
				CoordMatrix sBinA = BtoA.transposeCopy().mul(sB).mul(BtoA);
				//Application.debug("sym=\n"+sB+"\ninA\n"+sBinA);
				
				A2d.setMatrix(A.getMatrix());
				B2d.setMatrix(sBinA);
				algo2d.intersectConics(A2d, B2d, points2d);
				
				for(int i=0; i<4; i++)
					P[i].setCoords(csA.getPoint(points2d[i].x, points2d[i].y), false);
				
				
			}
		}
				
	}
	
	private static void checkIsOnConic(GeoConicND B, GeoPoint3D p){
		if (!p.isDefined())
			return;
		
		Coords pp = B.getCoordSys().getNormalProjection(p.getCoords())[1];
		Coords pp2d = new Coords(3);
		pp2d.setX(pp.getX());pp2d.setY(pp.getY());pp2d.setZ(pp.getW());
		if (!B.isOnFullConic(pp2d,Kernel.MIN_PRECISION))
			p.setUndefined();
			
	}
	
	
	
	private static void setPointsUndefined(GeoPoint3D[] P){
        for (int i=0; i < 4; i++) 
            P[i].setUndefined();                  
        
	}
	

    @Override
	public final void initForNearToRelationship() {   
    	//TODO
    }
}
