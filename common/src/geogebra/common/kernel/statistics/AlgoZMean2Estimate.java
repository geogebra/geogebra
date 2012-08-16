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
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

import org.apache.commons.math.distribution.NormalDistributionImpl;


/**
 * 
 * 
 * @author G. Sturr
 */
public class AlgoZMean2Estimate extends AlgoElement {


	private GeoNumeric  mean, sd, n, mean_2, sd_2, n_2, level; //input
	private GeoList  result;     // output   
	/**
	 * @param cons
	 * @param label
	 * @param mean 
	 * @param sd 
	 * @param n
	 * @param mean_2 
	 * @param sd_2 
	 * @param n_2 
	 * @param level 
	 */
	public AlgoZMean2Estimate(Construction cons, String label, GeoNumeric mean, GeoNumeric sd, GeoNumeric n, GeoNumeric mean_2, GeoNumeric sd_2, GeoNumeric n_2, GeoNumeric level) {
		super(cons);
		this.mean = mean;
		this.sd = sd;
		this.n = n;
		this.mean_2 = mean_2;
		this.sd_2 = sd_2;
		this.n_2 = n_2;
		this.level = level;
		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		result.setLabel(label);
	}


	@Override
	public Algos getClassName() {
		return Algos.AlgoZMean2Estimate;
	}

	@Override
	protected void setInputOutput(){

		input = new GeoElement[7];
		input[0] = mean;
		input[1] = sd;
		input[2] = n;
		input[3] = mean_2;
		input[4] = sd_2;
		input[5] = n_2;
		input[6] = level;


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
		double n2 = n_2.getDouble();		
		double sd1 = sd.getDouble();		
		double sd2 = sd_2.getDouble();		
		double mean1 = mean.getDouble();
		double mean2 = mean_2.getDouble();
		double cLevel = level.getDouble();

		NormalDistributionImpl normalDist = new NormalDistributionImpl(0, 1);

		double critZ = 0;

		try {
			critZ = normalDist.inverseCumulativeProbability((1 - cLevel) / 2);
		} catch (Exception e) {
			result.setUndefined();
			return;
		}

		double stat = mean1 - mean2;
		double se = Math.sqrt(sd1 * sd1 / n1 + sd2 * sd2 / n2);
		double z = Math.abs(critZ);
		double me = z * se;

		// put these results into the output list
		result.clear();
		result.add(new GeoNumeric(cons, stat - me));
		result.add(new GeoNumeric(cons, stat + me));

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
