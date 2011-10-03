/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import java.awt.Point;


/**
 * Returns the Column of a GeoElement.
 * @author  Michael
 * @version 
 */
public class AlgoColumn extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoElement geo;  // input
    private GeoNumeric num;     // output              
        
    public AlgoColumn(Construction cons, String label, GeoElement geo) {
    	super(cons);
        this.geo = geo;  
        
        num = new GeoNumeric(cons); 
       setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        num.setLabel(label);
    }   
    
	public String getClassName() {
		return "AlgoColumn";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = geo;
        
        output = new GeoElement[1];        
        output[0] = num;        
        setDependencies(); // done by AlgoElement
    }    
    
    protected GeoNumeric getResult() { return num; }        
    
    // calc the current value of the arithmetic tree
    protected final void compute() {    	
    	Point p = geo.getSpreadsheetCoords();
    	if (p != null) num.setValue(p.x + 1);
    	else num.setUndefined();
    }         
}
