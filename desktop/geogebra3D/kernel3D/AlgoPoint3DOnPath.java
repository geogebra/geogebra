/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Path;


public class AlgoPoint3DOnPath extends AlgoElement3D {

	
	private Path path; // input
    private GeoPoint3D P; // output       

    public AlgoPoint3DOnPath(
        Construction cons,
        String label,
        Path path,
        double x,
        double y,
        double z) {
        super(cons);
        this.path = path;
        P = new GeoPoint3D(cons, path);
        

        
        P.setCoords(x, y, z, 1.0);
        //P.setWillingCoords(x, y, z, 1);
        //path.pointChanged(P);

        setInputOutput(); // for AlgoElement

        // compute 
        compute();
        P.setLabel(label);
    }
    
    
    

    public String getClassName() {
        return "AlgoPoint3DOnPath";
    }

    // for AlgoElement
    protected void setInputOutput() {
    	
    	input = new GeoElement[1];  	
        input[0] = path.toGeoElement();

        output = new GeoElement[1];
        output[0] = P;
        setDependencies(); // done by AlgoElement
        
     }

    public GeoPoint3D getP() {
        return P;
    }
    
    Path getPath() {
        return path;
    }

    protected final void compute() {
    	
    	
    	if (input[0].isDefined()) {	    	
	        path.pathChanged(P);
	        P.updateCoords();
    	} else {
    		P.setUndefined();
    	}
    	
    }

    final public String toString() {
        StringBuilder sb = new StringBuilder();
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("PointOnA",input[0].getLabel()));
        
        return sb.toString();
    }
}
