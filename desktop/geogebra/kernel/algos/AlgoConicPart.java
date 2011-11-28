/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoConic;
import geogebra.kernel.geos.GeoConicPart;


/**
 * Super class for all algorithms creating
 * conic arcs or sectors.
 */
public abstract class AlgoConicPart extends AlgoElement {

    public GeoConic conic; // input
    public NumberValue startParam; // input
	public NumberValue endParam;
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
    
	@Override
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
   
    @Override
	public void compute() {    	    	
    	conicPart.set(conic);
    	conicPart.setParameters(startParam.getDouble(), endParam.getDouble(), true);
    }

    
	@Override
	public String toString() {
		return getCommandDescription();
	}

}
