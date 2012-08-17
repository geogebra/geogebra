/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;


/**
 * Length of a GeoList object.
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoListLength extends AlgoElement {

	private GeoList geoList; //input
    private GeoNumeric length; //output	

    public AlgoListLength(Construction cons, String label, GeoList geoList) {
        this(cons, geoList);
        length.setLabel(label);
    }

    public AlgoListLength(Construction cons, GeoList geoList) {
        super(cons);
        this.geoList = geoList;
               
        length = new GeoNumeric(cons);

        setInputOutput();
        compute();
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoListLength;
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = geoList;

        super.setOutputLength(1);
        super.setOutput(0, length);
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getLength() {
        return length;
    }

    @Override
	public final void compute() {
    	if (geoList.isDefined()) {
    		length.setValue(geoList.size());
    	} else {
    		length.setUndefined();
    	}
    }

	// TODO Consider locusequability
    
}
