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

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoSegmentND;


/**
 * Length of a segment.
 * @author  mathieu
 * @version 
 */
public class AlgoLengthSegment extends AlgoElement {

    private GeoSegmentND seg; // input
    private GeoNumeric num; // output 
    
    public AlgoLengthSegment(Construction cons, String label, GeoSegmentND seg) {
        super(cons);
        this.seg = seg;
        num = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        num.setLabel(label);
    }

    @Override
	public Commands getClassName() {
        return Commands.Length;
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

    public GeoNumeric getLength() {
        return num;
    }
    

    // calc length of vector v   
    @Override
	public final void compute() {
    	
        num.setValue(seg.getLength());
    }

    @Override
	final public String toString(StringTemplate tpl) {
        return app.getPlain("LengthOfA",((GeoElement) seg).getLabel(tpl));

    }

	// TODO Consider locusequability
}
