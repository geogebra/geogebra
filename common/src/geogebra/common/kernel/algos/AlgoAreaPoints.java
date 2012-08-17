/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;

public class AlgoAreaPoints extends AlgoElement {

	private GeoPoint [] P;  // input
    private GeoNumeric area;     // output           
        
    public AlgoAreaPoints(Construction cons, String label, GeoPoint [] P) {       
        this(cons, P);
        area.setLabel(label);
    }   
    
    AlgoAreaPoints(Construction cons, GeoPoint [] P) {      
        super(cons); 
        this.P = P;
        area = new GeoNumeric(cons); 
        setInputOutput(); // for AlgoElement
        
        // compute angle
        compute();      
    }   
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoAreaPoints;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_AREA;
    }
        
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = P;
        
        setOutputLength(1);        
        setOutput(0,area);        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoNumeric getArea() { return area; }
    GeoPoint [] getPoints() { return P; }    
    
    // calc area of polygon P[0], ..., P[n]  
    // angle in range [0, pi]
    @Override
	public final void compute() {      
        area.setValue(GeoPolygon.calcArea(P)); 
    }

	// TODO Consider locusequability       
    
}
