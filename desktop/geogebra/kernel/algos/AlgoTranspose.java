/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;
import geogebra.util.GgbMat;

/**
 * Reverse a list. Adapted from AlgoSort
 * @author Michael Borcherds
 * @version 16-02-2008
 */

public class AlgoTranspose extends AlgoElement {

	private GeoList inputList; //input
    private GeoList outputList; //output	

    public AlgoTranspose(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoTranspose";
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputList;

        super.setOutputLength(1);
        super.setOutput(0, outputList);
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return outputList;
    }

    @Override
	public final void compute() {
    	   		
   		GgbMat matrix = new GgbMat(inputList);
   		
   		if (matrix.isUndefined()) {
  			outputList.setUndefined();
	   		return;   		
	   	}
   		
   		matrix.transposeImmediate();
   		// Transpose[{{1,2},{3,4}}]
   		
   		outputList = matrix.getGeoList(outputList, cons);    
    }        
     
}
