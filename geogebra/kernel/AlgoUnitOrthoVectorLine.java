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

package geogebra.kernel;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoUnitOrthoVectorLine extends AlgoElement {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoLine g; // input
    private GeoVector  n;     // output       
    
    private double length;
        
    /** Creates new AlgoOrthoVectorLine */
    AlgoUnitOrthoVectorLine(Construction cons, String label,GeoLine g) {        
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
    
    public String getClassName() {
        return "AlgoUnitOrthoVectorLine";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];        
        input[0] = g;
        
        output = new GeoElement[1];        
        output[0] = n;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoVector getVector() { return n; }    
    GeoLine getg() { return g; }
    
    // line through P normal to v
    protected final void compute() {     
        length = GeoVec2D.length(g.x, g.y);
        n.x = g.x / length;
        n.y = g.y / length;        
    }   
    
    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("UnitVectorPerpendicularToA",g.getLabel());

    }
}
