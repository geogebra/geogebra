/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 * Returns the name of a GeoElement as a GeoText.
 * @author  Markus
 * @version 
 */
public class AlgoConstructionStep extends AlgoElement {

	private static final long serialVersionUID = 1L;
	//private GeoElement geo;  // input
    protected GeoNumeric num;     // output          
    //private Construction cons;
        
    public AlgoConstructionStep(Construction cons, String label) {
    	super(cons);
    	//this.cons=cons;
        //this.geo = geo;  
        
        num = new GeoNumeric(cons); 
        setInputOutput(); // for AlgoElement
        
        // compute angle
        compute();     
            
        num.setLabel(label);
   }   
    
	public String getClassName() {
		return "AlgoConstructionStep";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = new GeoElement[0];
        
        output = new GeoElement[1];        
        output[0] = num;        
        setDependencies(); // done by AlgoElement
    }    
	
    protected GeoNumeric getResult() { return num; }        
 
    final public boolean wantsConstructionProtocolUpdate() {
    	return true;
    }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {  
    	//double step=cons.getApplication().getConstructionProtocol().getCurrentStepNumber();
    	double step=kernel.getConstructionStep();
    	//Application.debug("compute"+step+" "+kernel.getConstructionStep());
    	num.setValue(step+1);
    	//num.setValue(cons.getStep());
    }         
}
