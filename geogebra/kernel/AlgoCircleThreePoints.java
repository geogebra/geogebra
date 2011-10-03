/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoCircleThreePoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoPointND;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoCircleThreePoints extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPointND A, B, C; // input    
    protected GeoConicND circle; // output     

    // line bisectors
    private GeoLine s0, s1;
    private GeoPoint center;    
    private double[] det = new double[3];
    transient private double ax,
        ay,
        bx,
        by,
        cx,
        cy,
        ABx,
        ABy,
        ACx,
        ACy,
        BCx,
        BCy,
        maxDet;
    transient private int casenr;

    protected AlgoCircleThreePoints(
        Construction cons,
        String label,
        GeoPointND A,
        GeoPointND B,
        GeoPointND C) {
        this(cons, A, B, C);
        circle.setLabel(label);
    }
    
    public AlgoCircleThreePoints(
            Construction cons,           
            GeoPointND A,
            GeoPointND B,
            GeoPointND C) {
    	
            super(cons);
            
            setPoints(A,B,C);
 
            createCircle();
            circle.addPointOnConic(getA()); //move into setIncidence();
            circle.addPointOnConic(getB());
            circle.addPointOnConic(getC());
            
            // temp: line bisectors
            s0 = new GeoLine(cons);
            s1 = new GeoLine(cons);

            center = new GeoPoint(cons);            

            setInputOutput(); // for AlgoElement

            compute();           
            setIncidence();
    }
    
    
    private void setIncidence() {
    	if (A instanceof GeoPoint)
    		((GeoPoint) A).addIncidence(circle);
    	if (B instanceof GeoPoint)
    		((GeoPoint) B).addIncidence(circle);
    	if (C instanceof GeoPoint)
    		((GeoPoint) C).addIncidence(circle);
		
	}

	/** set the three points of the circle to A, B, C
	 * @param A first point
	 * @param B second point
	 * @param C third point
     */
    protected void setPoints(GeoPointND A, GeoPointND B, GeoPointND C){
    	
        this.A = A;
        this.B = B;
        this.C = C;	
    }

    
    
    /**
     * create the object circle
     */
    protected void createCircle(){
    	
        circle = new GeoConic(cons);
    }
    
    
    
    
    public String getClassName() {
        return "AlgoCircleThreePoints";
    }

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_CIRCLE_THREE_POINTS;
    }
    
    // for AlgoElement
    protected void setInputOutput() {
    	setInput();
    	setOutput();
        setDependencies(); // done by AlgoElement
   	
    }
    
    protected void setInput() {
        input = new GeoElement[3];
        input[0] = (GeoElement) A;
        input[1] = (GeoElement) B;
        input[2] = (GeoElement) C;

    }
    
    protected void setOutput() {

        output = new GeoElement[1];
        output[0] = circle;
     }

    
    public GeoConicND getCircle() {
        return circle;
    }
    public GeoPoint getA() {
        return (GeoPoint) A;
    }
    public GeoPoint getB() {
        return (GeoPoint) B;
    }
    public GeoPoint getC() {
        return (GeoPoint) C;
    }

    // compute circle through A, B, C
    protected void compute() {
        // A, B or C undefined
        if (!getA().isFinite() || !getB().isFinite() || !getC().isFinite()) {
            circle.setUndefined();
            return;
        }

        // get inhomogenous coords of points
        ax = ((GeoPoint) getA()).inhomX;
        ay = ((GeoPoint) getA()).inhomY;
        bx = ((GeoPoint) getB()).inhomX;
        by = ((GeoPoint) getB()).inhomY;
        cx = ((GeoPoint) getC()).inhomX;
        cy = ((GeoPoint) getC()).inhomY;

        // A = B = C
        if (kernel.isEqual(ax, bx)
            && kernel.isEqual(ax, cx)
            && kernel.isEqual(ay, by)
            && kernel.isEqual(ay, cy)) {
            circle.setCircle((GeoPoint) getA(), 0.0); // single point
            return;
        }

        // calc vectors AB, AC, BC
        ABx = bx - ax;
        ABy = by - ay;
        ACx = cx - ax;
        ACy = cy - ay;
        BCx = cx - bx;
        BCy = cy - by;

        double lengthAB = GeoVec2D.length(ABx, ABy);
        double lengthAC = GeoVec2D.length(ACx, ACy);
        double lengthBC = GeoVec2D.length(BCx, BCy);

        // find the two bisectors with max intersection angle
        // i.e. maximum abs of determinant of directions            
        // max( abs(det(AB, AC)), abs(det(AC, BC)), abs(det(AB, BC)) )
        det[0] = Math.abs(ABx * ACy - ABy * ACx) / (lengthAB * lengthAC);
        // AB, AC
        det[1] = Math.abs(ACx * BCy - ACy * BCx) / (lengthAC * lengthBC);
        // AC, BC
        det[2] = Math.abs(ABx * BCy - ABy * BCx) / (lengthAB * lengthBC);
        // AB, BC

        // take ip[0] as init minimum and find minimum case
        maxDet = det[0];
        casenr = 0;
        if (det[1] > maxDet) {
            casenr = 1;
            maxDet = det[1];
        }
        if (det[2] > maxDet) {
            casenr = 2;
            maxDet = det[2];
        }

        // A, B, C are collinear: set M to infinite point
        // in perpendicular direction of AB
        if (kernel.isZero(maxDet)) {
            center.setCoords(-ABy, ABx, 0.0d);
            circle.setCircle(center, (GeoPoint) getA());
        }
        // standard case
        else {
            // intersect two line bisectors according to casenr
            switch (casenr) {
                case 0 : // bisectors of AB, AC                        
                    s0.x = ABx;
                    s0.y = ABy;
                    s0.z = - ((ax + bx) * s0.x + (ay + by) * s0.y) / 2.0;

                    s1.x = ACx;
                    s1.y = ACy;
                    s1.z = - ((ax + cx) * s1.x + (ay + cy) * s1.y) / 2.0;
                    break;

                case 1 : // bisectors of AC, BC                    
                    s1.x = ACx;
                    s1.y = ACy;
                    s1.z = - ((ax + cx) * s1.x + (ay + cy) * s1.y) / 2.0;

                    s0.x = BCx;
                    s0.y = BCy;
                    s0.z = - ((bx + cx) * s0.x + (by + cy) * s0.y) / 2.0;
                    break;

                case 2 : // bisectors of AB, BC                    
                    s0.x = ABx;
                    s0.y = ABy;
                    s0.z = - ((ax + bx) * s0.x + (ay + by) * s0.y) / 2.0;

                    s1.x = BCx;
                    s1.y = BCy;
                    s1.z = - ((bx + cx) * s1.x + (by + cy) * s1.y) / 2.0;
                    break;
            }

            // intersect line bisectors to get midpoint
            GeoVec3D.cross(s0, s1, center);
            circle.setCircle(center, center.distance(getA()));
        }
    }

    public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("CircleThroughABC",A.getLabel(),B.getLabel(),C.getLabel());
    }
}
