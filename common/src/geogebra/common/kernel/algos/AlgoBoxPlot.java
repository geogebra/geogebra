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
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.statistics.AlgoMedian;
import geogebra.common.kernel.statistics.AlgoQ1;
import geogebra.common.kernel.statistics.AlgoQ3;
import geogebra.common.util.Cloner;

import java.util.ArrayList;

/**
 * Boxplot algorithm. See AlgoFunctionAreaSums for implementation.
 * 
 * @author George Sturr
 * 
 */
public class AlgoBoxPlot extends AlgoElement implements AlgoDrawInformation {

	private static final int TYPE_QUARTILES = 0;
	private static final int TYPE_RAW = 1;
	private static final int TYPE_FREQUENCY = 2;
	private int type;
	private NumberValue a;
	private NumberValue b;
	private GeoElement ageo;
	private GeoElement bgeo;
	private GeoElement minGeo;
	private GeoElement Q1geo;
	private GeoElement medianGeo;
	private GeoElement Q3geo;
	private GeoElement maxGeo;
	private GeoNumeric sum;
	private GeoBoolean useOutliersGeo;
	private GeoList list1;
	private GeoList freqList;
	private ArrayList<Double> tempList;
	private int N;
	private double[] yval;
	private double[] leftBorder;

	/**
	 * Creates boxplot given all the quartiles, y-offset and y-scale
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param a
	 *            y-offset
	 * @param b
	 *            y-scale
	 * @param min
	 * @param Q1
	 * @param median
	 * @param Q3
	 * @param max
	 */
	public AlgoBoxPlot(Construction cons, String label, NumberValue a, NumberValue b,  NumberValue min,
			NumberValue Q1, NumberValue median, NumberValue Q3,
			NumberValue max) {

		super(cons);

		type = TYPE_QUARTILES;

		this.a = a;
		this.b = b;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();
		minGeo = min.toGeoElement();
		Q1geo = Q1.toGeoElement();
		medianGeo = median.toGeoElement();
		Q3geo = Q3.toGeoElement();
		maxGeo = max.toGeoElement();

		sum = new GeoNumeric(cons); // output
		// sum.setLabelVisible(false);
		setInputOutput(); // for AlgoElement
		compute();
		sum.setLabel(label);
		sum.setDrawable(true);
	}
	

	/**
	 * Creates boxplot from list of raw data
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param a
	 *            y-offset
	 * @param b
	 *            y-scale
	 * @param list1
	 *            rawData
	 * @param useOutliers whether to plot outliers separately
	 */
	public AlgoBoxPlot(Construction cons, String label,NumberValue a, NumberValue b, GeoList list1, GeoBoolean useOutliers) {

		this(cons, a, b, list1, useOutliers);
		
		sum.setLabel(label);
	}

	
	
	public AlgoBoxPlot(Construction cons, String label,NumberValue a, NumberValue b, GeoList list1, GeoList freqList, GeoBoolean useOutliers) {

		this(cons, a, b, list1, freqList, useOutliers);
		sum.setLabel(label);
	}
		
	/**
	 * Creates boxplot from frequency table
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param a
	 *            y-offset
	 * @param b
	 *            y-scale
	 * @param list1
	 *            rawData
	 * @param useOutliers whether to plot outliers separately
	 */
	public AlgoBoxPlot(Construction cons, NumberValue a, NumberValue b, GeoList list1, GeoList freqList, GeoBoolean useOutliers) {

		super(cons);

		type = TYPE_FREQUENCY;

		this.a = a;
		this.b = b;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();
		this.list1 = list1;
		this.freqList = freqList;
		this.useOutliersGeo = useOutliers;

		sum = new GeoNumeric(cons); // output
		// sum.setLabelVisible(false);
		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
	}

	public AlgoBoxPlot(Construction cons, NumberValue a, NumberValue b, GeoList list1, GeoBoolean useOutliers) {

		super(cons);

		type = TYPE_RAW;

		this.a = a;
		this.b = b;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();
		this.list1 = list1;
		this.useOutliersGeo = useOutliers;

		sum = new GeoNumeric(cons); // output
		// sum.setLabelVisible(false);
		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
	}

	private AlgoBoxPlot(Construction cons, double[] list1, NumberValue a, NumberValue b) {
		super(cons, false);
		type = TYPE_RAW;

		this.a = a;
		this.b = b;
		this.leftBorder = list1;
	}

	public NumberValue getB() {
		return b;
	}

	public NumberValue getA() {
		return a;
	}

	public GeoList getList1() {
		return list1;
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoBoxPlot;
	}

	public AlgoBoxPlot copy() {
		return new AlgoBoxPlot(cons, Cloner.clone(leftBorder),
					(NumberValue) a.deepCopy(kernel), (NumberValue) b
							.deepCopy(kernel));		
	}

	@Override
	public void compute() {
		
		
		boolean useOutliers = false;
		
		if (useOutliersGeo != null && useOutliersGeo.getBoolean()) {
			useOutliers = true;
		}
		
		outliers = null;
		if (tempList == null) {
			tempList = new ArrayList<Double>();
		}
		tempList.clear();

		if (type == TYPE_FREQUENCY) {
			if (list1.size() == 0 || list1.size() != freqList.size()) {
				sum.setUndefined();
				return;
			}
		}	
			
		
		if (type == TYPE_RAW || type == TYPE_FREQUENCY) {
			
			AlgoQ1 Q1Algo;
			AlgoQ3 Q3Algo;
			AlgoMedian medianAlgo;
			
			if(type == TYPE_RAW ){
				Q1Algo = new AlgoQ1(cons, list1);				
				medianAlgo = new AlgoMedian(cons, list1);			
				Q3Algo = new AlgoQ3(cons, list1);
			}else{
				Q1Algo = new AlgoQ1(cons, list1, freqList);
				medianAlgo = new AlgoMedian(cons, list1, freqList);		
				Q3Algo = new AlgoQ3(cons, list1, freqList);
			}
			cons.removeFromConstructionList(Q1Algo);
			cons.removeFromConstructionList(Q3Algo);
			cons.removeFromConstructionList(medianAlgo);
			

			double median = medianAlgo.getMedian().getDouble();
			double Q1 = Q1Algo.getQ1().getDouble();
			double Q3 = Q3Algo.getQ3().getDouble();
			double min = Double.MAX_VALUE;
			double max = -Double.MAX_VALUE;
			
			
			for (int i = 0 ; i < list1.size() ; i++) {
				double x = list1.get(i).evaluateNum().getDouble();
				
				boolean updateMaxMin = true;
				
				if (useOutliers) {

					// outlier = more than 1.5 * IQR from median
					if (x > median + 1.5 * (Q3 - Q1)) {
						addOutlier(x);
						updateMaxMin = false;
					}
					
					if (x < median - 1.5 * (Q3 - Q1)) {
						addOutlier(x);
						updateMaxMin = false;
					}
					
				}
				
				// need to adjust max/min (ie exclude outliers)
				if (updateMaxMin) {
				
					if (x < min) {
						min = x;
					}
					// no else (think!)
					if (x > max) {
						max = x;
					}
				}

				
			}
			
			//App.debug(min+" "+Q1+" "+median+" "+Q3+" "+max);
			
			
			tempList.add(min);
			tempList.add(Q1);
			tempList.add(median);
			tempList.add(Q3);
			tempList.add(max);
			N = 5;

			calcBoxPlot();

		}

		else {// TYPE_QUARTILES:

			tempList.add(minGeo.evaluateNum().getDouble());
			tempList.add(Q1geo.evaluateNum().getDouble());
			tempList.add(medianGeo.evaluateNum().getDouble());
			tempList.add(Q3geo.evaluateNum().getDouble());
			tempList.add(maxGeo.evaluateNum().getDouble());

			N = 5;

			calcBoxPlot();

		}

	}

	private void calcBoxPlot() {
		if (yval == null || yval.length < N) {
			yval = new double[N];
			leftBorder = new double[N];
		}

		for (int i = 0; i < N; i++) {

			double x = tempList.get(i);

			if (!Double.isNaN(x))
				leftBorder[i] = x;
			else {
				sum.setUndefined();
				return;
			}

			yval[i] = 1.0; // dummy value

		}

		sum.setValue(leftBorder[2]); // median
	}

	@Override
	protected void setInputOutput() {
		
		switch (type) {
		case TYPE_QUARTILES:
			input = new GeoElement[7];
			input[0] = ageo;
			input[1] = bgeo;
			input[2] = minGeo;
			input[3] = Q1geo;
			input[4] = medianGeo;
			input[5] = Q3geo;
			input[6] = maxGeo;			
			break;
			
		case TYPE_RAW:
			input = new GeoElement[3 + (useOutliersGeo == null ? 0:1)];
			input[0] = ageo;
			input[1] = bgeo;
			input[2] = list1;
			
			if (useOutliersGeo != null) {
				input[3] = useOutliersGeo;
			}
			
			break;
			
		case TYPE_FREQUENCY:
			input = new GeoElement[4 + (useOutliersGeo == null ? 0:1)];
			input[0] = ageo;
			input[1] = bgeo;
			input[2] = list1;
			input[3] = freqList;
			
			if (useOutliersGeo != null) {
				input[4] = useOutliersGeo;
			}
			
			break;
				
		}

		setOutputLength(1);
		setOutput(0, sum);
		setDependencies();
	}

	public GeoNumeric getSum() {
		return sum;
	}
	
	/**
	 * Returns minimum
	 * @return minimum
	 */
	public GeoElement getMinGeo() {
		return minGeo;
	}

	/**
	 * Returns maximum
	 * @return maximum
	 */
	public GeoElement getMaxGeo() {
		return maxGeo;
	}

	/**
	 * Returns Q1
	 * @return Q1
	 */
	public GeoElement getQ1geo() {
		return Q1geo;
	}

	/**
	 * Returns Q3
	 * @return Q3
	 */
	public GeoElement getQ3geo() {
		return Q3geo;
	}

	/**
	 * Returns median
	 * @return median
	 */
	public GeoElement getMedianGeo() {
		return medianGeo;
	}
	public double[] getLeftBorders(){
		return leftBorder;
	}

	private ArrayList<Double> outliers;

	public ArrayList<Double> getOutliers() {
		return outliers;
	}
	
	private void addOutlier(double x) {
		
		//App.debug("outlier "+x);
		
		if (outliers == null) {
			outliers = new ArrayList<Double>();
		}
		
		outliers.add(x);
		
	}


	


}
