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

public class AlgoDeterminant extends AlgoElement {

	private GeoList inputList; //input
    private GeoNumeric num; //output	

    AlgoDeterminant(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        num = new GeoNumeric(cons);

        setInputOutput();
        compute();
        num.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoDeterminant";
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputList;

        super.setOutputLength(1);
        super.setOutput(0, num);
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getResult() {
        return num;
    }

    @Override
	protected final void compute() {
    	   		
   		GgbMat matrix = new GgbMat(inputList);
   		
   		if (matrix.isUndefined() || !matrix.isSquare()) {
  			num.setUndefined();
	   		return;   		
	   	}
   		
   		num.setValue(matrix.getDeterminant());
   		
   		// Determinant[{{1,2},{3,4}}]
    }
        
     
}
