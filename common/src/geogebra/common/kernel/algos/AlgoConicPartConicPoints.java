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
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;



/**
 * Arc or sector defined by a conic, start- and end-point.
 */
public class AlgoConicPartConicPoints extends AlgoConicPart {
	
	private GeoPoint startPoint, endPoint;
	
	// temp points
	private GeoPoint P, Q;	

    /**
     * Creates a new arc or sector algorithm.
     * The type is either GeoConicPart.CONIC_PART_ARC or 
     * GeoConicPart.CONIC_PART_ARC       
     */
    public AlgoConicPartConicPoints(Construction cons, String label,
    		GeoConic circle, GeoPoint startPoint, GeoPoint endPoint,
    		int type) {
        super(cons, type);
        conic = circle;
        this.startPoint = startPoint;
        this.endPoint = endPoint;                          
        
        // temp points
        P = new GeoPoint(cons);        
        Q = new GeoPoint(cons);
        P.setPath(conic);
        Q.setPath(conic);
        
        conicPart = new GeoConicPart(cons, type);

        setInputOutput(); // for AlgoElement      
        compute();
        setIncidence();
        
        conicPart.setLabel(label);
    }    	
    
    private void setIncidence() {
		// TODO Auto-generated method stub
		
	}

	public GeoPoint getStartPoint() {
    	return startPoint;
    }

    public GeoPoint getEndPoint() {
    	return endPoint;
    }
    
    public GeoConic getConic() {
    	return conic;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = conic;      
        input[1] = startPoint;
        input[2] = endPoint;

        super.setOutputLength(1);
        super.setOutput(0, conicPart);

        setDependencies();
    }
    
    @Override
	public final void compute() {
    	// the temp points P and Q should lie on the conic
    	P.setCoords(startPoint);
    	conic.pointChanged(P);
    	
    	Q.setCoords(endPoint);
    	conic.pointChanged(Q);
    	
    	// now take the parameters from the temp points
    	conicPart.set(conic);
    	    	   	    
    	conicPart.setParameters(P.getPathParameter().t, Q.getPathParameter().t, 
    			true);
    }

	// TODO Consider locusequability
    
}
