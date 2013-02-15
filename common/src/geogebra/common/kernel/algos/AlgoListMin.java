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
 * Minimum value of a list.
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoListMin extends AlgoElement {

	private GeoList geoList; //input
	private GeoList freqList; //input
    private GeoNumeric min; //output	

    public AlgoListMin(Construction cons, String label, GeoList geoList, GeoList freqList) {
        this(cons, geoList,freqList);
        
        min.setLabel(label);
    }
    
    public AlgoListMin(Construction cons, String label, GeoList geoList) {
        this(cons, geoList,null);
        
        min.setLabel(label);
    }

    public AlgoListMin(Construction cons, GeoList geoList) {
        this(cons, geoList,null);
    }

    
    public AlgoListMin(Construction cons, GeoList geoList, GeoList freqList) {
        super(cons);
        this.geoList = geoList;
        this.freqList = freqList;       
        min = new GeoNumeric(cons);

        setInputOutput();
        compute();
    }

    @Override
	public Commands getClassName() {
		return Commands.Min;
	}

    @Override
	protected void setInputOutput(){
    	
		if (freqList == null) {
			input = new GeoElement[1];
			input[0] = geoList;
		} else {
			input = new GeoElement[2];
			input[0] = geoList;
			input[1] = freqList;
		}

        super.setOutputLength(1);
        super.setOutput(0, min);
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getMin() {
        return min;
    }

    @Override
	public final void compute() {
    	int size = geoList.size();
    	if (!geoList.isDefined() ||  size == 0) {
    		min.setUndefined();
    		return;
    	}
    	
    	double minVal = Double.POSITIVE_INFINITY;
    	
		if (freqList == null) {

			for (int i = 0; i < size; i++) {
				GeoElement geo = geoList.get(i);
				if (geo.isNumberValue()) {
					NumberValue num = (NumberValue) geo;
					minVal = Math.min(minVal, num.getDouble());
				} else {
					min.setUndefined();
					return;
				}
			}
			
		} else {

			if (!freqList.isDefined() || freqList.size() != geoList.size()) {
				min.setUndefined();
				return;
			}

			boolean hasPositiveFrequency = false;
			for (int i = 0; i < size; i++) {
				GeoElement geo = geoList.get(i);
				GeoElement freqGeo = freqList.get(i);

				if (!geo.isNumberValue() || !freqGeo.isNumberValue()) {
					min.setUndefined();
					return;
				}

				// handle bad frequency
				double frequency = ((NumberValue) freqGeo).getDouble();
				if (frequency < 0) {
					min.setUndefined();
					return;
				} else if (frequency == 0) {
					continue;
				}
				hasPositiveFrequency = true;

				NumberValue num = (NumberValue) geo;
				minVal = Math.min(minVal, num.getDouble());
			}

			// make sure not all frequencies are zero
			if (!hasPositiveFrequency) {
				min.setUndefined();
				return;
			}

		}

		min.setValue(minVal);
	}

	// TODO Consider locusequability
    
}
