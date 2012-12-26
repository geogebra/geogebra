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

import java.util.Arrays;


/**
 * Sort a list. Adapted from AlgoSort
 * @author Michael Borcherds
 * @version 2008-02-16
 */

public class AlgoQ1 extends AlgoElement {

	
	private GeoList inputList, freqList; //input
    private GeoNumeric Q1; //output	
    private int size;

    public AlgoQ1(Construction cons, String label, GeoList inputList) {
    	this(cons, inputList);
    	Q1.setLabel(label);
    }

    public AlgoQ1(Construction cons, String label, GeoList inputList, GeoList freqList) {
    	this(cons, inputList, freqList);
    	Q1.setLabel(label);
    }
    
    public AlgoQ1(Construction cons, GeoList inputList) {
    	this(cons, inputList, null);
    }
    
    
    public AlgoQ1(Construction cons, GeoList inputList, GeoList freqList) {
        super(cons);
        this.inputList = inputList;
        this.freqList = freqList;
        
        Q1 = new GeoNumeric(cons);

        setInputOutput();
        compute();
    }
    
    @Override
	public Commands getClassName() {
        return Commands.Q1;
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
        
        setOnlyOutput(Q1);
        setDependencies(); // done by AlgoElement
    }
    
     
    
    public GeoNumeric getQ1() {
        return Q1;
    }

    @Override
	public final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size < 2) {
    		Q1.setUndefined();
    		return;
    	} 

    	// ========================================
    	// CASE 1: simple list of data
    	// ========================================
    	
    	if (freqList == null) {	
    		double[] sortList = new double[size];

    		// copy inputList into an array
    		for (int i=0 ; i<size ; i++)
    		{
    			GeoElement geo = inputList.get(i); 
    			if (geo.isNumberValue()) {
    				NumberValue num = (NumberValue) geo;
    				sortList[i]=num.getDouble();
    			}
    			else
    			{
    				Q1.setUndefined();
    				return;			
    			}
    		}

    		// do the sorting
    		Arrays.sort(sortList);


    		switch (size % 4)
    		{
    		case 0:
    			Q1.setValue((sortList[(size)/4-1]+sortList[(size+4)/4-1])/2);  
    			break;
    		case 1:
    			Q1.setValue((sortList[(size-1)/4-1]+sortList[(size+3)/4-1])/2);  
    			break;
    		case 2:
    			Q1.setValue(sortList[(size+2)/4-1]);  
    			break;
    		default:
    			Q1.setValue(sortList[(size+1)/4-1]);  
    			break;
    		}

    	}
    	
    	// ================================================
		// CASE 2: data from value/frequency lists
		// ================================================
		else if (inputList.size() == freqList.size()) {

			if (!freqList.isDefined() || (inputList.size() != freqList.size())) {
				Q1.setUndefined();
				return;
			}

			for (int i = 0; i < freqList.size(); i++) {
				if (!freqList.get(i).isNumberValue()) {
					Q1.setUndefined();
					return;
				}
			}	
			
			for (int i = 0; i < inputList.size(); i++) {
				if (!inputList.get(i).isNumberValue()) {
					Q1.setUndefined();
					return;
				}
			}	
			
			// extract value and frequency arrays
			Object[] obj = AlgoMedian.convertValueFreqListToArrays(inputList,
					freqList);
			Double[] v = (Double[]) obj[0];
			Integer[] f = (Integer[]) obj[1];
			int n = (Integer) obj[2];

			// find Q1
			switch (n % 4) {
			case 0:
				Q1.setValue((AlgoMedian.getValueAt(n / 4 - 1, v, f) + AlgoMedian
						.getValueAt((n + 4) / 4 - 1, v, f)) / 2);
				break;
			case 1:
				Q1.setValue((AlgoMedian.getValueAt((n - 1) / 4 - 1, v, f) + AlgoMedian
						.getValueAt((n + 3) / 4 - 1, v, f)) / 2);
				break;
			case 2:
				Q1.setValue(AlgoMedian.getValueAt((n + 2) / 4 - 1, v, f));
				break;
			default:
				Q1.setValue(AlgoMedian.getValueAt((n + 1) / 4 - 1, v, f));
				break;
			}

		}

      
       
       
    }

	// TODO Consider locusequability
}
