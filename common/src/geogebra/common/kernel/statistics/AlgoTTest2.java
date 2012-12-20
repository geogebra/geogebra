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
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.util.StringUtil;

import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.StatisticalSummaryValues;
import org.apache.commons.math.stat.inference.TTestImpl;

/**
 * Performs a two sample t-test of the difference of means.
 * 
 * @author G. Sturr
 */
public class AlgoTTest2 extends AlgoElement {

	
	private GeoList geoList0, geoList1; //input
	private GeoNumeric mean0, mean1, sd0, sd1, n0, n1; //input
	private GeoText tail; //input
	private GeoBoolean pooled; //input

	private GeoList  result;     // output   
	private TTestImpl tTestImpl;
	private double[] val0, val1;


	public AlgoTTest2(Construction cons, String label, GeoList geoList0, GeoList geoList1, 
			GeoText tail,  GeoBoolean pooled) {
		super(cons);
		this.geoList0 = geoList0;
		this.geoList1 = geoList1;
		this.tail = tail;
		this.pooled = pooled;
		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		result.setLabel(label);
	}

	public AlgoTTest2(Construction cons, String label, GeoNumeric mean0, GeoNumeric sd0, GeoNumeric n0, GeoNumeric mean1, 
			GeoNumeric sd1, GeoNumeric n1, GeoText tail, GeoBoolean pooled) {
		this(cons, mean0, sd0, n0, mean1, sd1, n1, tail, pooled);
		result.setLabel(label);
	}

	public AlgoTTest2(Construction cons, GeoNumeric mean0, GeoNumeric sd0, GeoNumeric n0, GeoNumeric mean1, 
			GeoNumeric sd1, GeoNumeric n1, GeoText tail, GeoBoolean pooled) {
		super(cons);
		this.mean0 = mean0;
		this.mean1 = mean1;
		this.sd0 = sd0;
		this.sd1 = sd1;
		this.n0 = n0;
		this.n1 = n1;
		this.tail = tail;
		this.pooled = pooled;
		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		
	}


	@Override
	public Algos getClassName() {
		return Algos.AlgoTTest2;
	}

	@Override
	protected void setInputOutput(){

		if(geoList0 != null){
			input = new GeoElement[4];
			input[0] = geoList0;
			input[1] = geoList1;
			input[2] = tail;
			input[3] = pooled;

		}else{
			input = new GeoElement[8];
			input[0] = mean0;
			input[1] = sd0;
			input[2] = n0;
			input[3] = mean1;
			input[4] = sd1;
			input[5] = n1;
			input[6] = tail;
			input[7] = pooled;
		}

		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return result;
	}


	private double adjustedPValue(double p, double testStatistic){

		// two sided test
		if (StringUtil.isNotEqual(tail.getTextString())) {
			return p;
		}
		
		// one sided test
		else if((tail.getTextString().equals(">") && testStatistic > 0)
				|| (tail.getTextString().equals("<") && testStatistic < 0))
			return p/2;
		else
			return 1 - p/2;
	}


	@Override
	public final void compute() {


		if (!(StringUtil.isInequality(tail.getTextString()))) {
			result.setUndefined();
			return;
		}

		double p, testStat;


		// sample data input
		if(input.length == 4){

			int size0 = geoList0.size();
			if(!geoList0.isDefined() || size0 < 2){
				result.setUndefined();	
				return;			
			}

			int size1 = geoList1.size();
			if(!geoList1.isDefined() || size1 < 2){
				result.setUndefined();	
				return;			
			}


			val0 = new double[size0];
			val1 = new double[size1];
			// load array from first sample
			for (int i=0; i < size0; i++) {
				GeoElement geo0 = geoList0.get(i);
				if (geo0.isNumberValue()) {
					NumberValue num = (NumberValue) geo0;
					val0[i] = num.getDouble();

				} else {
					result.setUndefined();
					return;
				}    		    		
			}   
			// load array from second sample
			for (int i=0; i < size1; i++) {
				GeoElement geo1 = geoList1.get(i);
				if (geo1.isNumberValue()) {
					NumberValue num = (NumberValue) geo1;
					val1[i] = num.getDouble();

				} else {
					result.setUndefined();
					return;
				}    		    		
			}   

			try {

				// get the test statistic and p
				if(tTestImpl == null)
					tTestImpl = new TTestImpl();
				
				if(pooled.getBoolean()){
					testStat = tTestImpl.homoscedasticT(val0, val1);
					p = tTestImpl.homoscedasticTTest(val0, val1);
					p = adjustedPValue(p, testStat);
				}else{
					testStat = tTestImpl.t(val0, val1);
					p = tTestImpl.tTest(val0, val1);
					p = adjustedPValue(p, testStat);
				}
				
				// put these results into the output list
				result.clear();
				result.add(new GeoNumeric(cons, p));
				result.add(new GeoNumeric(cons,testStat));


			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (MathException e) {
				e.printStackTrace();
			}


			// sample statistics input 
		}else{

			// check for valid stand. deviation and sample size
			if(sd0.getDouble() < 0 || sd1.getDouble() < 0 || n0.getDouble() < 2 || n1.getDouble() < 2){
				result.setUndefined();
				return;
			}



			StatisticalSummaryValues sumStats0 = new StatisticalSummaryValues(
					mean0.getDouble(), sd0.getDouble()*sd0.getDouble(), (long) n0.getDouble(), -1,-1,-1);
			StatisticalSummaryValues sumStats1 = new StatisticalSummaryValues(
					mean1.getDouble(), sd1.getDouble()*sd1.getDouble(), (long) n1.getDouble(), -1,-1,-1);

			try {
				
				// get the test statistic and p
				if(tTestImpl == null)
					tTestImpl = new TTestImpl();
				
				if(pooled.getBoolean()){
					testStat = tTestImpl.homoscedasticT(sumStats0, sumStats1);
					p = tTestImpl.homoscedasticTTest(sumStats0, sumStats1);
					p = adjustedPValue(p, testStat);
				}else{
					testStat = tTestImpl.t(sumStats0, sumStats1);
					p = tTestImpl.tTest(sumStats0, sumStats1);
					p = adjustedPValue(p, testStat);
				}
				
				// put these results into the output list
				result.clear();
				result.add(new GeoNumeric(cons, p));
				result.add(new GeoNumeric(cons,testStat));
		

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (MathException e) {
				e.printStackTrace();
			}

		}

	}

	// TODO Consider locusequability

}
