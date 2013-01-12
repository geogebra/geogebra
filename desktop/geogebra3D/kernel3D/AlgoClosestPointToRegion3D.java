/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;




public class AlgoClosestPointToRegion3D extends AlgoElement3D {

	private Region r;
	private GeoPointND P;
	
	private GeoPoint3D geoPointOnRegion;

    public AlgoClosestPointToRegion3D(Construction c,
    		String label, Region r, GeoPointND P) {
        super(c);
        this.r = r;
        this.P = P;
        geoPointOnRegion = new GeoPoint3D(c);
        geoPointOnRegion.setRegion(r);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        geoPointOnRegion.setLabel(label);
    }
    

    @Override
	public Commands getClassName() {
		return Commands.ClosestPoint;
	}
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement)r;
        input[1] = (GeoElement)P;

        super.setOutputLength(1);
        super.setOutput(0, (GeoElement3D)geoPointOnRegion);
        setDependencies(); // done by AlgoElement
    }
 
    Region getInputRegion() {
        return r;
    }
    GeoPointND getInputPoint() {
        return P;
    }

    public GeoPoint3D getOutputPoint() {
    	return geoPointOnRegion;
    }
    
    @Override
	public void compute() {
    	//TODO: this is just copied from 2D version, not verified!
    	if (input[0].isDefined() && P.isDefined()) {	  
    		geoPointOnRegion.setCoords(P);
	        r.regionChanged(geoPointOnRegion);
	        geoPointOnRegion.updateCoords();
    	} else {
    		geoPointOnRegion.setUndefined();
    	}
    }

	


	

    
}
