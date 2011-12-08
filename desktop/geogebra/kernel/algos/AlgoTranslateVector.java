/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoTranslatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.kernel.Construction;


/**
 * Vector w = v starting at A
 * @author  Markus
 * @version 
 */
public class AlgoTranslateVector extends AlgoElement {

    private GeoPoint2 A;   // input
    private GeoVec3D v;  // input
    private GeoVector w;     // output        
            
    public AlgoTranslateVector(Construction cons, String label,  GeoVec3D v, GeoPoint2 A) {
        super(cons);
        this.A = A;        
        this.v = v;
        
        // create new Point
        w = new GeoVector(cons);  
        
        try {     
            w.setStartPoint(A);
        } catch (CircularDefinitionException e) {}
        
        setInputOutput();
                
        compute();        
        w.setLabel(label);
    }           
    
    @Override
	public String getClassName() {
        return "AlgoTranslateVector";
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_TRANSLATE_BY_VECTOR;
    }
   
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = v;        
        input[1] = A;        
        
        setOutputLength(1);        
        setOutput(0,w);        
        setDependencies(); // done by AlgoElement
    }           
        
    GeoPoint2 getPoint() { return A; }
    GeoVec3D getVector() { return v; }
    public GeoVector getTranslatedVector() { return w; }
        
    // simply copy v
    @Override
	public final void compute() {
        w.setCoords(v);        
    }       
    
    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("TranslationOfAtoB",v.getLabel(),A.getLabel());

    }
}
