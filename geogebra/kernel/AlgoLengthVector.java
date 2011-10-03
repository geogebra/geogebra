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


/**
 * Length of a vector or point.
 * @author  Markus
 * @version 
 */
public class AlgoLengthVector extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoVec3D v; // input
    private GeoNumeric num; // output 
    
    private double [] coords = new double[2];
    
    AlgoLengthVector(Construction cons, String label, GeoVec3D v) {
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
    GeoVec3D getv() {
        return v;
    }

    // calc length of vector v   
    protected final void compute() {
    	v.getInhomCoords(coords);
        num.setValue(GeoVec2D.length(coords[0], coords[1]));
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("LengthOfA",v.getLabel());

    }
}
