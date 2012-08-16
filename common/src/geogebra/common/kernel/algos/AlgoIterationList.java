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
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;


/**
 * Iteration[ f(x), x0, n ] 
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoIterationList extends AlgoElement {

	private GeoFunction f; //input
	private NumberValue startValue, n;
	private GeoElement startValueGeo, nGeo;
    private GeoList list; //output	

    public AlgoIterationList(Construction cons, String label, 
    		GeoFunction f, NumberValue startValue, NumberValue n) {
        super(cons);
        this.f = f;
        this.startValue = startValue;
        startValueGeo = startValue.toGeoElement();
        this.n = n;
        nGeo = n.toGeoElement();
               
        list = new GeoList(cons);

        setInputOutput();
        compute();
        list.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoIterationList;
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[3];
        input[0] = f;
        input[1] = startValueGeo;
        input[2] = nGeo;

        super.setOutputLength(1);
        super.setOutput(0, list);
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return list;
    }

    @Override
	public final void compute() {
    	list.setDefined(true);
    	for (int i=0; i < input.length; i++) {
    		if (!input[i].isDefined()) {
    			list.setUndefined();
    			return;
    		}
    	}     
    	
    	// number of iterations
    	list.clear();
       	int iterations = (int) Math.round(n.getDouble());   
    	if (iterations < 0) return;    	
    	    	    	     	
    	// perform iteration f(f(f(...(startValue))))
    	// and fill list with all intermediate results    	
    	double val = startValue.getDouble();
    	setListElement(0, val);    	
    	for (int i=0; i < iterations; i++) {    		    		
    		val = f.evaluate(val);
    		setListElement(i+1, val);
    	}    	    	    
    }   
    
    private void setListElement(int index, double value) {
    	GeoNumeric listElement;
    	if (index < list.getCacheSize()) {
    		// use existing list element
    		listElement = (GeoNumeric) list.getCached(index);    	
    	} else {
    		// create a new list element
    		listElement = new GeoNumeric(cons);
    		listElement.setParentAlgorithm(this);
    		listElement.setConstructionDefaults();
			listElement.setUseVisualDefaults(false);	    		
    	}
    	
    	list.add(listElement);
    	listElement.setValue(value);
    }

	// TODO Consider locusequability
    
}
