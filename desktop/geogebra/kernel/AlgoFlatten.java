/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel;

import java.util.ArrayList;

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

	AlgoFlatten(Construction cons, String label, GeoList inputList) {
		super(cons);

		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public String getClassName() {
		return "AlgoFlatten";
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

		if (!inputList.isDefined()) {
			outputList.setUndefined();
			return;
		} 

		outputList.setDefined(true);
		outputList.clear();

		ArrayList<GeoElement> flattened=flatten(inputList);
		
		for (int i=0; i<flattened.size(); i++){
			outputList.add(flattened.get(i).copyInternal(cons));
		}
	}
	
	private static ArrayList<GeoElement> flatten(GeoList list){
		int size=list.size();
		ArrayList<GeoElement> output=new ArrayList<GeoElement>(size);
		for (int i=0; i<size;i++){
			GeoElement element=list.get(i);
			if (element.isGeoList()){
				output.addAll(flatten((GeoList)element));
			} else
				output.add(element);
		}
		return output;
	}
	
	final public String toString(){
		return getCommandDescription();
	}

}
