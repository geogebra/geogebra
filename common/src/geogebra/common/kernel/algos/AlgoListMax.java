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
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;


/**
 * Maximum value of a list.
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoListMax extends AlgoElement {

	private GeoList geoList; //input
    private GeoNumeric max; //output	

    public AlgoListMax(Construction cons, String label, GeoList geoList) {
    	this(cons, geoList);
        max.setLabel(label);
    }

    public AlgoListMax(Construction cons, GeoList geoList) {
        super(cons);
        this.geoList = geoList;
               
        max = new GeoNumeric(cons);

        setInputOutput();
        compute();
    }

    @Override
	public Commands getClassName() {
		return Commands.Max;
	}

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = geoList;

        setOutputLength(1);
        setOutput(0,max);
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getMax() {
        return max;
    }

    @Override
	public final void compute() {
    	int size = geoList.size();
    	if (!geoList.isDefined() ||  size == 0) {
    		max.setUndefined();
    		return;
    	}
    	
    	double maxVal = Double.NEGATIVE_INFINITY;
    	for (int i=0; i < size; i++) {
    		GeoElement geo = geoList.get(i);
    		if (geo.isNumberValue()) {
    			NumberValue num = (NumberValue) geo;
    			maxVal = Math.max(maxVal, num.getDouble());
    		} else {
    			max.setUndefined();
        		return;
    		}    		    		
    	}   
    	
    	max.setValue(maxVal);
    }

	// TODO Consider locusequability
    
}
