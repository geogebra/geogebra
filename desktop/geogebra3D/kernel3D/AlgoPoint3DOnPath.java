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
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;


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
    
    
    

    @Override
	public Commands getClassName() {
        return Commands.Point;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
    	
    	input = new GeoElement[1];  	
        input[0] = path.toGeoElement();

        setOnlyOutput(P);
        setDependencies(); // done by AlgoElement
        
     }

    public GeoPoint3D getP() {
        return P;
    }
    
    Path getPath() {
        return path;
    }

    @Override
	public final void compute() {
    	
    	
    	if (input[0].isDefined()) {	    	
	        path.pathChanged(P);
	        P.updateCoords();
    	} else {
    		P.setUndefined();
    	}
    	
    }

    @Override
	final public String toString(StringTemplate tpl) {
        StringBuilder sb = new StringBuilder();
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("PointOnA",input[0].getLabel(tpl)));
        
        return sb.toString();
    }

	// TODO Consider locusequability
}
