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
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;

import java.util.Arrays;


/**
 * Create a dot plot. 
 * 
 * Input: list of unsorted raw numeric data 
 * Output: sorted list of points forming a dot plot of the raw data
 * 
 * A dot plot is a set of points for which:
 *  x coordinates = values from a list of numeric data
 *  y coordinates = number of times the x data value has occurred 
 *  
 *  example:
 *      raw data = { 5,11,12,12,12,5 }
 *      dot plot = { (5,1), (5,2), (11,1), (12,1), (12,2), (12,3) }
 *  
 * Adapted from AlgoSort and AlgoPointList
 * @author G.Sturr
 * @version 2010-8-10
 */

public class AlgoDotPlot extends AlgoElement {

	private GeoList inputList; //input
    private GeoList outputList; //output	
    private int size;
	private double[] sortedData;

    public AlgoDotPlot(Construction cons, String label, GeoList inputList) {
        
    	this(cons, inputList);
    	outputList.setLabel(label);
        
    }

    public AlgoDotPlot(Construction cons, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        
    }

    
    @Override
	public Algos getClassName() {
        return Algos.AlgoDotPlot;
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputList;

        setOutputLength(1);
        setOutput(0,outputList);
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return outputList;
    }

    @Override
	public final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size == 0) {
    		outputList.setUndefined();
    		return;
    	} 

    	//========================================
    	// sort the raw data
    	
    	// convert geoList to sorted array of double
		sortedData = new double[size];
		for (int i=0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isNumberValue()) {
				NumberValue num = (NumberValue) geo;
				sortedData[i] = num.getDouble();

			} else {
				outputList.setUndefined();
				return;
			}    		    		
		}   
		Arrays.sort(sortedData);
	       
        
		// prepare output list. Pre-existing geos will be recycled, 
		// but extra geos are removed when outputList is too long
		outputList.setDefined(true);
		for (int i = outputList.size() - 1; i >= size; i--) {
			GeoElement extraGeo = outputList.get(i);
			extraGeo.remove();
			outputList.remove(extraGeo);			
		}	
		
		int oldListSize = outputList.size();
    	 
                     
        //========================================
        // iterate through the sorted data and 
        // create dot plot points
        boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

        double k = 1.0;
        
        // first point
        if(outputList.size()>0)
			((GeoPoint)outputList.get(0)).setCoords(sortedData[0], k, 1.0);
    	else
    	 outputList.add(new GeoPoint(cons, null, sortedData[0], k, 1.0));
        
        // remaining points
        for(int i = 1; i< size; i++){
        	// stack repeated values
        	if(sortedData[i] == sortedData[i-1]) 
        		++k;
        	else
        		k = 1;
        	if(i<oldListSize)
				((GeoPoint)outputList.get(i)).setCoords(sortedData[i], k, 1.0);
        	else
        	 outputList.add(new GeoPoint(cons, null, sortedData[i], k, 1.0));
        }      
		
        cons.setSuppressLabelCreation(suppressLabelCreation); 
    }

	// TODO Consider locusequability
  
}
