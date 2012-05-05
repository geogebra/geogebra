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
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.prover.Prover;
import geogebra.common.kernel.prover.Prover.ProverEngine;

/**
 *
 * @author  Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoProve extends AlgoElement {

    private GeoElement root;  // input
    private GeoBoolean bool;     // output              
        
    public AlgoProve(Construction cons, String label, GeoElement root) {
    	super(cons);
        this.root = root;  
        
        bool = new GeoBoolean(cons);
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        bool.setLabel(label);
    }   
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoDependentBoolean;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = root;
        
        super.setOutputLength(1);
        super.setOutput(0, bool);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoBoolean getGeoBoolean() { return bool; }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {	
    	Prover p = new Prover();
    	p.setConstruction(cons);
    	p.setStatement(root);
    	p.setProverEngine(ProverEngine.RECIOS_PROVER);
    	p.compute();
    	Boolean result = p.getYesNoAnswer();
    	if (result != null)
    		bool.setValue(result);
    	
    }   
    @Override
    // Not sure how to do this hack normally. 
    final public String getCommandName(StringTemplate tpl) {
    	return "Prove";
    }
    
    @Override
	final public String toString(StringTemplate tpl) {
    	return getCommandDescription(tpl);
   
    	
    }
    
    @Override
	public void update(){
    	//do not redo the prove
    }
}

