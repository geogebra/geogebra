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
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;




/**
 * Computes the area of a polygon
 * @author  mathieu
 * @version 
 */
public class AlgoAreaPolygon extends AlgoElement {

	private GeoPolygon  polygon;  // input
    private GeoNumeric area;     // output           
        
    public AlgoAreaPolygon(Construction cons, String label, GeoPolygon  polygon) {       
	  super(cons); 
      this.polygon = polygon;
      area = new GeoNumeric(cons); 
      setInputOutput(); // for AlgoElement
      
      // compute angle
      compute();     
          
      area.setLabel(label);
    }   
  
    @Override
	public Algos getClassName() {
        return Algos.AlgoAreaPolygon;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_AREA;
    }   
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input =  new GeoElement[1];
        input[0] = polygon;
        
        setOutputLength(1);        
        setOutput(0, area);        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoNumeric getArea() { return area; }        
    
    // calc area of conic c 
    @Override
	public final void compute() { 
    	/*
    	if (!polygon.isDefined()) {
    		area.setUndefined();
    		return;
    	}
      	*/
    	
    	area.setValue(polygon.getArea());
    }

	// TODO Consider locusequability   
    
    
}
