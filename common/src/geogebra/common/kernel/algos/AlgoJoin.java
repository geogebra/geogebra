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


public class AlgoJoin extends AlgoElement {

	private GeoList inputList; //input
	private GeoList outputList; //output	
	private int size, size2;

	public AlgoJoin(Construction cons, String label, GeoList inputList) {
		super(cons);

		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoJoin;
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
