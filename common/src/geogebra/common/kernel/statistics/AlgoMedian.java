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

	
	private GeoList inputList; //input
    private GeoNumeric median; //output	
    private int size;

    public AlgoMedian(Construction cons, String label, GeoList inputList) {
    	this(cons, inputList);
        median.setLabel(label);
    }

    public AlgoMedian(Construction cons, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
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
        input = new GeoElement[1];
        input[0] = inputList;

        setOnlyOutput(median);
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getMedian() {
        return median;
    }

    @Override
	public final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size == 0) {
    		median.setUndefined();
    		return;
    	} 
       
    	
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
			median.setUndefined();
    		return;			
		 }
       }
       
       // do the sorting
       Arrays.sort(sortList);
       
       if (Math.floor((double)size/2)==size/2.0)
       {
    	 median.setValue((sortList[size/2]+sortList[size/2-1])/2);  
       }
       else
       {
    	 median.setValue(sortList[(size-1)/2]);   
       }
      
    }
}
