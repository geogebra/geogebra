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

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoVectorND;


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
	private GeoVectorND v; // input
    private GeoNumeric num; // output 
    
    AlgoLengthVector3D(Construction cons, String label, GeoVectorND v) {
        super(cons);
        this.v = v;
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
        input[0] = (GeoElement)v;

        setOnlyOutput(num);
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getLength() {
        return num;
    }

    // calc length of vector v   
    @Override
	public final void compute() {
    	Coords coords = v.getDirectionInD3();
    	coords.calcNorm();
        num.setValue(coords.getNorm());
    }

    @Override
	final public String toString(StringTemplate tpl) {
        return app.getPlain("LengthOfA",((GeoElement)v).getLabel(tpl));
    }

	// TODO Consider locusequability
}
