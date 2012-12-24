/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.plugin.GeoClass;

import java.util.Iterator;

import org.apache.commons.math.stat.Frequency;

public class AlgoUnique extends AlgoElement {

	private GeoList dataList; //input
	private GeoList uniqueList; //output	
	
	private Frequency f;


	public AlgoUnique(Construction cons, String label, GeoList dataList ) {
		this(cons, dataList);
		uniqueList.setLabel(label);
	}

	public AlgoUnique(Construction cons, GeoList dataList ) {
		super(cons);
		this.dataList = dataList;

		uniqueList = new GeoList(cons);

		setInputOutput();
		compute();

	}

	@Override
	public Commands getClassName() {
		return Commands.Unique;
	}

	@Override
	protected void setInputOutput(){

    	// make sure that x(Element[list,1]) will work even if the output list's length is zero
		uniqueList.setTypeStringForXML(dataList.getTypeStringForXML());  	

    	input = new GeoElement[1];
		input[0] = dataList;

		super.setOutputLength(1);
        super.setOutput(0, uniqueList);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return uniqueList;
	}

	@Override
	public final void compute() {
	
		// Validate input arguments
		if (!dataList.isDefined() || dataList.size() == 0) {
			uniqueList.setUndefined();		
			return; 		
		}

		if( !( dataList.getElementType() .equals(GeoClass.TEXT) 
				|| dataList.getElementType() .equals(GeoClass.NUMERIC ))) {
			uniqueList.setUndefined();		
			return;
		}

		uniqueList.setDefined(true);
		uniqueList.clear();

		// Load the data into f, an instance of Frequency class 
		if(f == null)
		 f = new Frequency();
		f.clear();
		for (int i=0 ; i < dataList.size(); i++) {
			if(dataList.getElementType() .equals(GeoClass.TEXT))
				f.addValue(((GeoText)dataList.get(i)).toValueString(StringTemplate.defaultTemplate));
			if(dataList.getElementType() .equals(GeoClass.NUMERIC))
				f.addValue(new MyDouble(kernel, ((GeoNumeric)dataList.get(i)).getDouble()));
		}

		// Get the unique value list 	
		if(dataList.getElementType() .equals(GeoClass.TEXT)) {
			// handle string data
			Iterator<Comparable<?>> itr = f.valuesIterator();
			while(itr.hasNext()) {		
				String s = (String) itr.next();
				GeoText text = new GeoText(cons);
				text.setTextString(s);
				uniqueList.add(text);
			}
		} else {
			// handle numeric data
			Iterator<Comparable<?>> itr = f.valuesIterator();
			while(itr.hasNext()) {		
				MyDouble n = (MyDouble) itr.next();
				uniqueList.add(new GeoNumeric(cons,n.getDouble()));
			}
		} 
	}

	// TODO Consider locusequability

}
