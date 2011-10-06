/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoLengthVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.kernelND.GeoSegmentND;


/**
 * Length of a segment.
 * @author  mathieu
 * @version 
 */
public class AlgoLengthSegment extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoSegmentND seg; // input
    private GeoNumeric num; // output 
    
    private double [] coords = new double[2];
    
    AlgoLengthSegment(Construction cons, String label, GeoSegmentND seg) {
        super(cons);
        this.seg = seg;
        num = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        num.setLabel(label);
    }

    public String getClassName() {
        return "AlgoLengthSegment";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = (GeoElement) seg;

        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getLength() {
        return num;
    }
    

    // calc length of vector v   
    protected final void compute() {
    	
        num.setValue(seg.getLength());
    }

    final public String toString() {
        return app.getPlain("LengthOfA",((GeoElement) seg).getLabel());

    }
}
