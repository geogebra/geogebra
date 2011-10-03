/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


/**
 * Histogram algorithm. See AlgoFunctionAreaSums for implementation.
 * 
 * @author M. Borcherds
 *
 */
public class AlgoHistogram extends AlgoFunctionAreaSums {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates histogram
	 * @param cons construction
	 * @param label label for the histogram
	 * @param list1 list of boundaries
	 * @param list2 list of heights or raw data
	 * @param right 
	 */
	public AlgoHistogram(Construction cons, String label,
								   GeoList list1, GeoList list2,boolean right) {
		super(cons, label, list1, list2, right);		
	}
	
	private AlgoHistogram(Construction cons, double[]vals,double[]borders,int N) {
		super(cons, vals, borders,N);		
	}
	

	/**
	 * Creates histogram with density scaling factor 
	 * @param cons construction
	 * @param label label for the histogram
	 * @param isCumulative 
	 * @param list1 list of boundaries
	 * @param list2 list of heights or raw data
	 * @param useDensity flag  
	 * @param density density scaling factor 
	 * @param right 
	 */
	public AlgoHistogram(Construction cons, String label,
			GeoBoolean isCumulative,					   
			GeoList list1, 
			GeoList list2, 
			GeoBoolean useDensity, 
			GeoNumeric density,boolean right) {
		super(cons, label, isCumulative, list1, list2, useDensity, density,right);		
	}
	
	private AlgoHistogram(
			GeoBoolean isCumulative,					   			
			GeoBoolean useDensity, 
			GeoNumeric density,double[]vals,double[]borders,int N) {
		super(isCumulative,  useDensity, density, vals, borders,N);		
	}
	
	
	public AlgoHistogram(Construction cons,
			GeoBoolean isCumulative,					   
			GeoList list1, 
			GeoList list2, 
			GeoBoolean useDensity, 
			GeoNumeric density,boolean right) {
		super(cons, isCumulative, list1, list2, useDensity, density,right);		
	}
	

	public String getClassName() {
		return isRight()?"AlgoHistogramRight":"AlgoHistogram";
	}
	
	public AlgoHistogram copy() {
		int N = getIntervals();
		if(getType()==TYPE_HISTOGRAM_DENSITY)
			return new AlgoHistogram((GeoBoolean)getIsCumulative().copy(), 
				(GeoBoolean)getUseDensityGeo().copy(),(GeoNumeric)getDensityGeo().copy(),
				getValues().clone(),getLeftBorder().clone(),N);
		return new AlgoHistogram(kernel.getConstruction(), getValues().clone(),getLeftBorder().clone(),N);
	}
	
}
