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
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;



/**
 * Creates a residual plot. 
 * 
 * Input: list of points (x,y)
 * Input: regression function 
 * Output: list of residual points (x, y - yPredicted)
 *  
 * @author G.Sturr
 */

public class AlgoResidualPlot extends AlgoElement {

	private GeoList inputList; //input
	private GeoFunctionable function;
	private GeoList outputList; //output	
	private int size;
	private double min, max;
	

	public AlgoResidualPlot(Construction cons, String label, GeoList inputList, GeoFunctionable function2) {
		this(cons, inputList, function2);
		outputList.setLabel(label);
	}

	public AlgoResidualPlot(Construction cons, GeoList inputList, GeoFunctionable function2) {
		super(cons);
		this.inputList = inputList;
		this.function = function2;       
		outputList = new GeoList(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoResidualPlot;
	}

	@Override
	protected void setInputOutput(){
		input = new GeoElement[2];
		input[0] = inputList;
		input[1] = function.toGeoElement();

		super.setOutputLength(1);
        super.setOutput(0, outputList);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return outputList;
	}

	public double[] getResidualBounds(){
		double[] bounds = {min,max}; 
		return bounds;
	}
	
	@Override
	public final void compute() {

		size = inputList.size();
		if (!inputList.isDefined() ||  size == 0) {
			outputList.setUndefined();
			return;
		} 

		outputList.setDefined(true);
		outputList.clear();
		boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		
		GeoFunction funGeo = function.getGeoFunction();
    	
		double x,y, r;

		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;
		
		for (int i = 0 ; i < size ; i++) {
			GeoElement p = inputList.get(i);
			if (p.isGeoPoint()) {
				x = ((GeoPoint)p).getInhomX();
				y = ((GeoPoint)p).getInhomY();
				r = y - funGeo.evaluate(x);
				min = Math.min(r,min);
				max = Math.max(r,max);
				outputList.add(new GeoPoint(cons, null, x, r, 1.0));
			} else {
				outputList.setUndefined();
				return;
			}
		}	
		cons.setSuppressLabelCreation(suppressLabelCreation);
	}  

	// TODO Consider locusequability

}
