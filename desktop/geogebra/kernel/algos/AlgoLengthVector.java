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

package geogebra.kernel.algos;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.GeoVec3D;


/**
 * Length of a vector or point.
 * @author  Markus
 * @version 
 */
public class AlgoLengthVector extends AlgoElement {

    private GeoVec3D v; // input
    private GeoNumeric num; // output 
    
    private double [] coords = new double[2];
    
    public AlgoLengthVector(Construction cons, String label, GeoVec3D v) {
        super(cons);
        this.v = v;
        num = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        num.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoLengthVector";
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = v;

        super.setOutputLength(1);
        super.setOutput(0, num);
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getLength() {
        return num;
    }
    GeoVec3D getv() {
        return v;
    }

    // calc length of vector v   
    @Override
	public final void compute() {
    	v.getInhomCoords(coords);
        num.setValue(GeoVec2D.length(coords[0], coords[1]));
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("LengthOfA",v.getLabel());

    }
}
