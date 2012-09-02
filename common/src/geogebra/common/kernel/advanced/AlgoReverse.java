/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * Reverse a list. Adapted from AlgoSort
 * @author Michael Borcherds
 * @version 16-02-2008
 */

public class AlgoReverse extends AlgoElement {

	private GeoList inputList; //input
    private GeoList outputList; //output	
    private int size;

    /**
     * Creates new reverse list algo
     * @param cons
     * @param label
     * @param inputList
     */
    public AlgoReverse(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoReverse;
    }

    @Override
	protected void setInputOutput(){

    	// make sure that x(Element[list,1]) will work even if the output list's length is zero
    	outputList.setTypeStringForXML(inputList.getTypeStringForXML());  	

    	input = new GeoElement[1];
        input[0] = inputList;

        setOutputLength(1);
        setOutput(0, outputList);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the reversed list
     * @return reversed list
     */
    public GeoList getResult() {
        return outputList;
    }

    @Override
	public final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined()) {
    		outputList.setUndefined();
    		return;
    	} 
       
        outputList.setDefined(true);
        outputList.clear();
        
        if (size==0) return; // return empty list
        
 	    for (int i=0 ; i<size ; i++)
 	    {
 	    	// need to copy elements like eg {(1,1)} so the properties can be set independently
 	    	GeoElement geo = inputList.get(size-1-i);
 	    	if (!geo.isLabelSet()) {
 	    		geo = geo.copyInternal(cons);
 	    	}
 	    	
     	   outputList.add(geo);
        }      
     }

	// TODO Consider locusequability
    
}
