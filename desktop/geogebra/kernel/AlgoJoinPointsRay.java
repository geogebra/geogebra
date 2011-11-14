/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoJoinPoints.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.common.euclidian.EuclidianConstants;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoJoinPointsRay extends AlgoElement {

    private GeoPoint P, Q;  // input
    private GeoRay  ray;     // output       
        
    /** Creates new AlgoJoinPoints */
    AlgoJoinPointsRay(Construction cons, String label, GeoPoint P, GeoPoint Q) {
        super(cons);
        this.P = P;
        this.Q = Q;                
        ray = new GeoRay(cons, P); 
        ray.setEndPoint(Q);
        setInputOutput(); // for AlgoElement
        
        // compute line through P, Q
        compute();      
        setIncidence();
        ray.setLabel(label);
    }   
    
    private void setIncidence() {
    	P.addIncidence(ray);
    	Q.addIncidence(ray);
	}
    
    @Override
	public String getClassName() {
        return "AlgoJoinPointsRay";
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_RAY;
    }
       
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = Q;
         
        super.setOutputLength(1);
        super.setOutput(0, ray);
        setDependencies(); // done by AlgoElement
    }    
    
    GeoRay getRay() { return ray; }
    GeoPoint getP() { return P; }
    GeoPoint getQ() { return Q; }
    
    // calc the line g through P and Q    
    @Override
	protected final void compute() {
        // g = P v Q  <=>  g_n : n = P x Q
        // g = cross(P, Q)
        GeoVec3D.lineThroughPoints(P, Q, ray);        
    }   
    
    @Override
	final public String toString() {
        
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
    	return app.getPlain("RayThroughAB",P.getLabel(),Q.getLabel());

    }
}
