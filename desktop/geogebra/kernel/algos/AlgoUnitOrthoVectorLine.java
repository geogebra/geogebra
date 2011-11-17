/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoOrthoVectorLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.GeoVector;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoUnitOrthoVectorLine extends AlgoElement {
    
    private GeoLine g; // input
    private GeoVector  n;     // output       
    
    private double length;
        
    /** Creates new AlgoOrthoVectorLine */
    public AlgoUnitOrthoVectorLine(Construction cons, String label,GeoLine g) {        
        super(cons);       
        this.g = g;                
        n = new GeoVector(cons); 
        setInputOutput(); // for AlgoElement
        
        GeoPoint possStartPoint = g.getStartPoint();
        if (possStartPoint != null && possStartPoint.isLabelSet()) {
	        try{
	            n.setStartPoint(possStartPoint);
	        } catch (CircularDefinitionException e) {}
        }
        
        // compute line through P, Q
        n.z = 0.0d;
        compute();      
        n.setLabel(label);
    }   
    
    @Override
	public String getClassName() {
        return "AlgoUnitOrthoVectorLine";
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];        
        input[0] = g;
         
        super.setOutputLength(1);
        super.setOutput(0, n);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoVector getVector() { return n; }    
    GeoLine getg() { return g; }
    
    // line through P normal to v
    @Override
	public final void compute() {     
        length = GeoVec2D.length(g.x, g.y);
        n.x = g.x / length;
        n.y = g.y / length;        
    }   
    
    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("UnitVectorPerpendicularToA",g.getLabel());
    }
}
