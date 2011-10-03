/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

/**
 * Bar chart algorithm. See AlgoFunctionAreaSums for implementation.
 * @author M. Borcherds
 *
 */
public class AlgoBarChart extends AlgoFunctionAreaSums {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param a
	 * @param b
	 * @param list1
	 */
	public AlgoBarChart(Construction cons, String label,
			   NumberValue a, NumberValue b, GeoList list1) {
		super(cons, label, a, b, list1);		
	}

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param a
	 */
	public AlgoBarChart(Construction cons, String label,
			GeoList list1, GeoNumeric a) {
		super(cons, label, list1, a);		
	}

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 */
	public AlgoBarChart(Construction cons, String label,
			GeoList list1, GeoList list2) {
		super(cons, label, list1, list2);		
	}

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 * @param a
	 */
	public AlgoBarChart(Construction cons, String label,
			GeoList list1, GeoList list2, NumberValue a) {
		super(cons, label,list1,list2,  a);
	}

	public AlgoBarChart(NumberValue a, NumberValue b,
			double[]vals,double[]borders,int N) {
		super(a,b,vals,borders,N);		
	}

	private AlgoBarChart(Construction cons, double[]vals,double[]borders,int N) {
		super(cons, true,vals,borders,N);
	}

	private AlgoBarChart(NumberValue a,double[]vals,double[]borders,int N) {
		super(a,vals,borders,N);
	}

	public AlgoBarChart( GeoNumeric a,double[]vals,double[]borders,int N) {
		super(a,vals,borders,N);
	}

	public AlgoBarChart(Construction cons, GeoList discreteValueList,
			GeoList discreteProbList) {
		super(cons, discreteValueList, discreteProbList);
	}
	public AlgoBarChart(Construction cons, GeoList discreteValueList,
			GeoList discreteProbList, NumberValue width) {
		super(cons, discreteValueList, discreteProbList, width);
	}
	
	public String getClassName() {
		return "AlgoBarChart";
	}
	
	public AlgoBarChart copy() {
		int N = this.getIntervals();
		switch(this.getType()) {
		case TYPE_BARCHART:
			return new AlgoBarChart((NumberValue)getA().deepCopy(kernel),
					(NumberValue)getB().deepCopy(kernel),getValues().clone(),getLeftBorder().clone(),N);
		case TYPE_BARCHART_FREQUENCY_TABLE:
			return new AlgoBarChart(kernel.getConstruction(), getValues().clone(),getLeftBorder().clone(),N);
		case TYPE_BARCHART_FREQUENCY_TABLE_WIDTH:
			return new AlgoBarChart((NumberValue)getA().deepCopy(kernel),getValues().clone(),getLeftBorder().clone(),N);
		default: //TYPE_BARCHART_RAWDATA
			return new AlgoBarChart((GeoNumeric)getN().copy(),getValues().clone(),getLeftBorder().clone(),N);	
		}
	}
	
}
