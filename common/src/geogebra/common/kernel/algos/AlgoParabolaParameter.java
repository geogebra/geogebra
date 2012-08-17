/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoParabolaParameter.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoParabolaParameter extends AlgoElement {
    
    private GeoConic c;  // input
    private GeoNumeric num;     // output                  
    
    public AlgoParabolaParameter(Construction cons, String label, GeoConic c) {        
        super(cons);
        this.c = c;                                                              
        num = new GeoNumeric(cons);                
        setInputOutput(); // for AlgoElement                
        compute();              
        num.setLabel(label);            
    }   
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoParabolaParameter;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_PARABOLA;
    }
        
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;        
        
        super.setOutputLength(1);
        super.setOutput(0, num);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoNumeric getParameter() { return num; }    
    GeoConic getConic() { return c; }        
    
    // set parameter of parabola
    @Override
	public final void compute() {        
        if (c.type == GeoConicNDConstants.CONIC_PARABOLA)
            num.setValue(c.p);
        else 
            num.setUndefined();
    }
    
    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("ParameterOfA",c.getLabel(tpl));
    }

	// TODO Consider locusequability
}
