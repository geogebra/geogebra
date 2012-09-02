/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * Returns whether an object is an integer
 * @author Michael Borcherds
 * @version 2008-03-06
 */

public class AlgoIsInteger extends AlgoElement {

	private GeoNumeric inputGeo; //input
    private GeoBoolean outputBoolean; //output	

    public AlgoIsInteger(Construction cons, String label, GeoNumeric inputGeo) {
        super(cons);
        this.inputGeo = inputGeo;

               
        outputBoolean = new GeoBoolean(cons);

        setInputOutput();
        compute();
        outputBoolean.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoIsInteger;
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputGeo;

        super.setOutputLength(1);
        super.setOutput(0, outputBoolean);
        setDependencies(); // done by AlgoElement
    }

    public GeoBoolean getResult() {
        return outputBoolean;
    }

    @Override
	public final void compute() {
        outputBoolean.setValue(Kernel.isInteger(inputGeo.getDouble()));
    }

	// TODO Consider locusequability
  
}
