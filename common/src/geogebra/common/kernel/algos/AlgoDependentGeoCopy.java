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
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.geos.GeoElement;


/**
 * Creates a dependent copy of the given GeoElement.
 */
public class AlgoDependentGeoCopy extends AlgoElement implements DependentAlgo {

	private ExpressionNode origGeoNode;
    private GeoElement origGeo, copyGeo;     // input, ouput              
    
    /**
     * @param cons construction
     * @param label label for output
     * @param origGeo original element
     */
    public AlgoDependentGeoCopy(Construction cons, String label, GeoElement origGeo) {
    	super(cons);
    	this.origGeo = origGeo;
    	
    	// just for the toString() method
    	origGeoNode = new ExpressionNode(kernel, origGeo);
    	
        copyGeo = origGeo.copy();
        setInputOutput(); // for AlgoElement
        
        compute();      
        copyGeo.setLabel(label);
    }
    /**
     * @param cons construction
     * @param label label for output
     * @param origGeoNode original expression
     */
    public AlgoDependentGeoCopy(Construction cons, String label, ExpressionNode origGeoNode) {
    	super(cons);
    	this.origGeoNode = origGeoNode;
        origGeo = (GeoElement) origGeoNode.evaluate(StringTemplate.defaultTemplate);
        
        copyGeo = origGeo.copy();
        setInputOutput(); // for AlgoElement
        
        compute();      
        copyGeo.setLabel(label);
    }   
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoDependentGeoCopy;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = origGeo;
        
        setOutputLength(1);        
        setOutput(0,copyGeo);        
        setDependencies(); // done by AlgoElement
    }    
    
    /**
     * @return depedent copy of original geo
     */
    public GeoElement getGeo() { return copyGeo; }
    
    // copy geo
    @Override
	public final void compute() {	
    	try {
    		copyGeo.set(origGeo);
    	} catch (Exception e) {
    		copyGeo.setUndefined();
    	}
    }   
    
    @Override
	final public String toString(StringTemplate tpl) {
    	// we use the expression as it may add $ signs 
    	// to the label like $A$1
    	return origGeoNode.toString(tpl);
    }
	
	// TODO Consider locusequability
}
