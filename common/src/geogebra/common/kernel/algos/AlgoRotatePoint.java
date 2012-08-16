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
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.PointRotateable;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoRotatePoint extends AlgoTransformation {

	private GeoPoint Q;
    private PointRotateable out;    
    private NumberValue angle; 
    private GeoElement inGeo, outGeo, angleGeo;
    
    /**
     * Creates new point rotation algo
     */
    AlgoRotatePoint(Construction cons, String label,
            GeoElement A, NumberValue angle, GeoPoint Q) {
    	this(cons, A, angle, Q);
    	outGeo.setLabel(label);
    }
    
    /**
     * Creates new unlabeled point rotation algo
     */
    public AlgoRotatePoint(Construction cons, 
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

    @Override
	public Algos getClassName() {
        return Algos.AlgoRotatePoint;
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ROTATE_BY_ANGLE;
    }
  
    // for AlgoElement
    @Override
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
        out.rotate(angle, Q);
        if(inGeo.isLimitedPath())
        	this.transformLimitedPath(inGeo, outGeo);
    }
       
    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-25
        // simplified to allow better Chinese translation
        return app.getPlain("ARotatedByAngleB",inGeo.getLabel(tpl),angleGeo.getLabel(tpl));
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

	// TODO Consider locusequability
    
}
