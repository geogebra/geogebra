/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

import java.util.Arrays;


/**
 * Sort a list. Adapted from AlgoSort
 * @author Michael Borcherds
 * @version 2008-02-16
 */

public class AlgoQ3 extends AlgoElement {

	
	private GeoList inputList; //input
    private GeoNumeric Q3; //output	
    private int size;

    public AlgoQ3(Construction cons, String label, GeoList inputList) {
    	this(cons, inputList);
        Q3.setLabel(label);
    }

    public AlgoQ3(Construction cons, GeoList inputList) {
        super(cons);
        this.inputList = inputList;
               
        Q3 = new GeoNumeric(cons);

        setInputOutput();
        compute();
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoQ3;
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = inputList;

        setOnlyOutput(Q3);
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getQ3() {
        return Q3;
    }

    @Override
	public final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size < 2) {
    		Q3.setUndefined();
    		return;
    	} 
       
    	
       double[] sortList = new double[size];

       // copy inputList into an array
       for (int i=0 ; i<size ; i++)
       {
   		 GeoElement geo = inputList.get(i); 
		 if (geo.isNumberValue()) {
			NumberValue num = (NumberValue) geo;
			sortList[i]=num.getDouble();
		 }
		 else
		 {
			Q3.setUndefined();
    		return;			
		 }
       }
       
       // do the sorting
       Arrays.sort(sortList);
       
       switch (size % 4)
       {
       case 0:
      	   Q3.setValue((sortList[(3*size)/4-1]+sortList[(3*size+4)/4-1])/2);  
    	   break;
       case 1:
           Q3.setValue((sortList[(3*size+1)/4-1]+sortList[(3*size+5)/4-1])/2);  
    	   break;
       case 2:
      	   Q3.setValue(sortList[(3*size+2)/4-1]);  
    	   break;
       default:
           Q3.setValue(sortList[(3*size+3)/4-1]);  
    	   break;
       }
      
    }

	// TODO Consider locusequability
}
