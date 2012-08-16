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
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

public class AlgoImageCorner extends AlgoElement {
    
    private GeoImage img;  // input
    private GeoPoint corner;     // output    
    private NumberValue number;
    
    public AlgoImageCorner(Construction cons, String label, GeoImage img, NumberValue number) {        
        super(cons);
        this.img = img;   
        this.number = number;
        
        corner = new GeoPoint(cons);                
        setInputOutput(); // for AlgoElement                
        compute();              
        corner.setLabel(label);           
    }   
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoImageCorner;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = img;        
        input[1] = number.toGeoElement();
              
        super.setOutputLength(1);
        super.setOutput(0, corner);
        setDependencies(); // done by AlgoElement
    }       
         
    public GeoPoint getCorner() { return corner; }        
    
    @Override
	public final void compute() {         	
		img.calculateCornerPoint(corner, (int) number.getDouble());	    	
    }
    
    @Override
	final public String toString(StringTemplate tpl) {
        return getCommandDescription(tpl);
    }

	// TODO Consider locusequability
	
}
