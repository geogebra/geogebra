/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.kernel.Construction;
import geogebra.main.Application;


/**
 * 
 * @author  
 * @version 
 */
public class AlgoAxisStepX extends AlgoElement {

	protected GeoNumeric num;     // output          
        
    public AlgoAxisStepX(Construction cons, String label) {
    	super(cons);
        
        num = new GeoNumeric(cons); 
        setInputOutput(); // for AlgoElement
        
        // compute angle
        compute();     
            
        num.setLabel(label);
        
        // ensure we get updates
        cons.registerEuclidianViewCE(this);
   }   
    
	@Override
	public String getClassName() {
		return "AlgoAxisStepX";
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = new GeoElement[0];
        
        setOutputLength(1);        
        setOutput(0,num);        
        setDependencies(); // done by AlgoElement
    }    
	
    public GeoNumeric getResult() { return num; }        
 
    @Override
	public boolean euclidianViewUpdate() {

    	compute();
    	
    	// update num and all dependent elements
    	num.updateCascade(); 
    	return false;
   	
    }
    
    final public static boolean wantsEuclidianViewUpdate() {
    	return true;
    }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {  
    	double axisSteps[] = ((Application)kernel.getApplication()).getEuclidianView().getGridDistances();
    	num.setValue(axisSteps[0]);
    }         
}
