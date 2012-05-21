/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.prover.Prover;
import geogebra.common.kernel.prover.Prover.NDGCondition;
import geogebra.common.kernel.prover.Prover.ProverEngine;
import geogebra.common.main.AbstractApplication;

/**
 *
 * @author  Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoProveDetails extends AlgoElement {

    private GeoElement root;  // input
    private GeoList list;     // output              
        
    /**
     * Proves the given statement and gives some details in a list
     * @param cons The construction
     * @param label Label for the output
     * @param root Input statement
     */
    public AlgoProveDetails(Construction cons, String label, GeoElement root) {
    	super(cons);
        this.root = root;  
        
        list = new GeoList(cons);
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        list.setLabel(label);
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
        super.setOutput(0, list);
        setDependencies(); // done by AlgoElement
    }    
    
    /**
     * Returns the output for the ProveDetails command
     * @return A list: {true/false, {array of NDGConditions}}
     */
    public GeoList getGeoList() { return list; }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {	
	
	// Create and initialize the prover 
     	Prover p = new Prover();
        if ("OpenGeoProver".equalsIgnoreCase(AbstractApplication.proverEngine))
            p.setProverEngine(ProverEngine.OPENGEOPROVER);
        else if ("Botana".equalsIgnoreCase(AbstractApplication.proverEngine))
            p.setProverEngine(ProverEngine.BOTANAS_PROVER);
        else if ("Recio".equalsIgnoreCase(AbstractApplication.proverEngine))
            p.setProverEngine(ProverEngine.RECIOS_PROVER);
        else if ("PureSymbolic".equalsIgnoreCase(AbstractApplication.proverEngine))
            p.setProverEngine(ProverEngine.PURE_SYMBOLIC_PROVER);
        else if ("Auto".equalsIgnoreCase(AbstractApplication.proverEngine))
            p.setProverEngine(ProverEngine.AUTO);
        p.setTimeout(AbstractApplication.proverTimeout);
    	p.setConstruction(cons);
    	p.setStatement(root);
    	
    	// Adding benchmarking:
    	Date date = new Date();
        long startTime = date.getTime();
    	p.compute(); // the computation of the proof
    	date = new Date();
    	long elapsedTime = date.getTime() - startTime;
    	AbstractApplication.debug("Benchmarking: " + elapsedTime + " ms");
    	
    	Boolean result = p.getYesNoAnswer();
    	AbstractApplication.debug("Statement is " + result);
    	
    	list.clear();
    	list.setEuclidianVisible(false); // don't show in EV by default
    	list.setDrawAsComboBox(true); // but if someone wants it, then prefer a drop-down list
    	if (result != null) {
			GeoBoolean answer = new GeoBoolean(cons); 
			answer.setValue(result);
			list.add(answer);
    		if (result) {
    			GeoList ndgConditionsList = new GeoList(cons);
    			ndgConditionsList.clear();
    			ndgConditionsList.setDrawAsComboBox(true);
    			Iterator<NDGCondition> it = p.getNDGConditions().iterator();
    			TreeSet<GeoText> sortedSet = new TreeSet<GeoText>(GeoText.getComparator()); 

    			// Collecting the set of NDG conditions:
    			while (it.hasNext()) {
    				GeoText ndgConditionText = new GeoText(cons);
    				NDGCondition ndgc = it.next();
    				String s = app.getCommand(ndgc.getCondition());
    				s += "[";
    				for (int i = 0; i < ndgc.getGeos().length; ++i) {
    					if (i > 0)
    						s += ',';
    					s += ndgc.getGeos()[i].getLabelSimple();
    				}
    				s += "]";
    				ndgConditionText.setTextString(s);
    				ndgConditionText.setLabelVisible(false);
    				ndgConditionText.setEuclidianVisible(false);
    				// For alphabetically ordering, we need a sorted set here:
    				sortedSet.add(ndgConditionText);
    			}
    			// Copy the sorted list into the output:
    			Iterator<GeoText> it2 = sortedSet.iterator();
    			while (it2.hasNext()) {
    				ndgConditionsList.add(it2.next());
    			}
    			
    			// Put this list to the final output (if non-empty):
    			if (ndgConditionsList.size() > 0)
    				list.add(ndgConditionsList);
    		}
    	}
    		
    }   
    @Override
    // Not sure how to do this hack normally. 
    final public String getCommandName(StringTemplate tpl) {
    	return "ProveDetails";
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

