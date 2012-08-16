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
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;


/**
 * random element of a GeoList object.
 * 
 * Note: the type of the returned GeoElement object is determined
 * by the type of the first list element. If the list is initially empty,
 * a GeoNumeric object is created for element.
 * 
 * @author Michael
 * @version 2010-06-01
 */

public class AlgoRandomElement extends AlgoElement {

	private GeoList geoList; //input
    private GeoElement element; //output	

    /**
     * Creates new random element algo
     * @param cons
     * @param label
     * @param geoList
     */
    public AlgoRandomElement(Construction cons, String label, GeoList geoList) {
        super(cons);
        this.geoList = geoList;

        // init return element as copy of first list element
        element = geoList.get(0).copyInternal(cons);        

        setInputOutput();
        compute();
        element.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoRandomElement;
    }

    @Override
	protected void setInputOutput(){
    	
	        input = new GeoElement[1];
	        input[0] = geoList;

        setOutputLength(1);
        setOutput(0,element);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns chosen element
     * @return chosen element
     */
    public GeoElement getElement() {
        return element;
    }

    @Override
	public final void compute() {
    	if (!geoList.isDefined()) {
        	element.setUndefined();
    		return;
    	}

		GeoElement randElement = geoList.get((int)Math.floor((cons.getApplication().getRandomNumber() * geoList.size())));
		
		// check type:
		if (randElement.getGeoClassType() == element.getGeoClassType()) {
			element.set(randElement);			
		} else {
			element.setUndefined();
		}
    }

	// TODO Consider locusequability
    
}
