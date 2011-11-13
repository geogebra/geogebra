/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel.statistics;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.NumberValue;

import org.apache.commons.math.stat.descriptive.moment.GeometricMean;




/**
 * Returns the geometric mean for a list of numbers
 */

public class AlgoGeometricMean extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
	private GeoNumeric result; //output	
	private int size;
	private GeometricMean geoMean;
	private double[] inputArray;	



	public AlgoGeometricMean(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;
		result = new GeoNumeric(cons);

		setInputOutput();
		compute();
		result.setLabel(label);
	}

	public String getClassName() {
		return "AlgoGeometricMean";
	}

	protected void setInputOutput(){
		input = new GeoElement[1];
		input[0] = inputList;

		setOutputLength(1);
		setOutput(0,result);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return result;
	}

	protected final void compute() {

		//==========================
		// validation
		size = inputList.size();
		if (!inputList.isDefined() ||  size == 0) {
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

		if(geoMean == null)
			geoMean = new GeometricMean();

		result.setValue(geoMean.evaluate(inputArray,0,size));
	}


}
