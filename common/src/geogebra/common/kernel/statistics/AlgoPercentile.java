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
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

import org.apache.commons.math.stat.descriptive.rank.Percentile;


/**
 * Returns the percentile for a given percentage  in a list of numbers
 */

public class AlgoPercentile extends AlgoElement {

	
	private GeoList inputList; //input
	private GeoNumeric value; //input
	private GeoNumeric result; //output	
	private int size;
	private Percentile percentile;
	private double[] inputArray;
	private double val;



	public AlgoPercentile(Construction cons, String label, GeoList inputList, GeoNumeric value) {
		super(cons);
		this.inputList = inputList;
		this.value = value;
		result = new GeoNumeric(cons);

		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Percentile;
	}

	@Override
	protected void setInputOutput(){
		input = new GeoElement[2];
		input[0] = inputList;
		input[1] = value;

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return result;
	}

	@Override
	public final void compute() {

		//==========================
		// validation
		size = inputList.size();
		if (!inputList.isDefined() ||  size == 0) {
			result.setUndefined();
			return;
		} 

		if(value == null){
			result.setUndefined();
			return;
		}
		val = value.getDouble()*100;

		if(val <= 0 || val > 100){
			result.setUndefined();
			return;
		}


		//==========================
		// compute result

		inputArray = new double[size];

		// load input value array from  geoList
		for (int i=0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isNumberValue()) {
				NumberValue num = (NumberValue) geo;
				inputArray[i] = num.getDouble();	
			} else {
				result.setUndefined();
				return;
			}    		    		
		}   

		if(percentile == null)
			percentile = new Percentile();

		percentile.setData(inputArray);
		result.setValue(percentile.evaluate(val));
	}

	// TODO Consider locusequability


}
