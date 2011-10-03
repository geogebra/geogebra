/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyBoolean;

/**
 * Take objects from the middle of a list
 * @author Michael Borcherds
 * @version 2008-03-04
 */

public class AlgoKeepIf extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
    private GeoList outputList; //output	
	private GeoFunction boolFun;     // input
    private int size;

    AlgoKeepIf(Construction cons, String label, GeoFunction boolFun, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
    	this.boolFun = boolFun;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    public String getClassName() {
        return "AlgoKeepIf";
    }

    protected void setInputOutput(){
        input = new GeoElement[2];
        input[0] = boolFun;
        input[1] = inputList;

        output = new GeoElement[1];
        output[0] = outputList;
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
    	
    	if (size == 0) return;
    	//<Zbynek Konecny 2010-05-13>
		/*
		 * If val is not numeric, we use the underlying Expression of the function and 
		 * plug the list element as variable.
		 * Deep copy is needed so that we can plug the value repeatedly.
		 */
    	FunctionVariable var = boolFun.getFunction().getFunctionVariable();
    	for (int i=0 ; i<size ; i++)
    	{
    		GeoElement geo = inputList.get(i);
    		if(geo.isGeoNumeric()){
				if (boolFun.evaluateBoolean(((GeoNumeric)geo).getValue()) ) outputList.add(geo.copyInternal(cons));; 
			} 
			else {
    			ExpressionNode ex = (ExpressionNode)boolFun.getFunction().getExpression().deepCopy(kernel);
    			ex.replaceAndWrap(var, geo.evaluate());
    			if (((MyBoolean)ex.evaluate()).getBoolean()) outputList.add(geo.copyInternal(cons));;
			}
				
    	}
    	//</Zbynek>
    } 	
  
}
