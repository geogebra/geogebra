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
import java.util.TreeMap;


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
		// CASE 2: data from value/frequency lists
		// ================================================
		else if (inputList.size() == freqList.size()) {

			if (!freqList.isDefined()
					|| !(inputList.size() == freqList.size() || inputList
							.size() == freqList.size() + 1)) {
				median.setUndefined();
				return;
			}

			for (int i = 0; i < freqList.size(); i++) {
				GeoElement geo = inputList.get(i);
				if (!freqList.get(i).isNumberValue()) {
					median.setUndefined();
					return;
				}
			}	
			
			for (int i = 0; i < inputList.size(); i++) {
				GeoElement geo = inputList.get(i);
				if (!inputList.get(i).isNumberValue()) {
					median.setUndefined();
					return;
				}
			}	
			
			
			// extract value and frequency arrays
			Object[] obj = convertValueFreqListToArrays(inputList, freqList);
			Double[] v = (Double[]) obj[0];
			Integer[] f = (Integer[]) obj[1];
			int n = (Integer) obj[2];
			

			// find the median
			if (Math.floor((double) n / 2) == n / 2.0) {
				median.setValue((getValueAt(n / 2, v, f) + getValueAt(
						n / 2 - 1, v, f)) / 2);
			} else {
				median.setValue(getValueAt((n - 1) / 2, v, f));
			}
		}

		// ============================================
		// CASE 3: data grouped by class and frequency
		// ============================================

		else {

			if (!freqList.isDefined()
					|| !(inputList.size() == freqList.size() || inputList
							.size() == freqList.size() + 1)) {
				median.setUndefined();
				return;
			}

			// TODO ok to assume classes are valid? (sorted no gaps)
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
	
	/**
	 * Returns the value at an index position in a list of data
	 * constructed from a sorted list of values and frequencies
	 * 
	 * @param index
	 * @param val
	 * @param freq
	 * @return
	 */
	public static Double getValueAt(int index, Double[] val, Integer[] freq) {

		int cf = 0; // cumulative frequency
		for (int i = 0; i < val.length; i++) {
			cf += freq[i];
			if (index < cf) {
				return val[i];
			}
		}
		return null;
	}

	
	public static Object[] convertValueFreqListToArrays(GeoList inputList,
			GeoList freqList) {
		
		// create a tree map to sort the value/frequency pairs
		double val;
		int freq;
		TreeMap<Double, Integer> tm = new TreeMap<Double, Integer>();
		for (int i = 0; i < freqList.size(); i++) {
			
			val = ((NumberValue) inputList.get(i)).getDouble();
			freq = (int) ((NumberValue) freqList.get(i)).getDouble();
			// handle repeated values
			if (tm.containsKey(val)) {
				freq += tm.get(val);
			}
			
			tm.put(val, freq);
		}

		// extract value and frequency arrays
		Double[] v = tm.keySet().toArray(new Double[tm.size()]);
		Integer[] f = tm.values().toArray(new Integer[tm.size()]);

		// get data size n
		int n = 0;
		for (int i = 0; i < f.length; i++) {
			n += f[i]; // add the frequency for this value
		}
					
		// return the arrays in an Object array
		Object[] obj = { v, f, n };
		return obj;
	}
	
	// TODO Consider locusequability
}
