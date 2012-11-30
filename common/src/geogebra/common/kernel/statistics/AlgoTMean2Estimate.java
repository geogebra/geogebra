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
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

/**
 * Calculates a t-confidence interval estimate of the difference of means.
 * 
 * 
 * @author G. Sturr
 */
public class AlgoTMean2Estimate extends AlgoElement {

	
	private GeoList geoList1, geoList2; //input
	private GeoNumeric geoLevel, geoMean1, geoSD1, geoN1, geoMean2, geoSD2, geoN2; //input
	private GeoBoolean geoPooled; //input

	private GeoList  result; // output   

	private double[] val1, val2;
	private int size1, size2;
	private double level, mean1, var1, n1, mean2, var2, n2, me;
	boolean pooled;
	private SummaryStatistics stats;
	private TDistributionImpl tDist;
	private double difference;


	public AlgoTMean2Estimate(Construction cons, String label, GeoList geoList1, GeoList geoList2, GeoNumeric geoLevel, GeoBoolean geoPooled) {
		super(cons);
		this.geoList1 = geoList1;
		this.geoList2 = geoList2;
		this.geoLevel = geoLevel;
		this.geoPooled = geoPooled;

		this.geoMean1 = null;
		this.geoSD1 = null;
		this.geoN1 = null;
		this.geoMean2 = null;
		this.geoSD2 = null;
		this.geoN2 = null;

		
		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		result.setLabel(label);
	}

	public AlgoTMean2Estimate(Construction cons, String label, GeoNumeric geoMean1, GeoNumeric geoSD1, GeoNumeric geoN1,
			GeoNumeric geoMean2, GeoNumeric geoSD2, GeoNumeric geoN2, GeoNumeric geoLevel, GeoBoolean geoPooled) {
		this(cons, geoMean1, geoSD1, geoN1, geoMean2, geoSD2, geoN2, geoLevel,
				geoPooled);
		result.setLabel(label);
	}

	public AlgoTMean2Estimate(Construction cons, GeoNumeric geoMean1, GeoNumeric geoSD1, GeoNumeric geoN1,
			GeoNumeric geoMean2, GeoNumeric geoSD2, GeoNumeric geoN2, GeoNumeric geoLevel, GeoBoolean geoPooled) {
		super(cons);
		this.geoList1 = null;
		this.geoList2 = null;
		this.geoLevel = geoLevel;
		this.geoPooled = geoPooled;

		this.geoMean1 = geoMean1;
		this.geoSD1 = geoSD1;
		this.geoN1 = geoN1;
		this.geoMean2 = geoMean2;
		this.geoSD2 = geoSD2;
		this.geoN2 = geoN2;

		

		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
	}

	

	@Override
	public Algos getClassName() {
		return Algos.AlgoTMean2Estimate;
	}

	@Override
	protected void setInputOutput(){

		if(geoList1 != null){
			input = new GeoElement[4];
			input[0] = geoList1;
			input[1] = geoList2;
			input[2] = geoLevel;
			input[3] = geoPooled;

		}else{
			input = new GeoElement[8];
			input[0] = geoMean1;
			input[1] = geoSD1;
			input[2] = geoN1;
			input[3] = geoMean2;
			input[4] = geoSD2;
			input[5] = geoN2;
			input[6] = geoLevel;
			input[7] = geoPooled;
		}

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return result;
	}



	/**
	 * Computes approximate degrees of freedom for 2-sample t-estimate.
	 * (code from Apache commons, TTestImpl class)
	 *
	 * @param v1 first sample variance
	 * @param v2 second sample variance
	 * @param n1 first sample n
	 * @param n2 second sample n
	 * @return approximate degrees of freedom
	 */
	private static double getDegreeOfFreedom(double v1, double v2, double n1, double n2, boolean pooled) {
		
		if(pooled)
			return n1 + n2 - 2;
		return (((v1 / n1) + (v2 / n2)) * ((v1 / n1) + (v2 / n2))) /
		((v1 * v1) / (n1 * n1 * (n1 - 1d)) + (v2 * v2) /
				(n2 * n2 * (n2 - 1d)));
	}


	/**
	 * Computes margin of error for 2-sample t-estimate; 
	 * this is the half-width of the confidence interval
	 * 
	 * @param v1 first sample variance
	 * @param v2 second sample variance
	 * @param n1 first sample n
	 * @param n2 second sample n
	 * @param confLevel confidence level
	 * @return margin of error for 2 mean interval estimate
	 * @throws MathException
	 */
	private double getMarginOfError(double v1, double n1, double v2, double n2, double confLevel, boolean pooled) throws MathException {

		if(pooled){
			
			double pooledVariance = ((n1  - 1) * v1 + (n2 -1) * v2 ) / (n1 + n2 - 2);
			double se = Math.sqrt(pooledVariance * (1d / n1 + 1d / n2));
			tDist = new TDistributionImpl(getDegreeOfFreedom(v1, v2, n1, n2, pooled));
			double a = tDist.inverseCumulativeProbability((confLevel + 1d)/2);
			return a * se;
			
		
		}
		double se = Math.sqrt((v1 / n1) + (v2 / n2));
		tDist = new TDistributionImpl(getDegreeOfFreedom(v1, v2, n1, n2, pooled));
		double a = tDist.inverseCumulativeProbability((confLevel + 1d)/2);
		return a * se;

	}



	@Override
	public final void compute() {

		try 
		{

			// get statistics from sample data input
			if(input.length == 4){

				size1= geoList1.size();
				if(!geoList1.isDefined() || size1 < 2){
					result.setUndefined();	
					return;			
				}

				size2= geoList2.size();
				if(!geoList2.isDefined() || size2 < 2){
					result.setUndefined();	
					return;			
				}

				val1 = new double[size1];
				for (int i=0; i < size1; i++) {
					GeoElement geo = geoList1.get(i);
					if (geo.isNumberValue()) {
						NumberValue num = (NumberValue) geo;
						val1[i] = num.getDouble();

					} else {
						result.setUndefined();
						return;
					}    		    		
				}   

				val2 = new double[size2];
				for (int i=0; i < size2; i++) {
					GeoElement geo = geoList2.get(i);
					if (geo.isNumberValue()) {
						NumberValue num = (NumberValue) geo;
						val2[i] = num.getDouble();

					} else {
						result.setUndefined();
						return;
					}    		    		
				}   


				stats = new SummaryStatistics();
				for (int i = 0; i < val1.length; i++) {
					stats.addValue(val1[i]);
				}

				n1 = stats.getN();
				var1 = stats.getVariance();
				mean1 = stats.getMean();

				stats.clear();
				for (int i = 0; i < val2.length; i++) {
					stats.addValue(val2[i]);
				}

				n2 = stats.getN();
				var2 = stats.getVariance();
				mean2 = stats.getMean();
				
				
			}else{
				mean1 = geoMean1.getDouble();
				var1 = geoSD1.getDouble()*geoSD1.getDouble();
				n1 = geoN1.getDouble();

				mean2 = geoMean2.getDouble();
				var2 = geoSD2.getDouble()*geoSD2.getDouble();
				n2 = geoN2.getDouble();	
			}

			
			level = geoLevel.getDouble();
			pooled = geoPooled.getBoolean();
			

			// validate statistics
			if(level < 0 || level > 1 || var1 < 0 || n1 < 1 || var2 < 0 || n2 < 1){
				result.setUndefined();
				return;
			}


			// get interval estimate 
			me = getMarginOfError(var1, n1, var2, n2, level, pooled);
			
			
			// return list = {low limit, high limit, difference, margin of error, df }
			difference = mean1 -mean2;
			result.clear();
			boolean oldSuppress = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			result.add(new GeoNumeric(cons, difference - me));
			result.add(new GeoNumeric(cons, difference + me));
			//result.add(new GeoNumeric(cons, difference));
			//result.add(new GeoNumeric(cons, me));
			//result.add(new GeoNumeric(cons, getDegreeOfFreedom(var1, var2, n1, n2, pooled)));
			
			cons.setSuppressLabelCreation(oldSuppress);



		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MathException e) {
			e.printStackTrace();
		}

	}

	// TODO Consider locusequability

}