/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


/**
 * n-th element of a GeoList object.
 * 
 * Note: the type of the returned GeoElement object is determined
 * by the type of the first list element. If the list is initially empty,
 * a GeoNumeric object is created for element.
 * 
 * @author Markus Hohenwarter
 * @version 15-07-2007
 */

public class AlgoSelectedElement extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
    private GeoElement element; //output	

    /**
     * Creates new selected element algo
     * @param cons
     * @param label
     * @param geoList
     */
    AlgoSelectedElement(Construction cons, String label, GeoList geoList) {
        super(cons);
        this.geoList = geoList;
               
        // init return element as copy of first list element
        if (geoList.size() > 0) {
        	// create copy of first GeoElement in list
        	element = geoList.get(0).copyInternal(cons);        
        } 

        // desperate case: empty list
        else {        	        	        	
        	element = new GeoNumeric(cons);
        }      

        setInputOutput();
        compute();
        element.setLabel(label);
    }


    public String getClassName() {
        return "AlgoSelectedElement";
    }

    protected void setInputOutput(){
    	
        input = new GeoElement[1];
        input[0] = geoList;

        setOutputLength(1);
        setOutput(0,element);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the selected element
     * @return selected element
     */
    GeoElement getElement() {
        return element;
    }

    protected final void compute() {
    	if (!geoList.isDefined()) {
        	element.setUndefined();
    		return;
    	}
    	
		GeoElement geo = geoList.getSelectedElement();
		// check type:
		if (geo != null && geo.getGeoClassType() == element.getGeoClassType()) {
			element.set(geo);
			
		} else {
			element.setUndefined();
		}
    }
    
}
