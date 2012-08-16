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
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;

import org.apache.commons.math.distribution.NormalDistributionImpl;


/**
 * 
 * 
 * @author G. Sturr
 */
public class AlgoZMeanTest extends AlgoElement {


	private GeoNumeric hypMean, mean, sd, n; //input
	private GeoList list; // input
	private GeoText tail; //input
	private GeoList  result;     // output   

	/**
	 * @param cons
	 * @param label
	 * @param mean
	 * @param sd 
	 * @param n
	 * @param hypMean
	 * @param tail
	 */
	public AlgoZMeanTest(Construction cons, String label, GeoNumeric mean, GeoNumeric sd, GeoNumeric n, GeoNumeric hypMean, GeoText tail) {
		super(cons);
		this.hypMean = hypMean;
		this.tail = tail;
		this.mean = mean;
		this.sd = sd;
		this.n = n;
		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		result.setLabel(label);
	}

	/**
	 * @param cons
	 * @param label
	 * @param mean
	 * @param sd 
	 * @param n
	 * @param hypMean
	 * @param tail
	 */
	public AlgoZMeanTest(Construction cons, String label, GeoList list, GeoNumeric sd, GeoNumeric hypMean, GeoText tail) {
		super(cons);
		this.hypMean = hypMean;
		this.tail = tail;
		this.list = list;
		this.sd = sd;
		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		result.setLabel(label);
	}


	@Override
	public Algos getClassName() {
		return Algos.AlgoZMeanTest;
	}

	@Override
	protected void setInputOutput(){

		if (list == null) {
			input = new GeoElement[5];
			input[0] = mean;
			input[1] = sd;
			input[2] = n;
			input[3] = hypMean;
			input[4] = tail;	
		} else {
			input = new GeoElement[4];
			input[0] = list;
			input[1] = sd;
			input[2] = hypMean;
			input[3] = tail;	

		}


		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return {P value, Z test statistic}
	 */
	public GeoList getResult() {
		return result;
	}

	@Override
	public final void compute() {

		String testType;
		if (tail.getTextString().equals("<")) {
			testType = "left";
		} else if (tail.getTextString().equals(">")) {
			testType = "right";
		} else if (StringUtil.isNotEqual(tail.getTextString())) {
			testType = "two";
		} else {
			result.setUndefined();
			return;			
		}

		double mean1;
		double n1;	

		if (list == null) {
			mean1 = mean.getDouble();
			n1 = n.getDouble();	
		} else {
			mean1 = list.mean();
			n1 = list.size();
		}
		
		double hyp = hypMean.getDouble();
		double sd1 = sd.getDouble();

		double se = sd1 / Math.sqrt(n1);
		double testStatistic = (mean1 - hyp) / se;

		NormalDistributionImpl normalDist = new NormalDistributionImpl(0, 1);
		double P=0;
		try {
			P = normalDist.cumulativeProbability(testStatistic);
		} catch (Exception e) {
			result.setUndefined();
			return;
		}

		if ("right".equals(testType)) {
			P = 1 - P;
		} else if ("two".equals(testType)) {
			if (testStatistic < 0) { 
				P = 2 * P; 
			} 
			else if (testStatistic > 0) { 
				P = 2 * ( 1 - P);
			}
		}

		// put these results into the output list
		result.clear();
		result.add(new GeoNumeric(cons, P));
		result.add(new GeoNumeric(cons,testStatistic));

	}

	

	

}
