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

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.PointRotateable;
import geogebra.common.kernel.geos.Rotateable;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoRotate extends AlgoTransformation {

    private Rotateable out;    
    private NumberValue angle; 
    private GeoElement inGeo, outGeo, angleGeo;
    /**
     * Creates new generic rotation algo
     */
    AlgoRotate(Construction cons, String label,
            GeoElement A, NumberValue angle) {
    	this(cons, A, angle);
    	outGeo.setLabel(label);
    }
    
    /**
     * Creates new unlabeled rotation algo
     */
    public AlgoRotate(Construction cons, GeoElement A, NumberValue angle) {
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

    @Override
	public Algos getClassName() {
        return Algos.AlgoRotate;
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ROTATE_BY_ANGLE;
    }   
    
    // for AlgoElement
    @Override
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
    @Override
	public
	GeoElement getResult() {
        return outGeo;
    }

    // calc rotated point
    @Override
	public final void compute() {
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
    
       
    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("ARotatedByAngleB",inGeo.getLabel(tpl),angleGeo.getLabel(tpl));
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

	// TODO Consider locusequability
}
