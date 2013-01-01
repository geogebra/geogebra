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

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.kernelND.RotateableND;


/**
 *
 * @author  mathieu
 */
public abstract class AlgoRotate3D extends AlgoTransformation {

	protected GeoElement in;
    protected RotateableND out;    
    protected NumberValue angle; 
    
 
    
    /**
     * Creates new unlabeled point rotation algo
     */
    public AlgoRotate3D(Construction cons, 
    		GeoElement in,
    		NumberValue angle) {
    	
        super(cons);   
        this.in = in;
        this.angle = angle;

        out = (RotateableND) getResultTemplate(in);
         
    }

  
 	/**
 	 * 
 	 */
 	protected void setOutput() { 
  	

        setOutputLength(1);
        setOutput(0, (GeoElement) out);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the rotated point
     * @return rotated point
     */
    @Override
	public
	GeoElement getResult() {
        return (GeoElement) out;
    }

  
       
    @Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		in = g;
		out = (RotateableND) g2;	
	}
    
    
    @Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if(geo instanceof GeoFunction)
			return new GeoCurveCartesian3D(cons);

		return super.getResultTemplate(geo);
	}
	
	@Override
	protected GeoElement copy(GeoElement geo) {
		return ((Kernel3D) kernel).copy3D(geo);
	}

    
}
