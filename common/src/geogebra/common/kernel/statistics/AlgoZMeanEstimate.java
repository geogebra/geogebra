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
public class AlgoZMeanEstimate extends AlgoElement {


	private GeoNumeric  mean, sd, n, level; //input
	GeoList list;
	private GeoList  result;     // output
	private double me;
	
	

	/************************************************
	 * @param cons
	 * @param label
	 * @param mean 
	 * @param sd 
	 * @param n
	 * @param level 
	 */
	public AlgoZMeanEstimate(Construction cons, String label, GeoNumeric mean, GeoNumeric sd, GeoNumeric n, GeoNumeric level) {
		this(cons, mean, sd, n, level);
		result.setLabel(label);
	}

	/************************************************
	 * @param cons
	 * @param mean 
	 * @param sd 
	 * @param n
	 * @param level 
	 */
	public AlgoZMeanEstimate(Construction cons, GeoNumeric mean, GeoNumeric sd, GeoNumeric n, GeoNumeric level) {
		super(cons);
		this.mean = mean;
		this.sd = sd;
		this.n = n;
		this.level = level;
		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		
	}

	/**
	 * @param cons
	 * @param label
	 * @param list
	 * @param sd
	 * @param level
	 */
	public AlgoZMeanEstimate(Construction cons, String label,
			GeoList list,
			GeoNumeric sd,
			GeoNumeric level) {
		super(cons);

		this.list = list;
		this.sd = sd;
		this.level = level;
		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		result.setLabel(label);
	}


	@Override
	public Algos getClassName() {
		return Algos.AlgoZMeanEstimate;
	}

	@Override
	protected void setInputOutput(){

		if (list == null) {
			input = new GeoElement[4];
			input[0] = mean;
			input[1] = sd;
			input[2] = n;
			input[3] = level;
		} else {
			input = new GeoElement[3];
			input[0] = list;
			input[1] = sd;
			input[2] = level;			
		}


		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return margin of error
	 */
	public double getME() {
		return me;
	}
	
	/**
	 * @return {lower confidence limit, upper confidence limit}.
	 */
	public GeoList getResult() {
		return result;
	}

	@Override
	public final void compute() {

		if (!sd.isDefined() || !level.isDefined()) {
			result.setUndefined();
			return;			
		}

		double n1, mean1;
		double sd1 = sd.getDouble();		
		double cLevel = level.getDouble();

		if (list == null) {

			if (!n.isDefined() || !mean.isDefined() ) {
				result.setUndefined();
				return;
			}

			n1 = n.getDouble();		
			mean1 = mean.getDouble();

		} else {

			if (!list.isDefined()) {
				result.setUndefined();
				return;
			}

			n1 = list.size();

			mean1 = list.mean();
		}


		NormalDistributionImpl normalDist = new NormalDistributionImpl(0, 1);

		double critZ = 0;

		try {
			critZ = normalDist.inverseCumulativeProbability((1 - cLevel) / 2);
		} catch (Exception e) {
			result.setUndefined();
			return;
		}

		double se = sd1 / Math.sqrt(n1);
		double z = Math.abs(critZ);
		me = z * se;

		// put these results into the output list
		result.clear();
		result.add(new GeoNumeric(cons, mean1 - me));
		result.add(new GeoNumeric(cons, mean1 + me));

	}

	

	
}
