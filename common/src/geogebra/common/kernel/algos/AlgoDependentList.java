/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

import java.util.ArrayList;

/**
 *  Algorithm that takes a list of GeoElement objects to build a Geolist with them.
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class AlgoDependentList extends AlgoElement implements DependentAlgo {

	private ArrayList<GeoElement> listItems; //input GeoElements
    private GeoList geoList;     // output    
    
    private boolean isCellRange;
    private String cellRangeString;
        
    /**
     * Creates a new algorithm that takes a list of GeoElements to build a Geolist with 
     * them.
     * @param cons construction
     * @param label label for new list
     * @param listItems list of GeoElement objects
     */
    public AlgoDependentList(Construction cons, String label, ArrayList<GeoElement> listItems) {
    	this (cons, listItems, false);
    	geoList.setLabel(label);
    }
    
    /**
     * Creates an unlabeled algorithm that takes a list of GeoElements to build a Geolist with 
     * them.
     * @param cons
     * @param listItems list of GeoElement objects
     * @param isCellRange
     */
    public AlgoDependentList(Construction cons, ArrayList<GeoElement> listItems, boolean isCellRange) {
    	super(cons);
    	this.listItems = listItems;
    	this.isCellRange = isCellRange; 
    	   
    	// create output object
        geoList = new GeoList(cons);                                    	
    	setInputOutput(); 
        
        // compute value of dependent number
        compute();              
    }   
    
    @Override
	public Algos getClassName() {
		return Algos.Expression;
	}
    
    
    public void updateList(ArrayList<GeoElement> listItems){
    	this.listItems = listItems;
    	setInputOutput(); 
        compute();              
    }
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
    	// create input array from listItems array-list
    	// and fill the geoList with these objects
    	int size = listItems.size();
        input = new GeoElement[size];
    	for (int i=0; i < size; i++) {
    		input[i] = listItems.get(i);    
    		
    		if (!input[i].isLabelSet()) {
    			input[i].labelWanted = false;
    		}
    	}          
        
        setOutputLength(1);        
        setOutput(0,geoList);
        
        if(isCellRange){
        	setInputUpdateSetOnly();
        }else{
        	setDependencies(); // done by AlgoElement
        }
    }    
    
    /**
     * Call super.remove() to remove the list.
     * Then remove all unlabeled input objects (= list elements)
     */
    @Override
    public void remove() {
    	if(removed)
			return;
    	super.remove();
    	
		//  removing unlabeled input
		for (int i=0; i < input.length; i++) {
			if (!input[i].isLabelSet()) {				
				input[i].remove();
			}
		}    		
    		    	 	    
    }
    
    /**
     * Returns the list
     * @return the list as geo
     */
    public GeoList getGeoList() { 
    	return geoList; 
    }       
    
    @Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_CREATE_LIST;
}
    
    @Override
	public final void compute() {	    	
    	geoList.clear();
    	for (int i=0; i < input.length; i++) {    
    		// add input and its siblings to the list
    		// if the siblings are of the same type
    		
    		AlgoElement algo = input[i].getParentAlgorithm();
    		if (algo != null && algo.getOutputLength()>1 && algo.hasSingleOutputType()) {
    			// all siblings have same type: add them all
    			for (int k=0; k < algo.getOutputLength(); k++) {
    				GeoElement geo = algo.getOutput(k);
    				if ((geo == input[i] || geo.isDefined()) && !geoList.listContains(geo))
    			         geoList.add(geo);
    			}    			
    		} else {
    			// independent or mixed sibling types:
    			// add only this element    						
    			geoList.add(input[i]);
    		}
    	}
    }   
    
    
	public void setCellRangeString(String cellRangeString) {
		this.cellRangeString = cellRangeString;
	}

	private StringBuilder sb; 
	
    @Override
	final public String toString(StringTemplate tpl) {

    	if(isCellRange && cellRangeString != null){
    		return cellRangeString;
    	}
    	
        if (sb == null) sb = new StringBuilder();
        else sb.setLength(0);
    	sb.append("{");
    	
    	if(input.length > 0) { // Florian Sonner 2008-07-12
	    	for (int i=0; i < input.length - 1; i++) {
	    		sb.append(input[i].getLabel(tpl));
	    		sb.append(", ");
	    	}    	
	    	sb.append(input[input.length-1].getLabel(tpl));
    	}
    	sb.append("}");    		    	    	
        return sb.toString();
    }

	// TODO Consider locusequability
    
}
