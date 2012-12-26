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
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;

import java.util.Arrays;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;


/**
 * Creates a Normal Quantile Plot. 
 * 
 * Input: list of unsorted raw numeric data 
 * Output: list containing 
 * (1) points forming a normal quantile plot for the raw data and
 * (2) a linear function for the qq line. 
 * 
 * Point ordering: 
 * x-coords = data values
 * y-coords = expected z-scores
 * 
 * 
 * The algorithm follows the description given by: 
 * http://en.wikipedia.org/wiki/Normal_probability_plot
 * http://www.itl.nist.gov/div898/handbook/eda/section3/normprpl.htm
 * 
 * @author G.Sturr
 */

public class AlgoNormalQuantilePlot extends AlgoElement {

	private GeoList inputList; //input
	private GeoList outputList; //output	
	private int size;
	private double[] zValues;
	private double[] sortedData;

	public AlgoNormalQuantilePlot(Construction cons, String label, GeoList inputList) {		
		this(cons, inputList);
		outputList.setLabel(label);
	}

	public AlgoNormalQuantilePlot(Construction cons, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.NormalQuantilePlot;
	}

	@Override
	protected void setInputOutput(){
		input = new GeoElement[1];
		input[0] = inputList;

		setOutputLength(1);
		setOutput(0,outputList);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return outputList;
	}

	private void calculateZValues(int n){

		zValues = new double[n];    	
		NormalDistributionImpl normalDist = new NormalDistributionImpl(0, 1);
		double x;

		try {
			x = 1 - Math.pow(0.5, 1.0/n);
			zValues[0] = normalDist.inverseCumulativeProbability(x);

			for(int i = 2; i<n; i++){
				x = (i - 0.3175)/(n + 0.365);
				zValues[i-1] = normalDist.inverseCumulativeProbability(x);
			}

			x = Math.pow(0.5, 1.0/n);
			zValues[n-1] = normalDist.inverseCumulativeProbability(x); 

		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private GeoSegment getQQLineSegment(){

		SummaryStatistics stats = new SummaryStatistics();
		for (int i = 0; i < sortedData.length; i++) {
			stats.addValue(sortedData[i]);
		}
		double sd = stats.getStandardDeviation();
		double mean = stats.getMean();
		double min = stats.getMin();
		double max = stats.getMax();

		// qq line: y = (1/sd)x - mean/sd 
		
		GeoPoint startPoint = new GeoPoint(cons);
		startPoint.setCoords(min, (min/sd) - mean/sd, 1.0);
		GeoPoint endPoint = new GeoPoint(cons);
		endPoint.setCoords(max, (max/sd) - mean/sd, 1.0);
		GeoSegment seg = new GeoSegment(cons, startPoint, endPoint);
		seg.calcLength();
		
		return seg;
	}

	@Override
	public final void compute() {

		// validate
		size = inputList.size();
		if (!inputList.isDefined() ||  size == 0) {
			outputList.setUndefined();
			return;
		} 

		// convert geoList to sorted array of double
		sortedData = new double[size];
		for (int i=0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isNumberValue()) {
				NumberValue num = (NumberValue) geo;
				sortedData[i] = num.getDouble();

			} else {
				outputList.setUndefined();
				return;
			}    		    		
		}   
		Arrays.sort(sortedData);

		// create the z values
		calculateZValues(size);
		
		// prepare output list. Pre-existing geos will be recycled, 
		// but extra geos are removed when outputList is too long
		outputList.setDefined(true);
		for (int i = outputList.size() - 1; i >= size; i--) {
			GeoElement extraGeo = outputList.get(i);
			extraGeo.remove();
			outputList.remove(extraGeo);
			
		}	
		int oldListSize = outputList.size();
		

		// iterate through the sorted data and create the normal quantile points 

		boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		for(int i = 0; i<sortedData.length; i++) {
			if(i<oldListSize)
				((GeoPoint)outputList.get(i)).setCoords(sortedData[i], zValues[i], 1.0);
			else
				outputList.add(new GeoPoint(cons, null, sortedData[i], zValues[i], 1.0));
		}      

		// create qq line segment and add it to the list
		outputList.add(getQQLineSegment());
		
		cons.setSuppressLabelCreation(suppressLabelCreation);
	}

	// TODO Consider locusequability

}
