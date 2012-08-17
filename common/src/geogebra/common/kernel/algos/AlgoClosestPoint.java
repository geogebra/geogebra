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
import geogebra.common.kernel.Path;
import geogebra.common.kernel.PathAlgo;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;



public class AlgoClosestPoint extends AlgoElement implements PathAlgo {

	private Path path; // input
    private GeoPoint point; // input      
    private GeoPoint P; // output      

    public AlgoClosestPoint(Construction cons,  Path path, GeoPoint point) {
    	super(cons);
        this.path = path;
        this.point = point;
        
        // create point on path and compute current location
        P = new GeoPoint(cons);
        P.setPath(path);
		setInputOutput(); // for AlgoElement	       	        
		compute();
		addIncidence();
	}
    
    public AlgoClosestPoint(Construction cons, String label, Path path, GeoPoint point) {
    	this(cons,path,point);
      
		P.setLabel(label);
	}

	@Override
	public Algos getClassName() {
        return Algos.AlgoClosestPoint;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = path.toGeoElement();
		input[1] = point.toGeoElement();    		
        setOutputLength(1);
        setOutput(0, P);
        setDependencies(); // done by AlgoElement
    }

    /**
     * @author Tam
     * 
     * for special cases of e.g. AlgoIntersectLineConic
     */
    private void addIncidence() {
    	P.addIncidence((GeoElement) path);
		
	}
    
    public GeoPoint getP() {
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
	final public String toString(StringTemplate tpl) {
        return app.getPlain("PointOnAClosestToB", input[0].getLabel(tpl), input[1].getLabel(tpl));
    }

	public boolean isChangeable() {
		return false;
	}

	// TODO Consider locusequability
    
}
