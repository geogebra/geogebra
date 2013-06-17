/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.kernel;


import geogebra.common.kernel.geos.GeoElement;

import java.util.HashSet;

/**
 * Construction for macros.
  */
public class MacroConstruction extends Construction {
	
	private Construction parentCons;
	private HashSet<String> reservedLabels;
	private boolean globalVariableLookup = false;
	
	/**
	 * Creates new macro construction
	 * @param kernel Kernel
	 */
	public MacroConstruction(MacroKernel kernel) {
		super(kernel, kernel.getParentKernel().getConstruction());
		parentCons = kernel.getParentKernel().getConstruction();
		reservedLabels = new HashSet<String>();
		//allow using reserved function names in marco constructions
		super.setFileLoading(true);
	}		   
	
	/**
	 * Set construction via XML string.	 
	 * @param xmlString XML string of the construction
	 * @throws Exception if there is a problem while reading XML
	 */
	public void loadXML(String xmlString) throws Exception {
		if (undoManager == null)
			undoManager = kernel.getApplication().getUndoManager(this);
		this.setFileLoading(true);
		undoManager.processXML(xmlString);
		this.setFileLoading(false);
	}
	
	/**
	 * Adds label to the list of reserved labels. Such labels
	 * will not be looked up in the parent construction in lookup();
	 * @param label reserved label
	 */
	public void addReservedLabel(String label) {
		if (label != null) {			
			reservedLabels.add(label);						
		}
	}
	
    /**
     * Returns a GeoElement for the given label. Note: 
     * construction index is ignored here. If no geo is found for
     * the specified label a lookup is made in the parent construction.
     * @return may return null
     */      	    	   
    @Override
	public final GeoElement lookupLabel(String label, boolean autoCreate) {//package private
    	if (label == null) return null;
    	
    	// local var handling
		if (localVariableTable != null) {        	
        	GeoElement localGeo = localVariableTable.get(label);        
            if (localGeo != null) return localGeo;
        }
    	    	       
        // global var handling        
        GeoElement geo = geoTableVarLookup(label);

        if (geo == null && globalVariableLookup && !isReservedLabel(label)) {
        	// try parent construction        	
        	 geo =  parentCons.lookupLabel(label, autoCreate); 
        }
        return geo;                   
    }
    
    private boolean isReservedLabel(String label) {
    	return reservedLabels.contains(label);        	
    }

    /**
     * Returns true if geos of parent costruction can be referenced
     * @return true if geos of parent costruction can be referenced
     */
	public boolean isGlobalVariableLookup() {
		return globalVariableLookup;
	}

	 /**
     * Set to true if geos of parent costruction should be referenced
     * @param globalVariableLookup true if geos of parent costruction should be referenced
     */
	public void setGlobalVariableLookup(boolean globalVariableLookup) {
		this.globalVariableLookup = globalVariableLookup;
	}
}
