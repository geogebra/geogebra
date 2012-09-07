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
import geogebra.common.kernel.algos.AlgoFunctionAreaSums;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.util.Cloner;

/**
 * Bar chart algorithm. See AlgoFunctionAreaSums for implementation.
 * @author M. Borcherds
 *
 */
public class AlgoBarChart extends AlgoFunctionAreaSums {
		
	public AlgoBarChart(Construction cons, String label,
			   NumberValue a, NumberValue b, GeoList list1) {
		super(cons, label, a, b, list1);		
	}

	public AlgoBarChart(Construction cons, String label,
			GeoList list1, GeoNumeric a) {
		super(cons, label, list1, a);		
	}

	public AlgoBarChart(Construction cons,
			GeoList list1, GeoNumeric a) {
		super(cons, list1, a);		
	}
	
	public AlgoBarChart(Construction cons, String label,
			GeoList list1, GeoList list2) {
		super(cons, label, list1, list2);		
	}

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
	
	@Override
	public Algos getClassName() {
		return Algos.AlgoBarChart;
	}
	
	public AlgoBarChart copy() {
		int N = this.getIntervals();
		switch(this.getType()) {
		case TYPE_BARCHART:
			return new AlgoBarChart((NumberValue)getA().deepCopy(kernel),
					(NumberValue)getB().deepCopy(kernel),Cloner.clone(getValues()),Cloner.clone(getLeftBorder()),N);
		case TYPE_BARCHART_FREQUENCY_TABLE:
			return new AlgoBarChart(kernel.getConstruction(), Cloner.clone(getValues()),Cloner.clone(getLeftBorder()),N);
		case TYPE_BARCHART_FREQUENCY_TABLE_WIDTH:
			return new AlgoBarChart((NumberValue)getA().deepCopy(kernel),Cloner.clone(getValues()),Cloner.clone(getLeftBorder()),N);
		default: //TYPE_BARCHART_RAWDATA
			return new AlgoBarChart((GeoNumeric)getN().copy(),Cloner.clone(getValues()),Cloner.clone(getLeftBorder()),N);	
		}
	}
	
}
