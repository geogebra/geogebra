/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;


/**
 * Super class for all algorithms creating
 * conic arcs or sectors.
 */
public abstract class AlgoConicPart extends AlgoElement {

    GeoConic conic; // input
    NumberValue startParam, endParam; // input
    GeoConicPart conicPart; // output   
    
    int type;

    /**
     * Creates a new arc or sector algorithm.
     * The type is either GeoConicPart.CIRCLE_ARC or 
     * GeoConicPart.CIRCLE_ARC.CIRCLE_SECTOR         
     */
    AlgoConicPart(Construction cons, int type) {
        super(cons);        
        this.type = type;     
    }
    
	public String getClassName() {
		switch (type) {
			case GeoConicPart.CONIC_PART_ARC:
				return "AlgoConicArc";
			default:
				return "AlgoConicSector";
		}		
	}

    public GeoConicPart getConicPart() {
        return conicPart;
    }
   
    protected void compute() {    	    	
    	conicPart.set(conic);
    	conicPart.setParameters(startParam.getDouble(), endParam.getDouble(), true);
    }

    
	public String toString() {
		return getCommandDescription();
	}

}
