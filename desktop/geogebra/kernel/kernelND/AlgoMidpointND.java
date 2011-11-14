/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoMidPoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel.kernelND;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;


/**
 *
 * @author  Markus
 * @version 
 */
public abstract class AlgoMidpointND extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPointND P, Q; // input
    private GeoPointND M; // output        

	
    /**
     * 
     * @param cons
     * @param P
     * @param Q
     */
    protected AlgoMidpointND(Construction cons, GeoPointND P, GeoPointND Q) {
        super(cons);
        this.P = P;
        this.Q = Q;
        // create new Point
        M = newGeoPoint(cons);
        setInputOutput();

        // compute M = (P + Q)/2
        compute();        
    }
    
    /**
     * 
     * used for midpoint of a segment
     * 
     * @param cons
     * @param segment
     */
    protected AlgoMidpointND(Construction cons, GeoSegmentND segment) {
		super(cons);
		
		P = segment.getStartPoint();
    	Q = segment.getEndPoint();
    	
        // create new Point
        M = newGeoPoint(cons);
	}

	/**
     * 
     * @param cons
     * @return new GeoPointND 
     */
    protected abstract GeoPointND newGeoPoint(Construction cons);

    public String getClassName() {
        return "AlgoMidpoint";
    }

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_MIDPOINT;
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement) P;
        input[1] = (GeoElement) Q;

        output = new GeoElement[1];
        output[0] = (GeoElement) M;
        setDependencies(); // done by AlgoElement
    }
    
    // calc midpoint
    protected final void compute() {
    	
                
        boolean pInf = P.isInfinite();
        boolean qInf = Q.isInfinite();
        

        if (!pInf && !qInf) {
            // M = (P + Q) / 2          
            computeMidCoords();
        } else if (pInf && qInf)
            M.setUndefined();
        else if (pInf)
            copyCoords(P);
        else // qInf
        	copyCoords(Q);
    }
    
    
    
    /**
     * copy coords of the point to the output point
     * @param point
     */
    abstract protected void copyCoords(GeoPointND point);
    
    /**
     * compute output point as midpoint of input points
     */
    abstract protected void computeMidCoords();
    
    

    /**
     * 
     * @return the output point
     */
    protected GeoPointND getPoint() {
        return M;
    }

    /**
     * 
     * @return first input point
     */
    protected GeoPointND getP() {
        return P;
    }
    
    /**
     * 
     * @return second input point
     */
    protected GeoPointND getQ() {
        return Q;
    }

    public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("MidpointOfAB",P.getLabel(),Q.getLabel());

    }
}
