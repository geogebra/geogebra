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
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

public class AlgoInsert extends AlgoElement {

	private GeoElement inputGeo; //input
	private GeoList inputList; //input
	private GeoNumeric n; // input
    private GeoList outputList; //output	
    private int size, insertPoint;

    public AlgoInsert(Construction cons, String label, GeoElement inputGeo, GeoList inputList, GeoNumeric n) {
        super(cons);
                
        this.inputGeo = inputGeo;
        this.inputList = inputList;
        this.n = n;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoInsert;
    }

    @Override
	protected void setInputOutput(){

    	// make sure that x(Element[list,1]) will work even if the output list's length is zero
    	outputList.setTypeStringForXML(inputList.getTypeStringForXML());  	

    	input = new GeoElement[3];
        
	    input[0] = inputGeo;
	    input[1] = inputList;
	    input[2] = n;

        super.setOutputLength(1);
        super.setOutput(0, outputList);
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return outputList;
    }

    @Override
	public final void compute() {
    	
    	//size = inputGeo.size();
    	size = inputList.size();
    	
    	insertPoint = (int)n.getDouble();
    	
    	// -1 means insert in last place, -2 means penultimate etc
    	if (insertPoint < 0) insertPoint = size + insertPoint + 2;
   	
    	if (!inputGeo.isDefined() || !inputList.isDefined() || insertPoint <= 0 || insertPoint > size+1) {
    		outputList.setUndefined();
    		return;
    	} 
       
    	outputList.setDefined(true);
    	outputList.clear();
    	
    	if (insertPoint > 1)
        for (int i = 0 ; i < insertPoint-1 ; i++)
    		outputList.add(inputList.get(i).copyInternal(cons));
    	
    	if (inputGeo.isGeoList()) {
    		GeoList list = (GeoList)inputGeo;

    		if (list.size() > 0)
		        for (int i = 0 ; i < list.size() ; i++)
		    		outputList.add(list.get(i).copyInternal(cons));
	    } else {
	    	outputList.add(inputGeo.copyInternal(cons));
	    }
           
        if (insertPoint <= size)
        for (int i = insertPoint-1 ; i < size ; i++) {
    		outputList.add(inputList.get(i).copyInternal(cons));
        }
   }

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		// TODO Consider locusequability
		return false;
	}
  
}
