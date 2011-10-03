/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

public class AlgoAppend extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
	private GeoElement geo; //input
    private GeoList outputList; //output	
    private int size;
    private int order;
    private int ADD_OBJECT_AT_START = 0;
    private int ADD_OBJECT_AT_END   = 1;

    AlgoAppend(Construction cons, String label, GeoList inputList, GeoElement geo) {
        super(cons);
        
        order = ADD_OBJECT_AT_END;
        
        this.inputList = inputList;
        this.geo = geo;

               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    AlgoAppend(Construction cons, String label, GeoElement geo, GeoList inputList) {
        super(cons);
        
        order = ADD_OBJECT_AT_START;
        
        this.inputList = inputList;
        this.geo = geo;

               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    public String getClassName() {
        return "AlgoAppend";
    }

    protected void setInputOutput(){
        input = new GeoElement[2];
        
        if (order == ADD_OBJECT_AT_END) {
	        input[0] = inputList;
	        input[1] = geo;
        } else { // ADD_OBJECT_AT_START
            input[0] = geo;        	
            input[1] = inputList;
        }

        setOutputLength(1);
        setOutput(0,outputList);
        setDependencies(); // done by AlgoElement
    }

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
    	
        if (order == ADD_OBJECT_AT_START) 
        	outputList.add(geo.copyInternal(cons));

        for (int i=0 ; i < size ; i++)
    		outputList.add(inputList.get(i).copyInternal(cons));
    	
        if (order == ADD_OBJECT_AT_END) 
        	outputList.add(geo.copyInternal(cons));

   }
  
}
