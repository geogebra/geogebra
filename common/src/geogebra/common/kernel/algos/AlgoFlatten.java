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
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * adapted from AlgoJoin
 * 
 * @author Simon
 * @version 2011-11-15
 * 
 */
public class AlgoFlatten extends AlgoElement {

	private GeoList inputList; //input
	private GeoList outputList; //output

	public AlgoFlatten(Construction cons, String label, GeoList inputList) {
		super(cons);

		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoFlatten;
	}
	
	@Override
	protected void setInputOutput(){

    	// make sure that x(Element[list,1]) will work even if the output list's length is zero
    	outputList.setTypeStringForXML(inputList.getTypeStringForXML());  	

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

		if (!inputList.isDefined()) {
			outputList.setUndefined();
			return;
		} 

		outputList.setDefined(true);
		outputList.clear();

		flatten(outputList,inputList);
	}
	
	private void flatten(GeoList outputList, GeoList list){
		int size=list.size();
		for (int i=0; i<size;i++){
			GeoElement element=list.get(i);
			if (element.isGeoList()){
				flatten(outputList, (GeoList)element);
			} else
				outputList.add(element.copyInternal(cons));
		}
	}
	
	@Override
	final public String toString(StringTemplate tpl){
		return getCommandDescription(tpl);
	}

	// TODO Consider locusequability

}
