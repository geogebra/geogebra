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
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.statistics.AlgoMedian;
import geogebra.common.kernel.statistics.AlgoQ1;
import geogebra.common.kernel.statistics.AlgoQ3;
import geogebra.common.util.Cloner;

/**
 * Boxplot algorithm. See AlgoFunctionAreaSums for implementation.
 * 
 * @author George Sturr
 * 
 */
public class AlgoBoxPlot extends AlgoElement implements AlgoDrawInformation {

	private static final int TYPE_QUARTILES = 0;
	private static final int TYPE_RAW = 1;
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
	private GeoList list1;
	private GeoList tempList;
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
	 */
	public AlgoBoxPlot(Construction cons, String label,NumberValue a, NumberValue b, GeoList list1) {

		this(cons, a, b, list1);
		
		sum.setLabel(label);
	}

	public AlgoBoxPlot(Construction cons, NumberValue a, NumberValue b, GeoList list1) {

		super(cons);

		type = TYPE_RAW;

		this.a = a;
		this.b = b;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();
		this.list1 = list1;

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
		if (type == TYPE_RAW) {
			
			// list1 = rawData
			if (tempList == null)
				tempList = new GeoList(cons);
			tempList.clear();
			
			AlgoListMin min2 = new AlgoListMin(cons, list1);
			cons.removeFromConstructionList(min2);
			tempList.add(min2.getMin());
			AlgoQ1 Q1 = new AlgoQ1(cons, list1);
			cons.removeFromConstructionList(Q1);
			tempList.add(Q1.getQ1());
			AlgoMedian median = new AlgoMedian(cons, list1);
			cons.removeFromConstructionList(median);
			tempList.add(median.getMedian());
			AlgoQ3 Q3 = new AlgoQ3(cons, list1);
			cons.removeFromConstructionList(Q3);
			tempList.add(Q3.getQ3());
			AlgoListMax max = new AlgoListMax(cons, list1);
			cons.removeFromConstructionList(max);
			tempList.add(max.getMax());

			N = 5;

			calcBoxPlot();

		}

		else {// TYPE_QUARTILES:

			if (tempList == null)
				tempList = new GeoList(cons);
			tempList.clear();
			tempList.add(minGeo);
			tempList.add(Q1geo);
			tempList.add(medianGeo);
			tempList.add(Q3geo);
			tempList.add(maxGeo);

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

			GeoElement geo = tempList.get(i);

			if (geo.isGeoNumeric())
				leftBorder[i] = ((GeoNumeric) geo).getDouble();
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
		if (type == TYPE_QUARTILES) {
			input = new GeoElement[7];
			input[0] = ageo;
			input[1] = bgeo;
			input[2] = minGeo;
			input[3] = Q1geo;
			input[4] = medianGeo;
			input[5] = Q3geo;
			input[6] = maxGeo;
		} else { // TYPE_RAW
			input = new GeoElement[3];
			input[0] = ageo;
			input[1] = bgeo;
			input[2] = list1;
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


	


	// TODO Consider locusequability
}
