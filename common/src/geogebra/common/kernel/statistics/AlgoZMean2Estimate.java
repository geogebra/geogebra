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
public class AlgoZMean2Estimate extends AlgoElement {


	private GeoNumeric  mean, sd, n, mean_2, sd_2, n_2, level; //input
	private GeoList list, list2;
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

	/**
	 * @param cons
	 * @param label
	 * @param list
	 * @param list2
	 * @param sd
	 * @param sd_2
	 * @param level
	 */
	public AlgoZMean2Estimate(Construction cons, String label,
			GeoList list,
			GeoList list2,
			GeoNumeric sd,
			GeoNumeric sd_2, GeoNumeric level) {
		super(cons);

		this.list = list;
		this.sd = sd;
		this.list2 = list2;
		this.sd_2 = sd_2;
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

		if (list == null) {
			input = new GeoElement[7];
			input[0] = mean;
			input[1] = sd;
			input[2] = n;
			input[3] = mean_2;
			input[4] = sd_2;
			input[5] = n_2;
			input[6] = level;
		} else {
			input = new GeoElement[5];
			input[0] = list;
			input[1] = sd;
			input[2] = list2;
			input[3] = sd_2;
			input[4] = level;			
		}


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
		
		if (!sd.isDefined() || !sd_2.isDefined() || !level.isDefined()) {
			result.setUndefined();
			return;			
		}

		double n1, n2, mean1, mean2;
		double sd1 = sd.getDouble();		
		double sd2 = sd_2.getDouble();		
		double cLevel = level.getDouble();

		if (list == null) {
			
			if (!n.isDefined() || !n_2.isDefined() || !mean.isDefined() || !mean_2.isDefined()) {
				result.setUndefined();
				return;
			}

			n1 = n.getDouble();		
			n2 = n_2.getDouble();		
			mean1 = mean.getDouble();
			mean2 = mean_2.getDouble();

		} else {
			
			if (!list.isDefined() || !list2.isDefined()) {
				result.setUndefined();
				return;
			}
			
			n1 = list.size();
			n2 = list2.size();

			mean1 = list.mean();
			mean2 = list2.mean();
		}


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

	

	
}
