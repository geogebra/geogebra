/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.kernel.geos.GeoText;


/**
 * Returns the name of a GeoElement as a GeoText.
 * @author  Markus
 * @version 
 */
public class AlgoColumnName extends AlgoElement {

	private GeoElement geo;  // input
    private GeoText text;     // output              
        
    public AlgoColumnName(Construction cons, String label, GeoElement geo) {
    	super(cons);
        this.geo = geo;  
        
       text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        text.setLabel(label);
    }   
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoColumnName;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = geo;
          
        super.setOutputLength(1);
        super.setOutput(0, text);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoText getGeoText() { return text; }
    
    // calc the current value of the arithmetic tree
    @Override
	public final void compute() {    
    	String col = GeoElementSpreadsheet.getSpreadsheetColumnName(geo.getLabel(StringTemplate.defaultTemplate));
    	
    	if (col == null) text.setUndefined();
    	else text.setTextString(col);	    	
    }

	// TODO Consider locusequability         
}
