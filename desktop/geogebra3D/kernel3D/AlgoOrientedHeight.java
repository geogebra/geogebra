/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoRadius.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.HasHeight;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoOrientedHeight extends AlgoElement {
    
    private HasHeight c;  // input
    private GeoNumeric num;     // output                  
    
    public AlgoOrientedHeight(Construction cons, HasHeight c) {        
        super(cons);
        this.c = c;                                                              
        num = new GeoNumeric(cons);                
        setInputOutput(); // for AlgoElement                
        compute();                     
    }   
    
    public AlgoOrientedHeight(Construction cons, String label,HasHeight c) {        
        this(cons,c);    
        num.setLabel(label);            
    }   
    
    @Override
	public Commands getClassName() {
        return Commands.OrientedHeight;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = (GeoElement) c;        
        
        super.setOutputLength(1);
        super.setOutput(0, num);
        setDependencies(); // done by AlgoElement
    }       
    
    public GeoNumeric getOrientedHeight() { return num; } 
    
    // set parameter of parabola
    @Override
	public final void compute() {
    	if (!((GeoElement) c).isDefined()){
    		num.setUndefined();
    	}else{
            num.setValue(c.getOrientedHeight());
    	}
    }
    
    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return loc.getPlain("OrientedHeightOfA",((GeoElement) c).getLabel(tpl));
    }

}
