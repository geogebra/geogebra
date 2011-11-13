/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.util.GgbMat;

/**
 * Reverse a list. Adapted from AlgoSort
 * @author Michael Borcherds
 * @version 16-02-2008
 */

public class AlgoInvert extends AlgoElement {

	private GeoList inputList; //input
    private GeoList outputList; //output	

    AlgoInvert(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoInvert";
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputList;

        super.setOutputLength(1);
        super.setOutput(0, outputList);
        setDependencies(); // done by AlgoElement
    }

    GeoList getResult() {
        return outputList;
    }

    @Override
	protected final void compute() {
    	   		
   		GgbMat matrix = new GgbMat(inputList);
   		
   		if (matrix.isUndefined() || !matrix.isSquare()) {
  			outputList.setUndefined();
	   		return;   		
	   	}
   		
   		/*
   		if (matrix.getRows() == 1) {
   			
   			double det = matrix.det();
   			
   			if (kernel.isZero(det)) {
   	  			outputList.setUndefined();
   		   		return;   		
   		   	}
   			
   			// invert 1x1 matrix
   			matrix = new GgbMatrix(1,1);
   			matrix.set(1,1,1/det);
   			
   			outputList = matrix.getGeoList(outputList, cons);
   			return;
   		}*/
   		
   		matrix.inverseImmediate();
   		
   		if (matrix.isUndefined()) {
  			//outputList.setUndefined();
	   		//return;   		
	   	}
   		// Invert[{{1,2},{3,4}}]
   		
   		outputList = matrix.getGeoList(outputList, cons);      
    }       
     
}
