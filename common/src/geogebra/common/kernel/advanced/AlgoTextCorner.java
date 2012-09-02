/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

// adapted from AlgoImageCorner by Michael Borcherds 2007-11-26

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;

public class AlgoTextCorner extends AlgoElement {

	private GeoText txt;  // input
    private GeoPoint corner;     // output    
    private NumberValue number;
    
    public AlgoTextCorner(Construction cons, String label, GeoText txt, NumberValue number) {        
        super(cons);
        this.txt = txt;   
        this.number = number;
        
        // make sure bounding box of text is kept up to date
        // so we can use it in compute()
        txt.setNeedsUpdatedBoundingBox(true);
    	txt.update(); 

        corner = new GeoPoint(cons);                
        setInputOutput(); // for AlgoElement  
           	
        compute();              
        corner.setLabel(label);     
    
        cons.registerEuclidianViewCE(this);
    }   
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoTextCorner;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = txt;        
        input[1] = number.toGeoElement();
        
        super.setOutputLength(1);
        super.setOutput(0, corner);
        setDependencies(); // done by AlgoElement
    }       
         
    public GeoPoint getCorner() { return corner; }        
    
    @Override
	public final void compute() {  
    	// determine bounding box size here
		txt.calculateCornerPoint(corner, (int) number.getDouble());	    	
    }    	   
    
    @Override
	public boolean euclidianViewUpdate() {
    	// update text to update it's bounding box
    	kernel.notifyUpdate(txt);
    	
    	// now compute()
    	compute();
    	
    	// update corner
    	corner.update();  
    	
    	return true; //update cascade of dependent objects done in Construction
    }
    
    @Override
	final public String toString(StringTemplate tpl) {
        return getCommandDescription(tpl);
    }

	// TODO Consider locusequability
	
}
