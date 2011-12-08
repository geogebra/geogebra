/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.AbstractConstruction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoConicInterface;
import geogebra.common.kernel.geos.GeoConicPartInterface;


/**
 * Super class for all algorithms creating
 * conic arcs or sectors.
 */
public abstract class AlgoConicPart extends AlgoElement {

    public GeoConicInterface conic; // input
    public NumberValue startParam; // input((Construction) 
	public NumberValue endParam;
    public GeoConicPartInterface conicPart; // output//package private   
    
    public int type;//package private

    /**
     * Creates a new arc or sector algorithm.
     * The type is either GeoConicPart.CIRCLE_ARC or 
     * GeoConicPart.CIRCLE_ARC.CIRCLE_SECTOR         
     */
    public AlgoConicPart(AbstractConstruction cons, int type) {//package private
        super(cons);        
        this.type = type;     
    }
    
	@Override
	public String getClassName() {
		switch (type) {
			case GeoConicPartInterface.CONIC_PART_ARC:
				return "AlgoConicArc";
			default:
				return "AlgoConicSector";
		}		
	}

    public GeoConicPartInterface getConicPart() {
        return conicPart;
    }
   
    @Override
	public void compute() {
    	conicPart.set((GeoElement)conic);//TODO:remove typecast
    	conicPart.setParameters(startParam.getDouble(), endParam.getDouble(), true);
    }

    
	@Override
	public String toString() {
		return getCommandDescription();
	}

}
