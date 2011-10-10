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
 */

package geogebra3D.kernel3D;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Matrix.Coords;


/**
 * Length of a vector 
 * @author  mathieu
 * @version 
 */
public class AlgoLengthVector3D extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoVector3D v; // input
    private GeoNumeric num; // output 
    
    AlgoLengthVector3D(Construction cons, String label, GeoVector3D v) {
        super(cons);
        this.v = v;
        num = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        num.setLabel(label);
    }

    public String getClassName() {
        return "AlgoLengthVector";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = v;

        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getLength() {
        return num;
    }

    // calc length of vector v   
    protected final void compute() {
    	Coords coords = v.getCoords();
    	coords.calcNorm();
        num.setValue(coords.getNorm());
    }

    final public String toString() {
        return app.getPlain("LengthOfA",v.getLabel());
    }
}
