/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.common.kernel.AbstractConstruction;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.PathAlgo;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoPoint2;



public class AlgoClosestPoint extends AlgoElement implements PathAlgo {

	private Path path; // input
    private GeoPoint2 point; // input      
    private GeoPoint2 P; // output      

    public AlgoClosestPoint(AbstractConstruction cons,  Path path, GeoPoint2 point) {
    	super(cons);
        this.path = path;
        this.point = point;
        
        // create point on path and compute current location
        P = new GeoPoint2(cons);
        P.setPath(path);
		setInputOutput(); // for AlgoElement	       	        
		compute();		
	}
    
    public AlgoClosestPoint(AbstractConstruction cons, String label, Path path, GeoPoint2 point) {
    	this(cons,path,point);
      
		P.setLabel(label);
	}

	@Override
	public String getClassName() {
        return "AlgoClosestPoint";
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement)path.toGeoElement();
		input[1] = point.toGeoElement();    		
        setOutputLength(1);
        setOutput(0, P);
        setDependencies(); // done by AlgoElement
    }

    public GeoPoint2 getP() {
        return P;
    }
      
    @Override
	public final void compute() {
    	if (input[0].isDefined() && point.isDefined()) {	  
    		P.setCoords(point);
	        path.pathChanged(P);
	        P.updateCoords();
    	} else {
    		P.setUndefined();
    	}
    }

    @Override
	final public String toString() {
        return app.getPlain("PointOnAClosestToB", input[0].getLabel(), input[1].getLabel());
    }

	public boolean isChangeable() {
		return false;
	}
    
}
