/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

import java.util.Arrays;


/**
 * Sort a list. Adapted from AlgoSort
 * @author Michael Borcherds
 * @version 2008-02-16
 */

public class AlgoMedian extends AlgoElement {

	
	private GeoList inputList, freqList; //input
    private GeoNumeric median; //output	
    private int size;

    public AlgoMedian(Construction cons, String label, GeoList inputList) {
    	this(cons, inputList);
        median.setLabel(label);
    }

    public AlgoMedian(Construction cons, String label, GeoList inputList, GeoList freqList) {
    	this(cons, inputList, freqList);
        median.setLabel(label);
    }
    
    public AlgoMedian(Construction cons, GeoList inputList) {
    	this(cons, inputList, null);
    }
    
    
    public AlgoMedian(Construction cons, GeoList inputList, GeoList freqList) {
        super(cons);
        this.inputList = inputList;
        this.freqList = freqList;
        
        median = new GeoNumeric(cons);

        setInputOutput();
        compute();
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoMedian;
    }

    @Override
    protected void setInputOutput(){

    	if(freqList == null){
    		input = new GeoElement[1];
    		input[0] = inputList;
    	}
    	else{
    		input = new GeoElement[2];
    		input[0] = inputList;
    		input[1] = freqList;
    	}
        
        setOnlyOutput(median);
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getMedian() {
        return median;
    }

	@Override
	public final void compute() {

		size = inputList.size();
		if (!inputList.isDefined() || size == 0) {
			median.setUndefined();
			return;
		}

		// ========================================
		// CASE 1: simple list of data
		// ========================================
		if (freqList == null) {
			double[] sortList = new double[size];

			// copy inputList into an array
			for (int i = 0; i < size; i++) {
				GeoElement geo = inputList.get(i);
				if (geo.isNumberValue()) {
					NumberValue num = (NumberValue) geo;
					sortList[i] = num.getDouble();
				} else {
					median.setUndefined();
					return;
				}
			}

			// do the sorting
			Arrays.sort(sortList);

			if (Math.floor((double) size / 2) == size / 2.0) {
				median.setValue((sortList[size / 2] + sortList[size / 2 - 1]) / 2);
			} else {
				median.setValue(sortList[(size - 1) / 2]);
			}
		}

		// ================================================
		// CASE 2: data from ungrouped list with associated frequency
		// ================================================
		else if (inputList.size() == freqList.size()) {

			if (!freqList.isDefined()
					|| !(inputList.size() == freqList.size() || inputList
							.size() == freqList.size() + 1)) {
				median.setUndefined();
				return;
			}

			// get data size n
			int n = 0;
			for (int i = 0; i < freqList.size(); i++) {
				n += ((NumberValue) freqList.get(i)).getDouble();
			}
			
			int cf = 0;
			double value;

			// even size: median = average of values at position n/2 and n/2 + 1
			if ((Math.floor((double) n / 2) == n / 2.0)) {
				for (int i = 0; i < freqList.size(); i++) {
					value = ((NumberValue) inputList.get(i)).getDouble();
					cf += (int) ((NumberValue) freqList.get(i+1)).getDouble();
					if(cf >= n/2){
						// n/2 and n/2 + 1  values are different
						if (cf == n / 2) {
							median.setValue((value + ((NumberValue) inputList
									.get(i + 1)).getDouble()) / 2);
						} else {
							median.setValue(value);
						}
						break;
					}
				}
			}

			// odd size: median = value at (n+1)/2 position
			else {
				for (int i = 0; i < freqList.size(); i++) {
					value = ((NumberValue) inputList.get(i)).getDouble();
					cf += (int) ((NumberValue) freqList.get(i)).getDouble();
					
					if (cf >= (n + 1) / 2) {
						median.setValue(value);
						break;
					}	
				}
			}
		}

		// ============================================
		// CASE 3: data grouped by classes and frequency
		// ============================================

		else {

			if (!freqList.isDefined()
					|| !(inputList.size() == freqList.size() || inputList
							.size() == freqList.size() + 1)) {
				median.setUndefined();
				return;
			}

			double n = 0;
			for (int i = 0; i < freqList.size(); i++) {
				n += ((NumberValue) freqList.get(i)).getDouble();
			}
			int cf = 0;
			int f = 0;
			double lowBound, highBound;
			for (int i = 0; i < freqList.size(); i++) {
				lowBound = ((NumberValue) inputList.get(i)).getDouble();
				highBound = ((NumberValue) inputList.get(i + 1)).getDouble();
				f = (int) ((NumberValue) freqList.get(i)).getDouble();

				if (cf + f >= n / 2) {
					double width = highBound - lowBound;
					
					// apply grouped median linear interpolation formula		
					median.setValue(lowBound + width * (n / 2 - cf) / f);
					
					break;
				}
				cf += f;
			}
		}
	}

	// TODO Consider locusequability
}
