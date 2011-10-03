/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoRotatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.arithmetic.NumberValue;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoRotatePoint extends AlgoTransformation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint Q;
    private PointRotateable out;    
    private NumberValue angle; 
    private GeoElement inGeo, outGeo, angleGeo;
    
    /**
     * Creates new point rotation algo
     * @param cons
     * @param label
     * @param A
     * @param angle
     * @param Q
     */
    AlgoRotatePoint(Construction cons, String label,
            GeoElement A, NumberValue angle, GeoPoint Q) {
    	this(cons, A, angle, Q);
    	outGeo.setLabel(label);
    }
    
    /**
     * Creates new unlabeled point rotation algo
     * @param cons
     * @param A
     * @param angle
     * @param Q
     */
    AlgoRotatePoint(Construction cons, 
    		GeoElement A, NumberValue angle, GeoPoint Q) {
        super(cons);               
        this.angle = angle;
        this.Q = Q;

        angleGeo = angle.toGeoElement();
        inGeo = A;
        
        outGeo = getResultTemplate(inGeo);
        if(outGeo instanceof PointRotateable)
        	out = (PointRotateable) outGeo;
        
        setInputOutput();
        compute();
        if(inGeo.isGeoFunction())
        	cons.registerEuclidianViewCE(this);
    }

    public String getClassName() {
        return "AlgoRotatePoint";
    }


    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ROTATE_BY_ANGLE;
    }

    
    /**
     * Returns true iff euclidian view updte is needed (for images)
     * @return true iff euclidian view updte is needed 
     */
    final public boolean wantsEuclidianViewUpdate() {
        return inGeo.isGeoImage();
    }
    
    // for AlgoElement
    protected void setInputOutput() {    	
        input = new GeoElement[3];
        input[0] = inGeo;
        input[1] = angleGeo;
        input[2] = Q;

        setOutputLength(1);
        setOutput(0,outGeo);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the rotated point
     * @return rotated point
     */
    GeoElement getResult() {
        return outGeo;
    }

    // calc rotated point
    protected final void compute() {
    	if(inGeo.isGeoList()){
    		transformList((GeoList)inGeo,(GeoList)outGeo);
    		return;
    	}
    	if(inGeo instanceof GeoFunction){
    		((GeoFunction)inGeo).toGeoCurveCartesian((GeoCurveCartesian)outGeo);
    	}
    	else outGeo.set(inGeo);
        out.rotate(angle, Q);
        if(inGeo.isLimitedPath())
        	this.transformLimitedPath(inGeo, outGeo);
    }
       
    final public String toString() {
        // Michael Borcherds 2008-03-25
        // simplified to allow better Chinese translation
        return app.getPlain("ARotatedByAngleB",inGeo.getLabel(),angleGeo.getLabel());

    }

    @Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if(!(outGeo instanceof GeoList))
			out = (PointRotateable)outGeo;
		
	}
    
    @Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if(geo instanceof GeoFunction)
			return new GeoCurveCartesian(cons);
		return super.getResultTemplate(geo);
	}
    
}
