/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;

/**
 * Creates ray from point and direction vector
 *
 */
public class AlgoRayPointVector extends AlgoElement {

    /**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint P; // input
    private GeoVector v; // input
    private GeoRay ray; // output       

    /**
     * Creates new ray algo
     * @param cons
     * @param label
     * @param P
     * @param v
     */
    AlgoRayPointVector(
        Construction cons, String label, GeoPoint P, GeoVector v) {
    	this(cons, P, v);
        ray.setLabel(label);
    }

    
    /**
     * Creates new ray algo
     * @param cons
     * @param P
     * @param v
     */
    public AlgoRayPointVector(
        Construction cons, GeoPoint P, GeoVector v) {
        super(cons);
        this.P = P;
        this.v = v;
        ray = new GeoRay(cons, P);
        setInputOutput(); // for AlgoElement

        // compute line through P, Q
        compute();
        setIncidence();
    }
    
    
    
    
    
    
    private void setIncidence() {
    	P.addIncidence(ray);
	}
    
    public String getClassName() {
        return "AlgoRayPointVector";
    }
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_RAY;
    }
    

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = v;

        setOutputLength(1);
        setOutput(0,ray);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the ray
     * @return resulting ray
     */
    GeoRay getRay() {
        return ray;
    }
    /**
     * Returns the endpoint
     * @return the endpoint
     */
    GeoPoint getP() {
        return P;
    }
    
    /**
     * Returns the direction
     * @return direction vector
     */
    GeoVector getv() {
        return v;
    }

    // calc the line g through P and Q    
    protected final void compute() {
        // g = cross(P, v)
        GeoVec3D.lineThroughPointVector(P, v, ray);
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("RayThroughAWithDirectionB",P.getLabel(),v.getLabel());

    }

}
