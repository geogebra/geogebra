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

    private GeoSegmentND seg; // input
    private GeoNumeric num; // output 
    
    AlgoLengthSegment(Construction cons, String label, GeoSegmentND seg) {
        super(cons);
        this.seg = seg;
        num = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        num.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoLengthSegment";
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = (GeoElement) seg;

        super.setOutputLength(1);
        super.setOutput(0, num);
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getLength() {
        return num;
    }
    

    // calc length of vector v   
    @Override
	protected final void compute() {
    	
        num.setValue(seg.getLength());
    }

    @Override
	final public String toString() {
        return app.getPlain("LengthOfA",((GeoElement) seg).getLabel());

    }
}
