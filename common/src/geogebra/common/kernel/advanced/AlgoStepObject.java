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

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;


/**
 * Returns the name of a GeoElement as a GeoText.
 * @author  Markus
 * @version 
 */
public class AlgoStepObject extends AlgoElement {

	private GeoElement inputGeo; //input
    protected GeoNumeric num;     // output          
    //private Construction cons;
        
    public AlgoStepObject(Construction cons, String label, GeoElement inputGeo) {
    	super(cons);
    	//this.cons=cons;
        this.inputGeo = inputGeo;  
        
        num = new GeoNumeric(cons); 
        setInputOutput(); // for AlgoElement
        
        compute();     
            
        num.setLabel(label);
   }   
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoStepObject;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = inputGeo;
           
        super.setOutputLength(1);
        super.setOutput(0, num);
        setDependencies(); // done by AlgoElement
    }    
	
    public GeoNumeric getResult() { return num; }        
 
    @Override
	final public boolean wantsConstructionProtocolUpdate() {
    	return true;
    }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {  
    	double step=inputGeo.getConstructionIndex();
    	num.setValue(step+1);
    }         

	// TODO Consider locusequability
}
