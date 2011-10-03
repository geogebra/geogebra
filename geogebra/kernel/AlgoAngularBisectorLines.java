/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAngularBisectorLines.java
 *
 * Created on 26. Oktober 2001
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;


/**
 * Angle bisectors between two lines
 * @author  Markus
 * @version 
 */
public class AlgoAngularBisectorLines extends AlgoElement {
    
	private static final long serialVersionUID = 1L;
	private GeoLine g, h; // input    
    private GeoLine[] bisector; // output   

    // temp
    private double gx, gy, hx, hy, wx, wy, bx, by, lenH, lenG, length, ip;
    private GeoVector[] wv; // direction of bisector line bisector
    private GeoPoint B; // intersection point of g, h
    private boolean infiniteB;
    private int index;

    /** Creates new AlgoAngularBisectorLines 
     * @param cons 
     * @param label 
     * @param g 
     * @param h */
    AlgoAngularBisectorLines(
        Construction cons,
        String label,
        GeoLine g,
        GeoLine h) {
        this(cons, g, h);
        GeoElement.setLabels(label, bisector);
    }

    AlgoAngularBisectorLines(
        Construction cons,
        String[] labels,
        GeoLine g,
        GeoLine h) {
        this(cons, g, h);
        GeoElement.setLabels(labels, bisector);
    }

    public String getClassName() {
        return "AlgoAngularBisectorLines";
    }

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ANGULAR_BISECTOR;
    }
    
    AlgoAngularBisectorLines(Construction cons, GeoLine g, GeoLine h) {
        super(cons);
        this.g = g;
        this.h = h;
        bisector = new GeoLine[2];
        bisector[0] = new GeoLine(cons);
        bisector[1] = new GeoLine(cons);
        setInputOutput(); // for AlgoElement

        wv = new GeoVector[2];
        wv[0] = new GeoVector(cons);
        wv[0].setCoords(0, 0, 0);
        wv[1] = new GeoVector(cons);
        wv[1].setCoords(0, 0, 0);
        B = new GeoPoint(cons);

        bisector[0].setStartPoint(B);
        bisector[1].setStartPoint(B);

        // compute bisectors of lines g, h
        compute();
    }

    // for AlgoElement
    public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = g;
        input[1] = h;

        output = bisector;
        setDependencies(); // done by AlgoElement
    }

    GeoLine[] getLines() {
        return bisector;
    }
    GeoLine getg() {
        return g;
    }
    GeoLine geth() {
        return h;
    }
    GeoPoint getB() {
        return B;
    }
    
    public boolean isNearToAlgorithm() {
    	return true;
    }

    protected final void compute() {
        // calc intersection B of g and h
        GeoVec3D.cross(g, h, B);
        infiniteB = B.isInfinite();

        // (gx, gy) is direction of g = B v A        
        gx = g.y;
        gy = -g.x;
        lenG = GeoVec2D.length(gx, gy);
        gx /= lenG;
        gy /= lenG;

        // (hx, hy) is direction of h = B v C
        hx = h.y;
        hy = -h.x;
        lenH = GeoVec2D.length(hx, hy);
        hx /= lenH;
        hy /= lenH;

        // set direction vector of bisector: (wx, wy)        
        if (infiniteB) {
            // if B is at infinity then g and h are parallel
            // and the bisector line has same direction as g (or h)

            // calc z value of line in the middle of g, h 
            // orientation of g, h may differ: 2 cases
            if (gx * hx + gy * hy > 0) { // same orientation
                index = 0; // set first bisector
                bisector[index].z = (g.z / lenG + h.z / lenH) / 2.0;
            } else { // different orientation
                index = 1; // set second bisector
                bisector[index].z = (g.z / lenG - h.z / lenH) / 2.0;
            }

            // take direction of g as proposed direction for bisector
            wx = gx;
            wy = gy;

            if (kernel.isContinuous()) {
            	// init old direction of bisectors
            	if (bisector[0].isDefined()) {
            		wv[0].x = bisector[0].y;            	
            		wv[0].y = -bisector[0].x;
            	}
            	if (bisector[1].isDefined()) {
            		wv[1].x = bisector[1].y;            	
            		wv[1].y = -bisector[1].x;
            	}
            	
	            // NEAR TO RELATIONSHIP
	            // check orientation: take smallest change!!!
	            if (wv[index].x * wx + wv[index].y * wy >= 0) {
	                wv[index].x = wx;
	                wv[index].y = wy;
	            } else { // angle > 180�, change orientation
	                wv[index].x = -wx;
	                wv[index].y = -wy;
	                bisector[index].z = -bisector[index].z;
	            }
            } else {
            	// non continuous
            	wv[index].x = wx;
                wv[index].y = wy;
            }

            // set direction vector of bisector
            bisector[index].x = -wv[index].y;
            bisector[index].y = wv[index].x;
            // ohter bisector is undefined
            bisector[1 - index].setUndefined();
        }
        // standard case: B is not at infinity            
        else {
            // calc direction vector (wx, wy) of angular bisector
            // check if angle between vectors is > 90�
            ip = gx * hx + gy * hy;
            if (ip >= 0.0) { // angle < 90�
                // standard case
                wx = gx + hx;
                wy = gy + hy;
            } else { // ip <= 0.0, angle > 90�            
                // BC - BA is a normalvector of the bisector                        
            	wx = hy - gy;
                wy = gx - hx;
                
                // if angle > 180 degree change orientation of direction
                // det(g,h) < 0
                if (gx * hy < gy * hx) {
                	wx = -wx;
                	wy = -wy;
                }   
            }

            // make (wx, wy) a unit vector
            length = GeoVec2D.length(wx, wy);
            wx /= length;
            wy /= length;

            if (kernel.isContinuous()) {
            	// init old direction of bisectors
            	if (bisector[0].isDefined()) {
            		wv[0].x = bisector[0].y;            	
            		wv[0].y = -bisector[0].x;
            	}
            	if (bisector[1].isDefined()) {
            		wv[1].x = bisector[1].y;            	
            		wv[1].y = -bisector[1].x;
            	}
            	
	            // check orientations: take smallest change!!!
	            // first bisector: relativ to (wx, wy)
	            if (wv[0].x * wx + wv[0].y * wy >= 0) {
	                wv[0].x = wx;
	                wv[0].y = wy;
	            } else { // angle > 180 degree change orientation
	                wv[0].x = -wx;
	                wv[0].y = -wy;
	            }
	            // second bisector: relativ to (-wy, wx) 
	            if (wv[1].y * wx - wv[1].x * wy >= 0) {
	                wv[1].x = -wy;
	                wv[1].y = wx;
	            } else { // angle > 180 degree change orientation
	                wv[1].x = wy;
	                wv[1].y = -wx;
	            }
            } else {
            	// non continuous
            	wv[0].x = wx;
	            wv[0].y = wy;
	            wv[1].x = -wy;
                wv[1].y = wx;
            }

            // calc B's coords
            bx = B.inhomX;
            by = B.inhomY;

            // set first bisector through B
            bisector[0].x = -wv[0].y;
            bisector[0].y = wv[0].x;
            bisector[0].z = - (bx * bisector[0].x + by * bisector[0].y);

            // set second bisector perpendicular to first through B
            bisector[1].x = -wv[1].y;
            bisector[1].y = wv[1].x;
            bisector[1].z = - (bx * bisector[1].x + by * bisector[1].y);
        }
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("AngleBisectorOfAB",g.getLabel(),h.getLabel());
    }
}
