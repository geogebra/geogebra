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

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.kernelND.GeoSegmentND;


/**
 *
 * @author  mathieu
 * @version 
 */
public class AlgoMidpointSegment3D extends AlgoMidpoint3D {
      
	
	private GeoSegmentND segment;

    /** 
     * 
     * @param cons
     * @param label
     * @param segment 
     */
    AlgoMidpointSegment3D(Construction cons, String label, GeoSegmentND segment) {
    	this(cons, segment);
    	getPoint().setLabel(label);
    }
	
    /**
     * 
     * @param cons
     * @param segment 
     */
    AlgoMidpointSegment3D(Construction cons, GeoSegmentND segment) {
        super(cons,segment);
        this.segment=segment;
        
        setInputOutput(); 

        // compute M = (P + Q)/2
        compute();   
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = (GeoElement) segment;        

        output = new GeoElement[1];
        output[0] = getPoint();
        setDependencies(); // done by AlgoElement
    }

    
    final public String toString() {
        return app.getPlain("MidpointOfA",((GeoElement) segment).getLabel());

    }

    
    public String getClassName() {
        return "AlgoMidpointSegment";
    }


}
