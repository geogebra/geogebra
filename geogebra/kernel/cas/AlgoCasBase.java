/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.cas;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.CasEvaluableFunction;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;

/**
 * Base class for algorithms using the CAS.
 * 
 * @author Markus Hohenwarter
 */
public abstract class AlgoCasBase extends AlgoElement {

	private static final long serialVersionUID = 1L;
	protected CasEvaluableFunction f; // input
    protected CasEvaluableFunction g; // output     

    protected AlgoCasBase(Construction cons, String label, CasEvaluableFunction f) {
    	this(cons, f);
    	
		setInputOutput(); // for AlgoElement    
        compute();   
		g.toGeoElement().setLabel(label);
    }
    
    protected AlgoCasBase(Construction cons, CasEvaluableFunction f) {
    	super(cons);
        this.f = f;
        g = (CasEvaluableFunction) f.toGeoElement().copyInternal(cons);                
    }
    
    public abstract String getClassName();
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f.toGeoElement();

        setOutputLength(1);
        setOutput(0, g.toGeoElement());
        setDependencies(); // done by AlgoElement
    }

    public GeoElement getResult() {
        return g.toGeoElement();
    }
    
    protected final void compute() {
        if (!f.toGeoElement().isDefined()) {
        	g.toGeoElement().setUndefined();
        	return;
        }    

        applyCasCommand();
    }
    
    protected abstract void applyCasCommand();
    
    public String toString() {
    	return getCommandDescription();
    }

}
