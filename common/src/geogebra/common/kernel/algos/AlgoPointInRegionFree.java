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
import geogebra.common.kernel.Region;
import geogebra.common.kernel.RegionParameters;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;


/**
 * Point in region algorithm
 * @author mathieu
 *
 */
public class AlgoPointInRegionFree extends AlgoPointInRegion {


    public AlgoPointInRegionFree(
        Construction cons,
        String label,
        Region region,
        NumberValue param1,
        NumberValue param2) {
    	
        super(cons, region);
        
        this.region = region;
        
        P = new GeoPoint(cons, region);

        this.param1 = param1;
        this.param2 = param2;
        

        setInputOutput(); // for AlgoElement

        
        compute();
        P.setLabel(label);
    }
    
    
    /**
     * create algo PointIn[ region, param1, param2 ] with param1, param2 calculated regarding (x,y)
     * @param cons
     * @param region
     * @param x
     * @param y
     */
    public AlgoPointInRegionFree(
            Construction cons,
            Region region,
            double x,
            double y) {
    	

    	super(cons, region);
    	this.region = region;

    	P = new GeoPoint(cons, region);
    	P.setCoords(x, y, 1.0);
    	region.pointChangedForRegion(P);

    	RegionParameters rp = P.getRegionParameters();
    	this.param1 = new GeoNumeric(cons, rp.getT1());
    	this.param2 = new GeoNumeric(cons, rp.getT2());


    	setInputOutput(); // for AlgoElement


    	compute();
    }

    @Override
	public Commands getClassName() {
        return Commands.FreePointIn;
    }



    @Override
	public final void compute() {
    	
    	//if point is not defined, try to use region parameters
    	if (!P.isDefined()){
    		super.compute();
    		return;
    	}
    	
      	
    	if (region.isDefined()) {	    	
	        region.pointChangedForRegion(P);
	        P.updateCoords();
    	} else {
    		P.setUndefined();
    	}
    }
    
}
