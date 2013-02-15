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
	private GeoList freqList; //input
    private GeoNumeric max; //output	

    public AlgoListMax(Construction cons, String label, GeoList geoList, GeoList freqList) {
    	this(cons, geoList, freqList);
        max.setLabel(label);
    }
    
    public AlgoListMax(Construction cons, String label, GeoList geoList) {
    	this(cons, geoList, null);
        max.setLabel(label);
    }

    public AlgoListMax(Construction cons, GeoList geoList) {
    	this(cons, geoList, null);
    }
    
    public AlgoListMax(Construction cons, GeoList geoList, GeoList freqList) {
        super(cons);
        this.geoList = geoList;
        this.freqList = freqList;       
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
    	if(freqList == null){
        input = new GeoElement[1];
        input[0] = geoList;
    	}else{
    		input = new GeoElement[2];
            input[0] = geoList;
            input[1] = freqList;
    	}

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
		
		if (freqList == null) {
			for (int i = 0; i < size; i++) {
				GeoElement geo = geoList.get(i);
				if (geo.isNumberValue()) {
					NumberValue num = (NumberValue) geo;
					maxVal = Math.max(maxVal, num.getDouble());
				} else {
					max.setUndefined();
					return;
				}
			}
		} else {
			if (!freqList.isDefined() || freqList.size() != geoList.size()) {
				max.setUndefined();
				return;
			}
			
			boolean hasPositiveFrequency = false;
			
			for (int i = 0; i < size; i++) {
				GeoElement geo = geoList.get(i);
				GeoElement freqGeo = freqList.get(i);
				
				if (!geo.isNumberValue() || !freqGeo.isNumberValue()) {
					max.setUndefined();
					return;
				}
				
				// handle bad frequency
				double frequency = ((NumberValue) freqGeo).getDouble();
				if (frequency < 0) {
					max.setUndefined();
					return;
				} else if (frequency == 0) {
					continue;
				}
				hasPositiveFrequency = true;
				NumberValue num = (NumberValue) geo;
				maxVal = Math.max(maxVal, num.getDouble());
			}
			
			// make sure not all frequencies are zero
			if(!hasPositiveFrequency){
	    		max.setUndefined();
	    		return;
	    	}
		}
    	
    	max.setValue(maxVal);
    }

	// TODO Consider locusequability
    
}
