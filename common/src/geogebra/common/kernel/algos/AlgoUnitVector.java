/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoUnitVectorVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;


/**
 *
 * @author  Markus
 * @version 
 */
public abstract class AlgoUnitVector extends AlgoElement {
    
	protected GeoElement inputGeo; // input
    protected GeoVectorND  u;     // output       
    
    protected double length;
        
    /** Creates new AlgoOrthoVectorVector */
    public AlgoUnitVector(Construction cons, String label,GeoElement inputGeo) {        
        super(cons);    
        
        this.inputGeo = inputGeo;
        u = createVector(cons); 
        
        GeoPointND possStartPoint = getInputStartPoint();
        if (possStartPoint != null && possStartPoint.isLabelSet()) {
	        try{
	            u.setStartPoint(possStartPoint);
	        } catch (CircularDefinitionException e) {}
        }
        
        setInputOutput(); // for AlgoElement
        
        compute();      
        u.setLabel(label);
    } 
    
    /**
     * 
     * @param cons construction
     * @return new vector
     */
    abstract protected GeoVectorND createVector(Construction cons);
    
    abstract protected GeoPointND getInputStartPoint();
    
    
    // for AlgoElement
	protected void setInputOutput() {
        input = new GeoElement[1];        
        input[0] = inputGeo;
        
        super.setOutputLength(1);
        super.setOutput(0, (GeoElement) u);
        setDependencies(); // done by AlgoElement
    }    
    
    @Override
	public Commands getClassName() {
        return Commands.UnitVector;
    }
    
    public GeoVectorND getVector() { return u; }  
    
    
    
    
    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
    	return loc.getPlain("UnitVectorOfA", inputGeo.getLabel(tpl));
    }

}
