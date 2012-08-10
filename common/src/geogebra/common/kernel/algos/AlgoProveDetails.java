/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.factories.UtilFactory;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.main.App;
import geogebra.common.main.ProverSettings;
import geogebra.common.util.Prover;
import geogebra.common.util.Prover.NDGCondition;
import geogebra.common.util.Prover.ProverEngine;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

/**
 *
 * @author  Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoProveDetails extends AlgoElement {

    private GeoElement root;  // input
    private GeoList list;     // output
    private Boolean result;
    private HashSet<NDGCondition> ndgresult;
        
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
        initialCompute();
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
   
	/**
	 * Heavy computation of the proof 
	 */
	public final void initialCompute() {	
	
		// Create and initialize the prover 
		Prover p = UtilFactory.prototype.newProver();
        if ("OpenGeoProver".equalsIgnoreCase(ProverSettings.proverEngine)) {
        	if ("Wu".equalsIgnoreCase(ProverSettings.proverMethod))
        		p.setProverEngine(ProverEngine.OPENGEOPROVER_WU);
        	else if ("Area".equalsIgnoreCase(ProverSettings.proverMethod))
        		p.setProverEngine(ProverEngine.OPENGEOPROVER_AREA);
        }  	            
        else if ("Botana".equalsIgnoreCase(ProverSettings.proverEngine))
            p.setProverEngine(ProverEngine.BOTANAS_PROVER);
        else if ("Recio".equalsIgnoreCase(ProverSettings.proverEngine))
            p.setProverEngine(ProverEngine.RECIOS_PROVER);
        else if ("PureSymbolic".equalsIgnoreCase(ProverSettings.proverEngine))
            p.setProverEngine(ProverEngine.PURE_SYMBOLIC_PROVER);
        else if ("Auto".equalsIgnoreCase(ProverSettings.proverEngine))
            p.setProverEngine(ProverEngine.AUTO);
        p.setTimeout(ProverSettings.proverTimeout);
    	p.setConstruction(cons);
    	p.setStatement(root);
    	// Compute extra NDG's:
    	p.setReturnExtraNDGs(true);
    	
    	// Adding benchmarking:
    	Date date = new Date();
        long startTime = date.getTime();
    	p.compute(); // the computation of the proof
    	date = new Date();
    	long elapsedTime = date.getTime() - startTime;
    	App.debug("Benchmarking: " + elapsedTime + " ms");
    	
    	result = p.getYesNoAnswer();
    	ndgresult = p.getNDGConditions();
    	
    	App.debug("Statement is " + result);
    	
   
    		
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
	public void compute() {

		list.clear();
		list.setEuclidianVisible(false); // don't show in EV by default
		list.setDrawAsComboBox(true); // but if someone wants it, then prefer a
										// drop-down list
		if (result != null) {
			GeoBoolean answer = new GeoBoolean(cons);
			answer.setValue(result);
			list.add(answer);
			if (result) {
				GeoList ndgConditionsList = new GeoList(cons);
				ndgConditionsList.clear();
				ndgConditionsList.setDrawAsComboBox(true);
				Iterator<NDGCondition> it = ndgresult.iterator();
				TreeSet<GeoText> sortedSet = new TreeSet<GeoText>(
						GeoText.getComparator());

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
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		// TODO Consider locusequability
		return false;
	}

}
