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
import geogebra.common.util.GgbMat;

/**
 * Reverse a list. Adapted from AlgoSort
 * @author Michael Borcherds
 * @version 16-02-2008
 */

public class AlgoInvert extends AlgoElement {

	private GeoList inputList; //input
    private GeoList outputList; //output	

    public AlgoInvert(Construction cons, String label, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoInvert;
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
   		
   		if (matrix.isUndefined() || !matrix.isSquare()) {
  			outputList.setUndefined();
	   		return;   		
	   	}
   		
   		// needed for eg  {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}} 
   		boolean integers = matrix.hasOnlyIntegers();
   		double det = Math.round(matrix.determinant());
   		matrix.inverseImmediate();
   		if(integers){
   			for(int i=0;i<inputList.size();i++)
   				for(int j=0;j<inputList.size();j++){
   					matrix.setEntry(i, j, Math.round(matrix.getEntry(i, j)*det)/det);
   				}
   		}
   		
   		// Invert[{{1,2},{3,4}}]
   		
   		matrix.getGeoList(outputList, cons);      
    }       

	// TODO Consider locusequability
     
}
