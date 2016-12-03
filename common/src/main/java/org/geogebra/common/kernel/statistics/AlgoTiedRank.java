/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math.stat.ranking.NaturalRanking;
import org.apache.commons.math.stat.ranking.TiesStrategy;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * List ranking using tie strategy.
 */

public class AlgoTiedRank extends AlgoElement {

	private GeoList inputList; // input
	private GeoList outputList; // output
	private int size;
	private NaturalRanking rankingAlgorithm;
	private double[] inputArray;
	private double[] outputArray;

	public AlgoTiedRank(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.TiedRank;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		setOnlyOutput(outputList);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return outputList;
	}

	@Override
	public final void compute() {

		size = inputList.size();
		if (!inputList.isDefined() || size == 0) {
			outputList.setUndefined();
			return;
		}

		inputArray = new double[size];

		// load input value array from geoList
		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo instanceof NumberValue) {
				NumberValue num = (NumberValue) geo;
				inputArray[i] = num.getDouble();
			} else {
				outputList.setUndefined();
				return;
			}
		}

		if (rankingAlgorithm == null)
			rankingAlgorithm = new NaturalRanking(TiesStrategy.AVERAGE);

		outputArray = rankingAlgorithm.rank(inputArray);

		// copy the ranks back into a list
		outputList.setDefined(true);
		outputList.clear();
		for (int i = 0; i < size; i++) {
			outputList.addNumber(outputArray[i], this);
		}
	}

	

}
