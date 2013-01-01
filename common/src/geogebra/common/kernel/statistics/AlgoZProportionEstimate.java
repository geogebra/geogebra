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
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

import org.apache.commons.math.distribution.NormalDistributionImpl;


/**
 * 
 * 
 * @author G. Sturr
 */
public class AlgoZProportionEstimate extends AlgoElement {


	private GeoNumeric proportion, n, level; //input
	private GeoList  result;     // output   
	private double se;
	private double me;
	/**
	 * @param cons
	 * @param label
	 * @param proportion
	 * @param n
	 * @param level 
	 */
	public AlgoZProportionEstimate(Construction cons, String label, GeoNumeric proportion, GeoNumeric n, GeoNumeric level) {
		this(cons, proportion, n, level);
		result.setLabel(label);
	}

	public AlgoZProportionEstimate(Construction cons, GeoNumeric proportion, GeoNumeric n, GeoNumeric level) {
		super(cons);
		this.proportion = proportion;
		this.n = n;
		this.level = level;
		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
	}
	

	@Override
	public Commands getClassName() {
		return Commands.ZProportionEstimate;
	}

	@Override
	protected void setInputOutput(){

		input = new GeoElement[3];
		input[0] = proportion;
		input[1] = n;
		input[2] = level;


		setOnlyOutput(result);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return {lower confidence limit, upper confidence limit}.
	 */
	public GeoList getResult() {
		return result;
	}
	
	/**
	 * @return margin of error
	 */
	public double getME(){
		return me;
	}

	/**
	 * @return standard error
	 */
	public double getSE(){
		return se;
	}

	@Override
	public final void compute() {


		double n1 = n.getDouble();		
		double phat = proportion.getDouble();
		double cLevel = level.getDouble();

		NormalDistributionImpl normalDist = new NormalDistributionImpl(0, 1);

		double critZ = 0;

		try {
			critZ = normalDist.inverseCumulativeProbability((1 - cLevel) / 2);
		} catch (Exception e) {
			result.setUndefined();
			return;
		}

		se = Math.sqrt(phat * (1 - phat) / n1);
		double z = Math.abs(critZ);
		me = z * se;

		// put these results into the output list
		result.clear();
		result.add(new GeoNumeric(cons, phat - me));
		result.add(new GeoNumeric(cons, phat + me));

	}

	

	
}
