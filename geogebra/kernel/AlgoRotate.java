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
public class AlgoRotate extends AlgoTransformation {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Rotateable out;    
    private NumberValue angle; 
    private GeoElement inGeo, outGeo, angleGeo;
    /**
     * Creates new generic rotation algo
     * @param cons
     * @param label
     * @param A
     * @param angle
     */
    AlgoRotate(Construction cons, String label,
            GeoElement A, NumberValue angle) {
    	this(cons, A, angle);
    	outGeo.setLabel(label);
    }
    
    /**
     * Creates new unlabeled rotation algo
     * @param cons
     * @param A
     * @param angle
     */
    AlgoRotate(Construction cons, GeoElement A, NumberValue angle) {
        super(cons);        
        this.angle = angle;

        angleGeo = angle.toGeoElement();
        inGeo = A;
        
        
        // create output object
        outGeo = getResultTemplate(inGeo);
        if(outGeo instanceof PointRotateable)
        	out = (PointRotateable) outGeo;
        
        setInputOutput();
        compute();       
        if(inGeo.isGeoFunction())
        	cons.registerEuclidianViewCE(this);
    }

    public String getClassName() {
        return "AlgoRotate";
    }

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ROTATE_BY_ANGLE;
    }
    
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = inGeo;
        input[1] = angle.toGeoElement();

        setOutputLength(1);
        setOutput(0,outGeo);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the rotated object
     * @return rotated object
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
        out.rotate(angle);
        if(inGeo.isLimitedPath())
        	this.transformLimitedPath(inGeo, outGeo);
    }
    
       
    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("ARotatedByAngleB",inGeo.getLabel(),angleGeo.getLabel());

    }
    @Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if(!(outGeo instanceof GeoList))
			out = (Rotateable)outGeo;
		
	}
    
    @Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if(geo instanceof GeoFunction)
			return new GeoCurveCartesian(cons);
		return super.getResultTemplate(geo);
	}
}
