/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;


/**
 * Remove undefined objects from a list
 * @author Michael Borcherds
 * @version 2008-03-04
 */

public class AlgoRemoveUndefined extends AlgoElement {

	private GeoList inputList; //input
    private GeoList outputList; //output	
    private int size;

    /**
     * Creates new undefined removal algo
     * @param cons
     * @param label
     * @param inputList
     */
    public AlgoRemoveUndefined(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoRemoveUndefined;
    }

    @Override
	protected void setInputOutput(){
    	
    	// make sure that x(Element[list,1]) will work even if the output list's length is zero
    	outputList.setTypeStringForXML(inputList.getTypeStringForXML());  	

    	input = new GeoElement[1];
        input[0] = inputList;

        setOutputLength(1);
        setOutput(0, outputList);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the pruned list
     * @return pruned list
     */
    public GeoList getResult() {
        return outputList;
    }

    @Override
	public final void compute() {
    	
    	size = inputList.size();
    	
    	if (!inputList.isDefined()) {
    		outputList.setUndefined();
    		return;
    	} 
       
    	outputList.setDefined(true);
    	outputList.clear();
    	
    	if (size == 0) return;
    	
    	for (int i=0 ; i<size ; i++)
    	{
    		GeoElement geo=inputList.get(i);
    		
    		boolean isDefined = geo.isDefined();
    		
    		// intersection of 2 parallel lines "undefined" in AlgebraView
    		// but isDefined() returns true
    		if (isDefined && geo.isGeoPoint()) {
    			isDefined = ((GeoPoint)geo).isFinite();
    		}
    		//Application.debug(isDefined+"");
    		if (isDefined) outputList.add(geo.copyInternal(cons));
    	}
    } 	

	// TODO Consider locusequability
  
}
