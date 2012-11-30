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
import geogebra.common.util.StringUtil;

import org.apache.commons.math.distribution.NormalDistributionImpl;


/**
 * 
 * 
 * @author G. Sturr
 */
public class AlgoZProportionTest extends AlgoElement {


	private GeoNumeric hypPropertion, proportion, n; //input
	private GeoText tail; //input
	private GeoList  result;     // output   
	private double se;
	
	/**
	 * @param cons
	 * @param label
	 * @param proportion
	 * @param n
	 * @param hypProportion
	 * @param tail
	 */
	public AlgoZProportionTest(Construction cons, String label, GeoNumeric proportion, GeoNumeric n, GeoNumeric hypProportion, GeoText tail) {
		this(cons, proportion, n, hypProportion, tail);   
		result.setLabel(label);
	}


	/**
	 * @param cons
	 * @param label
	 * @param proportion
	 * @param n
	 * @param hypProportion
	 * @param tail
	 */
	public AlgoZProportionTest(Construction cons, GeoNumeric proportion, GeoNumeric n, GeoNumeric hypProportion, GeoText tail) {
		super(cons);
		this.hypPropertion = hypProportion;
		this.tail = tail;
		this.proportion = proportion;
		this.n = n;
		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		
	}
	

	@Override
	public Algos getClassName() {
		return Algos.AlgoZProportionTest;
	}

	@Override
	protected void setInputOutput(){

		input = new GeoElement[4];
		input[0] = proportion;
		input[1] = n;
		input[2] = hypPropertion;
		input[3] = tail;			


		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return {P value, Z test statistic}
	 */
	public GeoList getResult() {
		return result;
	}
	
	/**
	 * @return standard error
	 */
	public double getSE(){
		return se;
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

		double n1 = n.getDouble();		
		double hyp = hypPropertion.getDouble();
		double phat = proportion.getDouble();

		se = Math.sqrt(hyp*(1-hyp)/n1);
		double testStatistic = (phat - hyp)/se;

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
