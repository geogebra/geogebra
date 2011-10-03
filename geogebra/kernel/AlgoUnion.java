/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

public class AlgoUnion extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
	private GeoList inputList2; //input
    private GeoList outputList; //output	
    private int size, size2;

    AlgoUnion(Construction cons, String label, GeoList inputList, GeoList inputList2) {
        super(cons);
        
        
        this.inputList = inputList;
        this.inputList2 = inputList2;

               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }


    public String getClassName() {
        return "AlgoUnion";
    }

    protected void setInputOutput(){
        input = new GeoElement[2];
        
	    input[0] = inputList;
	    input[1] = inputList2;

        output = new GeoElement[1];
        output[0] = outputList;
        setDependencies(); // done by AlgoElement
    }

    GeoList getResult() {
        return outputList;
    }

    protected final void compute() {
    	
    	size = inputList.size();
    	size2 = inputList2.size();
   	
    	if (!inputList.isDefined() || !inputList2.isDefined()) {
    		outputList.setUndefined();
    		return;
    	} 
       
    	outputList.setDefined(true);
    	outputList.clear();
    	
        for (int i=0 ; i < size ; i++)
        	addToOutputList(inputList.get(i));
    	
        for (int i=0 ; i < size2 ; i++) {
        	
        	boolean alreadyInList = false;
    		GeoElement geo = inputList2.get(i);
        	
        	for (int j = 0 ; j < size ; j++) {
        		if (inputList.get(j).isEqual(geo)) {
        			alreadyInList = true;
        			break;
        		}
        	}
        	
        	if (!alreadyInList) {
        		addToOutputList(geo);
        	}
        		
        		
        	}
        	
        }

    /*
     * checks not already in list
     */
    private void addToOutputList(GeoElement geo)
    {
		boolean alreadyInOutputList = false;
		for (int k = 0 ; k < outputList.size() ; k++)
		if (geo.isEqual(outputList.get(k))) {
			alreadyInOutputList = true;
			break;
		}
		
		if (!alreadyInOutputList) outputList.add(geo.copyInternal(cons));
    	
    }
  
}
