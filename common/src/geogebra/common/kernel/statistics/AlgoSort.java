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
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;

import java.util.Iterator;
import java.util.TreeSet;


/**
 * Sort a list. Adapted from AlgoMax and AlgoIterationList
 * @author Michael Borcherds
 * @version 04-01-2008
 */

public class AlgoSort extends AlgoElement {

	private GeoList inputList; //input
    private GeoList outputList; //output	
    private int size;
	private GeoList valueList;

    /**
     * Creates new list sorting algorithm
     * @param cons construction
     * @param label label for output
     * @param inputList list to be sorted
     */
    public AlgoSort(Construction cons, String label, GeoList inputList) {
        this(cons, inputList);
        outputList.setLabel(label);
    }
    
    /**
     * Creates new list sorting algorithm
     * @param cons construction
     * @param label label for output
     * @param valueList list of values that shall be sorted
     * @param inputList list of keys which determines order of values in sorted list
     */
    public AlgoSort(Construction cons, String label, GeoList valueList,GeoList inputList) {
        this(cons, inputList,valueList);
        outputList.setLabel(label);
    }

    /**
     * Creates new list sorting algorithm
     * @param cons construction
     * @param inputList list of keys which determines order of values in sorted list
     * @param valueList list of values that shall be sorted
     */
    public AlgoSort(Construction cons, GeoList inputList,GeoList valueList) {
        super(cons);
        this.inputList = inputList;
        this.valueList = valueList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
    }

    /**
     * Creates new list sorting algorithm
     * @param cons construction
     * @param inputList to be sorted
     * */
    public AlgoSort(Construction cons, GeoList inputList) {
		this(cons, inputList, (GeoList) null);
	}

	@Override
	public Algos getClassName() {
        return Algos.AlgoSort;
    }

    @Override
	protected void setInputOutput(){

    	// make sure that x(Element[list,1]) will work even if the output list's length is zero
    	outputList.setTypeStringForXML(inputList.getTypeStringForXML());  	

    	input = new GeoElement[valueList==null?1:2];
        if(valueList!=null) {
        	input[0] = valueList;
        	input[1] = inputList;
        } else {
            input[0] = inputList;        	
        }
        super.setOutputLength(1);
        super.setOutput(0, outputList);
        setDependencies(); // done by AlgoElement
    }

    /**
     * @return sorted list
     */
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
    	
    	if (valueList!= null && (!valueList.isDefined() ||  valueList.size() != size)) {
    		outputList.setUndefined();
    		return;
    	} 

    	GeoElement geo0 = inputList.get(0); 
    	
    	Class<? extends GeoElement> geoClass = geo0.getClass();
    	
    	TreeSet<GeoElement> sortedSet;
    	
    	if (geo0.isGeoPoint()) {
    		sortedSet = new TreeSet(GeoPoint.getComparatorX());
    		
    	} else if (geo0.isGeoText()) {
    		sortedSet = new TreeSet(GeoText.getComparator());
    		
    	} else if (geo0.isNumberValue()) {
    		sortedSet = new TreeSet(GeoNumeric.getComparator());
    		
    	} else {
    		outputList.setUndefined();
    		return;    		
    	}
    	
    	
        // copy inputList into treeset
        for (int i=0 ; i<size ; i++)
        {
	      	GeoElement geo = inputList.get(i); 
	   		if (geo.getClass().equals(geoClass)) {
	   			sortedSet.add(geo);
	   		}
	   		else
	   		{
	   		  outputList.setUndefined();
	       	  return;			
	   		}
        }
        
        // copy the sorted treeset back into a list
        outputList.setDefined(true);
        outputList.clear();
        
        Iterator<GeoElement> iterator = sortedSet.iterator();
        if(valueList==null){
        while (iterator.hasNext()) {
     	   outputList.add((iterator.next()));
        }      
        }else{
        	while (iterator.hasNext()) {
        		int pos =inputList.find(iterator.next());
          	   	outputList.add(valueList.get(pos));
             }	
        }
    }

	// TODO Consider locusequability
  
}
