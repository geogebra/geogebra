/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * Take objects from the middle of a list
 * @author Michael Borcherds
 * @version 2008-03-04
 */

public class AlgoKeepIf extends AlgoElement {

	private GeoList inputList; //input
    private GeoList outputList; //output	
	private GeoFunction boolFun;     // input
    private int size;

    public AlgoKeepIf(Construction cons, String label, GeoFunction boolFun, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
    	this.boolFun = boolFun;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoKeepIf";
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[2];
        input[0] = boolFun;
        input[1] = inputList;

        super.setOutputLength(1);
        super.setOutput(0, outputList);
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
