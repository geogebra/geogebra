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
 *
 * @author  Michael
 * @version 
 */
public class AlgoCountIf extends AlgoElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoFunction boolFun;     // input
	private GeoList list;
	private GeoNumeric result; // output
    
	/**
	 * Algorithm for handling of a CountIf construct
	 * @param cons
	 * @param label
	 * @param condition
	 * @param list
	 */        
    public AlgoCountIf(Construction cons, String label, 
    		GeoFunction boolFun, GeoList list) {
    	super(cons);
    	this.boolFun = boolFun;
        this.list = list;
      
        
        // create output GeoElement of same type as ifGeo
        result = new GeoNumeric(cons);
        
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        result.setLabel(label);
    }   
    
	public String getClassName() {
		return "AlgoCountIf";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
		input = new GeoElement[2];    	
        input[0] = boolFun;
        input[1] = list;
        
        output = new GeoElement[1];        
        output[0] = result;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoNumeric getResult() { return result; }
    
    protected final void compute() {	 
    	try {

    		int count = 0;
    		//<Zbynek Konecny 2010-04-15>
    		/*
    		 * If val is not numeric, we use the underlying Expression of the function and 
    		 * plug the list element as variable.
    		 * Deep copy is needed so that we can plug the value repeatedly.
    		 */
    		FunctionVariable var = boolFun.getFunction().getFunctionVariable();
    		
    		for (int i = 0 ; i < list.size() ; i++)
    		{
    			GeoElement val = list.get(i);
    			if(val.isGeoNumeric()){
    				if (boolFun.evaluateBoolean(((GeoNumeric)val).getValue()) ) count++; 
    			} 
    			else {
	    			ExpressionNode ex = (ExpressionNode)boolFun.getFunction().getExpression().deepCopy(kernel);
	    			ex.replaceAndWrap(var, val.evaluate());
	    			if (((MyBoolean)ex.evaluate()).getBoolean()) count++;
    			}
    		}
    		//</Zbynek>
    		result.setValue(count);
    	
    	    	
    	} catch (Exception e) {
    		result.setUndefined();
    	}
    }   
    
    final public String toString() {        
        return getCommandDescription();
    }
}
