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
import geogebra.common.util.Unicode;

import org.apache.commons.math.distribution.NormalDistributionImpl;


/**
 * 
 * 
 * @author G. Sturr
 */
public class AlgoZProportion2Test extends AlgoElement {


	private GeoNumeric proportion, n, proportion2, n_2; //input
	private GeoText tail; //input
	private GeoList  result;     // output   
	
	/**
	 * @param cons
	 * @param label
	 * @param proportion
	 * @param n
	 * @param proportion2 
	 * @param n_2 
	 * @param tail
	 */
	public AlgoZProportion2Test(Construction cons, String label, GeoNumeric proportion, GeoNumeric n,GeoNumeric proportion2, GeoNumeric n_2, GeoText tail) {
		super(cons);
		this.tail = tail;
		this.proportion = proportion;
		this.n = n;
		this.proportion2 = proportion2;
		this.n_2 = n_2;
		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		result.setLabel(label);
	}


	@Override
	public Algos getClassName() {
		return Algos.AlgoZProportionTest;
	}

	@Override
	protected void setInputOutput(){

		input = new GeoElement[5];
		input[0] = proportion;
		input[1] = n;
		input[2] = proportion2;
		input[3] = n_2;
		input[4] = tail;			


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
		} else if (tail.getTextString().equals("!=") || tail.getTextString().equals(Unicode.NOTEQUAL)) {
			testType = "two";
		} else {
			result.setUndefined();
			return;			
		}

		double n1 = n.getDouble();		
		double phat1 = proportion.getDouble();
		double n2 = n_2.getDouble();		
		double phat2 = proportion2.getDouble();

		double x1 = phat1 * n1;
		double x2 = phat2 * n2;
		double phatTotal = (x1 + x2) / (n1 + n2);
		double se = Math.sqrt(phatTotal * (1 - phatTotal) * (1 / n1 + 1 / n2));
		double testStatistic = (phat1 - phat2) / se;

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

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		return false;
	}

}
