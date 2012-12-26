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
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

public class AlgoAppend extends AlgoElement {

	private GeoList inputList; //input
	private GeoElement geo; //input
    private GeoList outputList; //output	
    private int size;
    private int order;
    private int ADD_OBJECT_AT_START = 0;
    private int ADD_OBJECT_AT_END   = 1;

    public AlgoAppend(Construction cons, String label, GeoList inputList, GeoElement geo) {
        super(cons);
        
        order = ADD_OBJECT_AT_END;
        
        this.inputList = inputList;
        this.geo = geo;

               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    public AlgoAppend(Construction cons, String label, GeoElement geo, GeoList inputList) {
        super(cons);
        
        order = ADD_OBJECT_AT_START;
        
        this.inputList = inputList;
        this.geo = geo;

               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    @Override
	public Commands getClassName() {
        return Commands.Append;
    }

    @Override
	protected void setInputOutput(){

    	// make sure that x(Element[list,1]) will work even if the output list's length is zero
    	outputList.setTypeStringForXML(inputList.getTypeStringForXML());  	
        
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
    	
        if (order == ADD_OBJECT_AT_START) {
        	outputList.add(geo.copyInternal(cons));
        }
        for (int i=0 ; i < size ; i++) {
    		outputList.add(inputList.get(i).copyInternal(cons));
        }
        if (order == ADD_OBJECT_AT_END) {
        	outputList.add(geo.copyInternal(cons));
        }
   }

	// TODO Consider locusequability
  
}
