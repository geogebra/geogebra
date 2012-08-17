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
import geogebra.common.kernel.Path;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;




public class AlgoClosestPoint3D extends AlgoElement3D {

	private Path p;
	private GeoPointND P;
	
	private GeoPoint3D geoPointOnPath;

    public AlgoClosestPoint3D(Construction c,
    		String label, Path p, GeoPointND P) {
        super(c);
        this.p = p;
        this.P = P;
        geoPointOnPath = new GeoPoint3D(c);
        geoPointOnPath.setPath(p);
        setInputOutput(); // for AlgoElement

        // compute length
        compute();
        geoPointOnPath.setLabel(label);
    }
    

    @Override
	public Algos getClassName() {
        return Algos.AlgoClosestPoint3D;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement)p;
        input[1] = (GeoElement)P;

        super.setOutputLength(1);
        super.setOutput(0, (GeoElement3D)geoPointOnPath);
        setDependencies(); // done by AlgoElement
    }
 
    Path getInputPath() {
        return p;
    }
    GeoPointND getInputPoint() {
        return P;
    }

    public GeoPoint3D getOutputPoint() {
    	return geoPointOnPath;
    }
    
    @Override
	public void compute() {
    	//TODO: this is just copied from 2D version, not verified!
    	if (input[0].isDefined() && P.isDefined()) {	  
    		geoPointOnPath.setCoords(P);
	        p.pathChanged(geoPointOnPath);
	        geoPointOnPath.updateCoords();
    	} else {
    		geoPointOnPath.setUndefined();
    	}
    }

	


	

    
}
