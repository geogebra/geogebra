/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel;



public class AlgoJoin extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
	private GeoList outputList; //output	
	private int size, size2;

	AlgoJoin(Construction cons, String label, GeoList inputList) {
		super(cons);

		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}


	public String getClassName() {
		return "AlgoJoin";
	}
	
	protected void setInputOutput(){
		input = new GeoElement[1];

		input[0] = inputList;

		output = new GeoElement[1];
		output[0] = outputList;
		setDependencies(); // done by AlgoElement
	}

	GeoList getResult() {
		return outputList;
	}

	protected final void compute() {

		size = inputList.size();

		if (!inputList.isDefined()) {
			outputList.setUndefined();
			return;
		} 

		outputList.setDefined(true);
		outputList.clear();

		for (int i=0 ; i < size ; i++) {

			GeoElement geo = inputList.get(i);
			if (!geo.isGeoList()) {
				outputList.setUndefined();
				return;
			}

			GeoList list = (GeoList)geo;
			size2 = list.size();

			if (size2 > 0) {
				for (int j=0 ; j < size2 ; j++) {
					//GeoElement geo2 = list.get(j);
					//Application.debug(geo.getLabel() + " " + geo2.getClass());
					outputList.add(list.get(j).copyInternal(cons));
					
				}

			}
		}

	}

}
