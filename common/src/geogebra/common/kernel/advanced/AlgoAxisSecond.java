/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAxes.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec2D;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAxisSecond extends AlgoElement {
    
    private GeoConic c;  // input
    private GeoLine axis;     // output          
        
    private GeoVec2D [] eigenvec;    
    private GeoVec2D b;
    private GeoPoint P;
    
    public AlgoAxisSecond(Construction cons, String label,GeoConic c) {   
        super(cons);
        this.c = c;                               
        
        eigenvec = c.eigenvec;        
        b = c.b;                
        
        axis = new GeoLine(cons); 
        P = new GeoPoint(cons);
        axis.setStartPoint(P);
                       
        setInputOutput(); // for AlgoElement                
        compute();              
        axis.setLabel(label);            
    }   
    
    @Override
	public Commands getClassName() {
        return Commands.SecondAxis;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;        
        
        setOutputLength(1);
        setOutput(0,axis);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoLine getAxis() { return axis; }    
    GeoConic getConic() { return c; }        
    
    // calc axes
    @Override
	public final void compute() {                        
        // axes are lines with directions of eigenvectors
        // through midpoint b        
        
        axis.x = -eigenvec[1].getY();
        axis.y =  eigenvec[1].getX();
        axis.z = -(axis.x * b.getX() + axis.y * b.getY());       
        
        P.setCoords(b.getX(), b.getY(), 1.0);
    }
    
    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("SecondAxisOfA",c.getLabel(tpl));
    }

	// TODO Consider locusequability
}
