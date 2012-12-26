/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAngleConic.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;


/**
 * Converts a number to an angle.
 */
public class AlgoAngleNumeric extends AlgoElement {
    
    private GeoNumeric num;  // input
    private GeoAngle angle;     // output                  
    
    public AlgoAngleNumeric(Construction cons, String label, GeoNumeric num) { 
        super(cons);       
        this.num = num;                                                              
        angle = new GeoAngle(cons);                
        setInputOutput(); // for AlgoElement                
        compute();              
        angle.setLabel(label);            
    }   
    
    @Override
	public Commands getClassName() {
        return Commands.Angle;
    }
        
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = num;        
        
        setOutputLength(1);
        setOutput(0,angle);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoAngle getAngle() { return angle; }    
    GeoNumeric getNumber() { return num; }        
    
    // compute conic's angle
    @Override
	public final void compute() {                
        // copy number to angle
        angle.setValue(num.value);       
    }

	// TODO Consider locusequability
    
}
