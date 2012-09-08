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
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.util.Cloner;

/**
 * Bar graph algorithm
 * 
 * @author G. Sturr
 * 
 */
public class AlgoBarGraph extends AlgoElement implements DrawInformationAlgo {

	private GeoNumeric sum; // output
	
	private GeoNumeric geoWidth;
	private GeoList list1, list2; // input
  
	private double[] yVal;
	private double[] xVal;
	
	private double width;
	private boolean isHorizontal = false;

	public AlgoBarGraph(Construction cons, String label, GeoList list1,
			GeoList list2, GeoNumeric geoWidth) {

		this(cons, list1, list2, geoWidth);
		sum.setLabel(label);
	}

	public AlgoBarGraph(Construction cons, GeoList list1, GeoList list2, GeoNumeric geoWidth) {

		super(cons);

		this.list1 = list1;
		this.list2 = list2;
		this.geoWidth = geoWidth;

		sum = new GeoNumeric(cons); // output

		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
	}

	private AlgoBarGraph(Construction cons, double[] list1, double[] list2, double width) {
		super(cons, false);

		this.xVal = list1;
		this.yVal = list2;
		this.width = width;
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoBarGraph;
	}

	public AlgoBarGraph copy() {
		return new AlgoBarGraph(cons, Cloner.clone(xVal), Cloner.clone(yVal), width);
	}

	@Override
	protected void setInputOutput() {

		input = new GeoElement[3];
		input[0] = list1;
		input[1] = list2;
		input[2] = geoWidth;

		setOutputLength(1);
		setOutput(0, sum);
		setDependencies();
	}

	public GeoNumeric getSum() {
		return sum;
	}

	public double[] getXVal() {
		return xVal;
	}

	public double[] getYVal() {
		return yVal;
	}

	public double getWidth() {
		return width;
	}
	
	@Override
	public void compute() {

		if (list1 == null || !list1.isDefined()) {
			sum.setUndefined();
			return;
		}

		if (!list2.isDefined() || list1.size() == 0
				|| list1.size() != list2.size()) {
			sum.setUndefined();
			return;
		}

		if (list1.size() == 0 || list1.size() != list2.size()) {
			sum.setUndefined();
			return;
		}
		
		if(geoWidth == null || !geoWidth.isDefined()){
			sum.setUndefined();
			return;
		}
		width = geoWidth.getDouble();

		int s = 0;
		xVal = new double[list1.size()];
		yVal = new double[list1.size()];

		for (int i = 0; i < list1.size(); i++) {
			double x = list1.get(i).evaluateNum().getDouble();
			if (!Double.isNaN(x)) {
				xVal[i] = x;
			} else {
				sum.setUndefined();
				return;
			}
	
			double y = list2.get(i).evaluateNum().getDouble();
			if (!Double.isNaN(y)) {
				yVal[i] = y;
				s += y;
			} else {
				sum.setUndefined();
				return;
			}
		}

		sum.setValue(s);
	}

}
