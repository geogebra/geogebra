/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * Reverse a list. Adapted from AlgoSort
 * @author Michael Borcherds
 * @version 16-02-2008
 */

public class AlgoReverse extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
    private GeoList outputList; //output	
    private int size;

    /**
     * Creates new reverse list algo
     * @param cons
     * @param label
     * @param inputList
     */
    AlgoReverse(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    public String getClassName() {
        return "AlgoReverse";
    }

    protected void setInputOutput(){
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
    GeoList getResult() {
        return outputList;
    }

    protected final void compute() {
    	
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
    
}
