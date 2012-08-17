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

import org.apache.commons.math.distribution.NormalDistributionImpl;


/**
 * 
 * 
 * @author G. Sturr
 */
public class AlgoZProportion2Estimate extends AlgoElement {


	private GeoNumeric proportion, n, proportion2, n_2, level; //input
	private GeoList  result;     // output   
	/**
	 * @param cons
	 * @param label
	 * @param proportion
	 * @param n
	 * @param proportion2 
	 * @param n_2 
	 * @param level 
	 */
	public AlgoZProportion2Estimate(Construction cons, String label, GeoNumeric proportion, GeoNumeric n, GeoNumeric proportion2, GeoNumeric n_2, GeoNumeric level) {
		super(cons);
		this.proportion = proportion;
		this.n = n;
		this.proportion2 = proportion2;
		this.n_2 = n_2;
		this.level = level;
		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		result.setLabel(label);
	}


	@Override
	public Algos getClassName() {
		return Algos.AlgoZProportionEstimate;
	}

	@Override
	protected void setInputOutput(){

		input = new GeoElement[5];
		input[0] = proportion;
		input[1] = n;
		input[2] = proportion2;
		input[3] = n_2;
		input[4] = level;


		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return {lower confidence limit, upper confidence limit}.
	 */
	public GeoList getResult() {
		return result;
	}

	@Override
	public final void compute() {


		double n1 = n.getDouble();		
		double phat1 = proportion.getDouble();
		double n2 = n_2.getDouble();		
		double phat2 = proportion2.getDouble();
		double cLevel = level.getDouble();

		NormalDistributionImpl normalDist = new NormalDistributionImpl(0, 1);

		double critZ = 0;

		try {
			critZ = normalDist.inverseCumulativeProbability((1 - cLevel) / 2);
		} catch (Exception e) {
			result.setUndefined();
			return;
		}

		double stat = phat1 - phat2;
		double se = Math.sqrt(phat1 * (1 - phat1) / n1 + phat2 * (1 - phat2) / n2);
		double z = Math.abs(critZ);
		double me = z * se;

		// put these results into the output list
		result.clear();
		result.add(new GeoNumeric(cons, stat - me));
		result.add(new GeoNumeric(cons, stat + me));

	}

	

	
}
