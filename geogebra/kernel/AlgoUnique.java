/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.MyDouble;

import java.util.Iterator;

import org.apache.commons.math.stat.Frequency;



public class AlgoUnique extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList dataList; //input
	private GeoList uniqueList; //output	
	
	private Frequency f;


	AlgoUnique(Construction cons, String label, GeoList dataList ) {
		this(cons, dataList);
		uniqueList.setLabel(label);
	}

	AlgoUnique(Construction cons, GeoList dataList ) {
		super(cons);
		this.dataList = dataList;

		uniqueList = new GeoList(cons);

		setInputOutput();
		compute();

	}

	public String getClassName() {
		return "AlgoUnique";
	}

	protected void setInputOutput(){

		input = new GeoElement[1];
		input[0] = dataList;

		output = new GeoElement[1];
		output[0] = uniqueList;
		setDependencies(); // done by AlgoElement
	}

	GeoList getResult() {
		return uniqueList;
	}


	protected final void compute() {
	
		// Validate input arguments
		if (!dataList.isDefined() || dataList.size() == 0) {
			uniqueList.setUndefined();		
			return; 		
		}

		if( !( dataList.getElementType() == GeoElement.GEO_CLASS_TEXT 
				|| dataList.getElementType() == GeoElement.GEO_CLASS_NUMERIC )){
			uniqueList.setUndefined();		
			return;
		}


		uniqueList.setDefined(true);
		uniqueList.clear();


		// Load the data into f, an instance of Frequency class 
		if(f == null)
		 f = new Frequency();
		f.clear();
		for (int i=0 ; i < dataList.size(); i++){
			if(dataList.getElementType() == GeoElement.GEO_CLASS_TEXT)
				f.addValue(((GeoText)dataList.get(i)).toValueString());
			if(dataList.getElementType() == GeoElement.GEO_CLASS_NUMERIC)
				f.addValue(new MyDouble(kernel, ((GeoNumeric)dataList.get(i)).getDouble()));
		}



		// Get the unique value list 	
		if(dataList.getElementType() == GeoElement.GEO_CLASS_TEXT){
			// handle string data
			Iterator<Comparable<?>> itr = f.valuesIterator();
			while(itr.hasNext()) {		
				String s = (String) itr.next();
				GeoText text = new GeoText(cons);
				text.setTextString(s);
				uniqueList.add(text);
			}
		}else{
			// handle numeric data
			Iterator<Comparable<?>> itr = f.valuesIterator();
			while(itr.hasNext()) {		
				MyDouble n = (MyDouble) itr.next();
				uniqueList.add(new GeoNumeric(cons,n.getDouble()));
			}
		} 
	}

}
